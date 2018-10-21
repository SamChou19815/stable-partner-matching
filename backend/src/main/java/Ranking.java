import auth.GoogleUser;
import com.google.cloud.datastore.Key;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import course.CourseInfo;
import course.StudentCourse;
import freetime.FreeTimeInterval;
import init.InitData;
import kotlin.Pair;
import student.StudentPublicInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class Ranking {
    
    int vectorSize = -1;
    public List<String> orderedWeights = null;
    
    MathVector getCourseVector(CourseInfo courseInfo) {
        Gson gson = new Gson();
        Map<String, Double> v = gson.fromJson(
                courseInfo.getWeightVector(), new TypeToken<Map<String, Double>>() {}.getType()
        );
        MathVector retval = new MathVector(vectorSize);
        double[] vals = new double[vectorSize];
        for (int i = 0; i < vectorSize; i++) {
            vals[i] = v.get(orderedWeights.get(i));
        }
        retval.fillFromArr(vals);
        return retval;
    }
    
    public void init(StudentCourse course) {
        CourseInfo info = CourseInfo.Companion.get(course.getCourseId());
        Gson gson = new Gson();
        Map<String, Double> v = gson.fromJson(
                info.getWeightVector(), new TypeToken<Map<String, Double>>() {}.getType()
        );
        orderedWeights = new ArrayList<>(v.keySet());
        Collections.sort(orderedWeights);
        vectorSize = orderedWeights.size();
    }
    
    MathVector SkillSetSum(String skillStr) {
        String[] skills = skillStr.split(", ");
        Set<String> skillSet = new HashSet<String>();
        MathVector retval = new MathVector(vectorSize);
        
        double[] skillvector = new double[vectorSize];
        
        for (int i = 0; i < vectorSize; i++) {
            if (skillSet.contains(orderedWeights.get(i))) {
                skillvector[i] = 1.0; // smoothing
            }
        }
        
        retval.fillFromArr(skillvector);
        return retval;
    }
    
    public double computePartnerSscore(MathVector userV, MathVector partnerV) {
        MathVector s1 = userV.duplicate();
        MathVector s2 = partnerV.duplicate();
        s2.scalarProduct(-1.0);
        s1.addVector(s2);
        s1.abs();
        // s1.exp();
        
        s1.vectorProduct(userV);
        return s1.dotProduct(partnerV);
    }
    
    public MathVector compute_s_score(Key userKey, MathVector courseW) {
        StudentPublicInfo studInfo = StudentPublicInfo.Companion.buildForGeneral(userKey, true);
        
        String skillStr = studInfo.getSkills();
        MathVector skillSetSum = SkillSetSum(skillStr);
        
        List<Key> pastCourses = studInfo.getPastCourses();
        
        MathVector pastCoursesSum = new MathVector(vectorSize);
        for (Key k : pastCourses) {
            CourseInfo courseInfo = CourseInfo.Companion.get(k);
            MathVector courseVector = getCourseVector(courseInfo);
            StudentCourse course = StudentCourse.Companion.getAllCoursesByStudentAndCourseId(userKey, k);
            
            courseVector.scalarProduct(course.getScore()); // * grade
            courseVector.vectorProduct(courseW); // * specified course's weight
            pastCoursesSum.addVector(courseVector);
        }
        
        List<Key> currCourses = studInfo.getCurrCourses();
        
        MathVector currCoursesSum = new MathVector(vectorSize);
        for (Key k : currCourses) {
            CourseInfo courseInfo = CourseInfo.Companion.get(k);
            MathVector courseVector = getCourseVector(courseInfo);
            StudentCourse course = StudentCourse.Companion.getAllCoursesByStudentAndCourseId(userKey, k);
            courseVector.scalarProduct(8.0); // * grade
            courseVector.vectorProduct(courseW); // * specified course's weight
            currCoursesSum.addVector(courseVector);
        }
        currCoursesSum.scalarProduct(0.5);
        
        skillSetSum.addVector(pastCoursesSum);
        skillSetSum.addVector(currCoursesSum);
        
        return skillSetSum;
        
    }
    
    public final List<Key> getRankingForCourse(List<InitData> data, GoogleUser user, StudentCourse course) {
        if (orderedWeights == null) {
            init(course);
        }
        
        CourseInfo courseInfo = CourseInfo.Companion.get(course.getCourseId());
        MathVector courseW = getCourseVector(courseInfo);
        
        Key userKey = user.getKey();
        
        MathVector userScore = compute_s_score(userKey, courseW);
        
        List<Key> partners = new ArrayList<Key>();
        List<Double> scores = new ArrayList<Double>();
        
        List<Key> possPartners = GoogleUser.Companion.getAllOtherUserKeys(user);
        List<Pair<Key, Double>> partnerKeyScorePairList = new ArrayList<>();
        for (Key k : possPartners) {
            MathVector partnerScore = compute_s_score(k, courseW);
            GoogleUser partnerUser = GoogleUser.Companion.getByKey(k);
            double s = computePartnerSscore(userScore, partnerScore) +
                    FreeTimeInterval.totalOverlap(user.getFreeTimes(), partnerUser.getFreeTimes());
            /*
            partners.add(k);
            scores.add(s);
            */
            partnerKeyScorePairList.add(new Pair<>(k, s));
        }
        // SORTING
        /*
        partners.sort(Comparator.comparing(s -> scores.get(partners.indexOf(s))));
        data.sort(Comparator.comparing(s -> -partners.indexOf(s.getProfile().getKey())));
        */
        return partnerKeyScorePairList
                .stream()
                .sorted((o1, o2) -> Double.compare(o2.getSecond(), o1.getSecond()))
                .map(Pair::getFirst)
                .collect(Collectors.toList());
    }
}
