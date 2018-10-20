import auth.GoogleUser;
import com.google.cloud.datastore.Key;
import com.google.gson.Gson;
import course.CourseInfo;
import course.SimplifiedCourseInfo;
import course.StudentCourse;
import init.InitData;
import partner.StudentPartnership;
import student.StudentPublicInfo;

import java.util.*;


public class Ranking {

    int vectorSize = -1;
    public Map<Key, Double> courseW = null;
    public List<String> orderedWeights = null;


    MathVector getCourseVector(CourseInfo courseInfo) {
        Gson gson = new Gson();
        WeightVector v = gson.fromJson(courseInfo.getWeightVector(), WeightVector.class);
        Map<String, Double> hm = v.getWeightVector();

        MathVector retval = new MathVector(vectorSize);
        double[] vals = new double[vectorSize];

        for (int i = 0; i < vectorSize; i++) {
            vals[i] = hm.get(orderedWeights.get(i));
        }

        retval.fillFromArr(vals);
        return retval;
    }

    public void init(StudentCourse course) {
        CourseInfo info = CourseInfo.Companion.get(course.getKey());
        Gson gson = new Gson();
        WeightVector v = gson.fromJson(info.getWeightVector(), WeightVector.class);
        orderedWeights = new ArrayList<>(v.getWeightVector().keySet());
        Collections.sort(orderedWeights);
        vectorSize = orderedWeights.size();

        courseW = new HashMap<Key, Double>();
        for (SimplifiedCourseInfo c : CourseInfo.Companion.getAllSimplified()) {
            Key k = c.getKey();
            courseW.put(k, 1.0);
        }

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

    public MathVector compute_s_score(Key userKey, Key courseKey) {
        StudentPublicInfo studInfo = StudentPublicInfo.Companion.buildForGeneral(userKey);


        String skillStr = studInfo.getSkills();
        MathVector skillSetSum = SkillSetSum(skillStr);

        List<Key> pastCourses = studInfo.getPastCourses();

        MathVector pastCoursesSum = new MathVector(vectorSize);
        for (Key k : pastCourses) {
            CourseInfo courseInfo = CourseInfo.Companion.get(k);
            MathVector courseVector = getCourseVector(courseInfo);
            StudentCourse course = StudentCourse.Companion.getAllCoursesByStudentAndCourseId(userKey, k);

            courseVector.scalarProduct(course.getScore()); // * grade
            courseVector.scalarProduct(courseW.get(k)); // * course weight
            pastCoursesSum.addVector(courseVector);
        }

        List<Key> currCourses = studInfo.getCurrCourses();

        MathVector currCoursesSum = new MathVector(vectorSize);
        for (Key k : currCourses) {
            CourseInfo courseInfo = CourseInfo.Companion.get(k);
            MathVector courseVector = getCourseVector(courseInfo);
            StudentCourse course = StudentCourse.Companion.getAllCoursesByStudentAndCourseId(userKey, k);
            courseVector.scalarProduct(8.0); // * grade
            courseVector.scalarProduct(courseW.get(k)); // * course weight
            currCoursesSum.addVector(courseVector);
        }
        currCoursesSum.scalarProduct(0.5);

        return null;
    }
    public void getRankingForCourse(GoogleUser user, StudentCourse course) {
        InitData data = InitData.Companion.getByUser(user);
        Key userKey =  user.getKey();
        Key courseKey = course.getKey();
        user.getSkills();


    }
}
