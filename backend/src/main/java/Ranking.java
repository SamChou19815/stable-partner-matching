import auth.GoogleUser;
import com.google.cloud.datastore.Key;
import course.CourseInfo;
import course.StudentCourse;
import freetime.FreeTimeInterval;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import student.StudentPublicInfo;

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
public final class Ranking {
    
    private final int vectorSize;
    private final List<String> orderedWeights;
    private final GoogleUser user;
    private final MathVector courseWeightVector;
    
    public Ranking(@NotNull GoogleUser user, @NotNull CourseInfo courseInfo) {
        Map<String, Double> rawWeightVector = courseInfo.getWeightVectorMap();
        orderedWeights = new ArrayList<>(rawWeightVector.keySet());
        Collections.sort(orderedWeights);
        vectorSize = orderedWeights.size();
        courseWeightVector = getCourseWeightVector(courseInfo);
        this.user = user;
    }
    
    /**
     * Returns a computed course weight map from the raw course information.
     *
     * @param courseInfo the raw course information.
     * @return the computed course weight vector.
     */
    private MathVector getCourseWeightVector(CourseInfo courseInfo) {
        Map<String, Double> v = courseInfo.getWeightVectorMap();
        MathVector ans = new MathVector(vectorSize);
        double[] values = new double[vectorSize];
        for (int i = 0; i < vectorSize; i++) {
            values[i] = v.get(orderedWeights.get(i));
        }
        ans.updateFromArray(values);
        return ans;
    }
    
    /**
     * Compute the score vector for skills.
     * The function is pure.
     *
     * @param skillStr the string that encodes a list of skills.
     * @return a math vector of the skill score sum.
     */
    @NotNull
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
     * @return a math vector of the skill score sum.
     */
    @NotNull
    private MathVector computeCourseSetSum(Key userKey, List<Key> courseKeySet) {
        MathVector coursesSum = new MathVector(vectorSize);
        for (Key courseKey : courseKeySet) {
            CourseInfo courseInfo = CourseInfo.getCached(courseKey);
            MathVector courseVector = getCourseWeightVector(courseInfo);
            StudentCourse course = StudentCourse.getAllCoursesByStudentAndCourseId(
                    userKey, courseKey);
            if (course == null) {
                throw new Error();
            }
            Long scoreOpt = course.getScore();
            double score = scoreOpt == null? 3: scoreOpt;
            courseVector.scalarProduct(score); // * grade
            courseVector.vectorProduct(courseWeightVector); // * specified course's weight
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
    private double computePartnerScalarScore(MathVector userCompetenceVector,
                                             MathVector partnerCompetenceVector) {
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
     * Compute and returns the competence vector for one user with userKey.
     * The function is pure.
     *
     * @param userKey the key of the user of interest.
     * @return the computed competence vector.
     */
    @NotNull
    private MathVector computeCompetenceVector(Key userKey) {
        StudentPublicInfo studentInfo = StudentPublicInfo.buildForGeneral(userKey, true);
        // Compute Skill Sum
        String skillStr = studentInfo.getSkills();
        MathVector skillSetSum = computeSkillSetSum(skillStr);
        // Compute Past Courses Sum
        MathVector pastCoursesSum = computeCourseSetSum(userKey, studentInfo.getPastCourses());
        // Compute Current Courses Sum
        MathVector currCoursesSum = computeCourseSetSum(userKey, studentInfo.getCurrCourses());
        currCoursesSum.scalarProduct(0.5);
        // Add all the things together
        skillSetSum.addVector(pastCoursesSum);
        skillSetSum.addVector(currCoursesSum);
        return skillSetSum;
    }
    
    @NotNull
    public final List<StudentPublicInfo> getRankingForCourse() {
        // Prepare data for user.
        MathVector userCompetenceVector = computeCompetenceVector(user.getKeyNotNull());
        // Compute all partner competence and matching score
        List<Key> potentialPartnerKeys = GoogleUser.getAllOtherUserKeys(user);
        List<Pair<Key, Double>> partnerKeyScorePairList = new ArrayList<>();
        for (Key potentialPartnerKey : potentialPartnerKeys) {
            MathVector partnerCompetenceVector =
                    computeCompetenceVector(potentialPartnerKey);
            GoogleUser partnerUser = GoogleUser.getByKey(potentialPartnerKey);
            double s = computePartnerScalarScore(userCompetenceVector, partnerCompetenceVector) +
                    FreeTimeInterval.totalOverlap(user.getFreeTimes(), partnerUser.getFreeTimes());
            partnerKeyScorePairList.add(new Pair<>(potentialPartnerKey, s));
        }
        // Final Sorting
        return partnerKeyScorePairList.stream()
                .sorted((o1, o2) -> Double.compare(o2.getSecond(), o1.getSecond()))
                .map(pair -> StudentPublicInfo.buildForGeneral(pair.getFirst(), false))
                .collect(Collectors.toList());
    }
    
}
