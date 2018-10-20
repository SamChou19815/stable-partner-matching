import auth.GoogleUser;
import com.google.cloud.datastore.Key;
import course.StudentCourse;
import init.InitData;
import partner.StudentPartnership;
import student.StudentPublicInfo;

import java.util.List;
import java.util.Vector;

public class Ranking {

    public int SkillSetSum(String skills) {
        return 100; // TODO
    }

    public Vector compute_s_score(Key userKey, Key courseKey) {
        StudentPublicInfo info = StudentPublicInfo.Companion.buildForGeneral(userKey);

        List<Key> pastCourses = info.getPastCourses();
        List<Key> currCourses = info.getCurrCourses();
        String skills = info.getSkills();
        

    }
    public void getRankingForCourse(GoogleUser user, StudentCourse course) {
        InitData data = InitData.Companion.getByUser(user);
        Key userKey =  user.getKey();
        Key courseKey = course.getKey();
        user.getSkills()


    }
}
