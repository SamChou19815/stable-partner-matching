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
    public MathVector emptyVector = null;


    MathVector getCourseVector(CourseInfo courseInfo) {
        Gson gson = new Gson();
        WeightVector v = gson.fromJson(courseInfo.getWeightVector(), WeightVector.class);
        Map<String, Double> hm = v.getWeightVector();
        List<String> keys = new ArrayList<>(hm.keySet());

        vectorSize = keys.size();

        MathVector retval = new MathVector(vectorSize);
        Collections.sort(keys); // TODO sorting to maintain consistent order necessary?
        double[] vals = new double[vectorSize];
        for (int i = 0; i < vectorSize; i++) {
            vals[i] = hm.get(keys.get(i));
        }
        retval.fillFromArr(vals);
        return retval;
    }

    public void init(StudentCourse course) {
        CourseInfo info = CourseInfo.Companion.get(course.getKey());
        Gson gson = new Gson();
        WeightVector v = gson.fromJson(info.getWeightVector(), WeightVector.class);
        int numWeights = v.getWeightVector().keySet().size();

        emptyVector = new MathVector(numWeights);
        emptyVector.setAll(0.0);

        courseW = new HashMap<Key, Double>();
        for (SimplifiedCourseInfo c : CourseInfo.Companion.getAllSimplified()) {
            Key k = c.getKey();
            courseW.put(k, 1.0);
        }

    }

    public MathVector SkillSetSum(String skillStr) {
        String[] skills = skillStr.split(", ");


        return null; // TODO
    }

    public MathVector compute_s_score(Key userKey, Key courseKey) {
        StudentPublicInfo studInfo = StudentPublicInfo.Companion.buildForGeneral(userKey);


        String skillStr = studInfo.getSkills();
        MathVector skillSetSum = SkillSetSum(skillStr);

        List<Key> pastCourses = studInfo.getPastCourses();

        MathVector pastCoursesSum = new MathVector
        for (Key k : pastCourses) {
            CourseInfo courseInfo = CourseInfo.Companion.get(k);
            MathVector courseVector = getCourseVector(courseInfo);
            StudentCourse course = StudentCourse.Companion.get
        }

//        List<Key> currCourses = info.getCurrCourses();

        return null;
    }
    public void getRankingForCourse(GoogleUser user, StudentCourse course) {
        InitData data = InitData.Companion.getByUser(user);
        Key userKey =  user.getKey();
        Key courseKey = course.getKey();
        user.getSkills();


    }
}
