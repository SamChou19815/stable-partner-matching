import auth.GoogleUser;
import com.google.cloud.datastore.Key;
import com.google.gson.Gson;
import course.CourseInfo;
import course.StudentCourse;
import init.InitData;
import partner.StudentPartnership;
import student.StudentPublicInfo;

import java.util.List;
import java.util.Vector;


public class Ranking {

    public Vector<Double> courseW;
    public Vector<Double> emptyVector;

    public void init(StudentCourse course) {
        CourseInfo info = CourseInfo.Companion.get(course.getKey());
        Gson gson = new Gson();
        gson.fromJson(info.getWeightVector(), Vector.class);
    }
    public int SkillSetSum(String skills) {
        return 100; // TODO
    }

    public Vector compute_s_score(Key userKey, Key courseKey) {
        StudentPublicInfo studInfo = StudentPublicInfo.Companion.buildForGeneral(userKey);

        String skillStr = studInfo.getSkills();
        String[] skills = skillStr.split(", ");


        List<Key> pastCourses = studInfo.getPastCourses();
        for (Key k : pastCourses) {
            CourseInfo courseInfo = CourseInfo.Companion.get(k);
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
