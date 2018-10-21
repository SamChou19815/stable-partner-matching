import auth.GoogleUser;
import com.google.cloud.datastore.Key;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import course.CourseInfo;
import course.StudentCourse;
import freetime.FreeTimeInterval;
import kotlin.Pair;
import student.StudentPublicInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ranking is the utility class used to decide the rankings between students.
 * <p>
 * Important Concepts:
 * - Skill Vector: It is the vector that represents the different amount of skills in different
 * dimensions. It is computed from course descriptions and used to compute competence vector.
 * - Competence Vector: It is the vector that shows how the student is specialized in some areas.
 * It is the vector s_i in the design document.
 * - Matching Score: It is a scalar that shows how good is a matching between two students i and j.
 * It is the scalar S_ij in the design document.
 */
public class Ranking {
    
    /**
     * The globally used gson.
     */
    private static final Gson gson = new Gson();
    /**
     * The globally used type for weight vector.
     */
    private static final Type weightVectorType = new TypeToken<Map<String, Double>>() {}.getType();
    
    private int vectorSize = -1;
    private List<String> orderedWeights = null;
    private Key userKey;
    
    private MathVector getCourseVector(CourseInfo courseInfo) {
        Map<String, Double> v = gson.fromJson(courseInfo.getWeightVector(), weightVectorType);
        MathVector retval = new MathVector(vectorSize);
        double[] vals = new double[vectorSize];
        for (int i = 0; i < vectorSize; i++) {
            vals[i] = v.get(orderedWeights.get(i));
        }
        retval.updateFromArray(vals);
        return retval;
    }
    
    private void init(StudentCourse course) {
        CourseInfo info = CourseInfo.Companion.get(course.getCourseId());
        Map<String, Double> v = gson.fromJson(info.getWeightVector(), weightVectorType);
        orderedWeights = new ArrayList<>(v.keySet());
        Collections.sort(orderedWeights);
        vectorSize = orderedWeights.size();
    }
    
    /**
     * Compute the score vector for skills.
     * The function is pure.
     *
     * @param skillStr the string that encodes a list of skills.
     * @return a math vector of the skill score sum.
     */
    private MathVector computeSkillSetSum(String skillStr) {
        String[] skills = skillStr.split(", ");
        Set<String> skillSet = new HashSet<>(Arrays.asList(skills));
        MathVector ans = new MathVector(vectorSize);
        double[] skillVector = new double[vectorSize];
        for (int i = 0; i < vectorSize; i++) {
            if (skillSet.contains(orderedWeights.get(i))) {
                skillVector[i] = 1.0; // smoothing
            }
        }
        ans.updateFromArray(skillVector);
        return ans;
    }
    
    /**
     * Compute the score vector for courses for a given user.
     * The function is pure.
     *
     * @param userKey the key of the user of interest.
     * @param courseKeySet the course key set to do the computation on.
     * @param courseWeight the course wright to consider.
     * @return a math vector of the skill score sum.
     */
    private MathVector computeCourseSetSum(Key userKey, List<Key> courseKeySet,
                                           MathVector courseWeight) {
        MathVector coursesSum = new MathVector(vectorSize);
        for (Key courseKey : courseKeySet) {
            CourseInfo courseInfo = CourseInfo.Companion.get(courseKey);
            MathVector courseVector = getCourseVector(courseInfo);
            StudentCourse course = StudentCourse.Companion.getAllCoursesByStudentAndCourseId(
                    userKey, courseKey);
            
            courseVector.scalarProduct(course.getScore()); // * grade
            courseVector.vectorProduct(courseWeight); // * specified course's weight
            coursesSum.addVector(courseVector);
        }
        return coursesSum;
    }
    
    /**
     * Compute the scalar score between user and a potential that indicates the goodness of
     * matching.
     * The function is pure.
     *
     * @param userCompetenceVector the competence vector of the user that wants to find the partner.
     * @param partnerCompetenceVector the competence vector of the potential partner.
     * @return the scalar matching goodness score.
     */
    private double computePartnerScalarScore(
            MathVector userCompetenceVector, MathVector partnerCompetenceVector
    ) {
        MathVector s1 = userCompetenceVector.copy();
        MathVector s2 = partnerCompetenceVector.copy();
        // Formula: exp(-abs(s1 + s2 * -1)) = exp(-abs(s1 - s2))
        s2.scalarProduct(-1.0); // To produce -s2.
        s1.addVector(s2);
        s1.abs();
        s1.scalarProduct(-1.0);
        s1.exp();
        // Formula: (penalizing vector * s1) .* s2
        s1.vectorProduct(userCompetenceVector);
        return s1.dotProduct(partnerCompetenceVector);
    }
    
    /**
     * Compute and returns the competence vector for one user with userKey under a given
     * courseWeight.
     * The function is pure.
     *
     * @param userKey the key of the user of interest.
     * @param courseWeight the weight of the course.
     * @return the computed competence vector.
     */
    private MathVector computeCompetenceVector(Key userKey, MathVector courseWeight) {
        StudentPublicInfo studentInfo = StudentPublicInfo.Companion.buildForGeneral(
                userKey, true);
        // Compute Skill Sum
        String skillStr = studentInfo.getSkills();
        MathVector skillSetSum = computeSkillSetSum(skillStr);
        // Compute Past Courses Sum
        MathVector pastCoursesSum =
                computeCourseSetSum(userKey, studentInfo.getPastCourses(), courseWeight);
        // Compute Current Courses Sum
        MathVector currCoursesSum =
                computeCourseSetSum(userKey, studentInfo.getCurrCourses(), courseWeight);
        currCoursesSum.scalarProduct(0.5);
        // Add all the things together
        skillSetSum.addVector(pastCoursesSum);
        skillSetSum.addVector(currCoursesSum);
        return skillSetSum;
    }
    
    public final List<Key> getRankingForCourse(GoogleUser user, StudentCourse course) {
        if (orderedWeights == null) {
            init(course);
        }
        CourseInfo courseInfo = CourseInfo.Companion.get(course.getCourseId());
        MathVector courseWeightVector = getCourseVector(courseInfo);
        
        userKey = user.getKeyNotNull();
        
        MathVector userCompetenceVector = computeCompetenceVector(userKey, courseWeightVector);
        
        List<Key> potentialPartnerKeys = GoogleUser.Companion.getAllOtherUserKeys(user);
        List<Pair<Key, Double>> partnerKeyScorePairList = new ArrayList<>();
        for (Key potentialPartnerKey : potentialPartnerKeys) {
            MathVector partnerCompetenceVector =
                    computeCompetenceVector(potentialPartnerKey, courseWeightVector);
            GoogleUser partnerUser = GoogleUser.Companion.getByKey(potentialPartnerKey);
            double s = computePartnerScalarScore(userCompetenceVector, partnerCompetenceVector) +
                    FreeTimeInterval.totalOverlap(user.getFreeTimes(), partnerUser.getFreeTimes());
            partnerKeyScorePairList.add(new Pair<>(potentialPartnerKey, s));
        }
        // SORTING
        return partnerKeyScorePairList
                .stream()
                .sorted((o1, o2) -> Double.compare(o2.getSecond(), o1.getSecond()))
                .map(Pair::getFirst)
                .collect(Collectors.toList());
    }
    
}
