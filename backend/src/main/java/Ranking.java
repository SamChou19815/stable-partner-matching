import auth.GoogleUser;
import com.google.cloud.datastore.Key;
import course.StudentCourse;
import init.InitData;
import partner.StudentPartnership;

import java.util.List;
import java.util.Vector;

public class Ranking {

    public int SkillSetSum(String skills) {
        return 100; // TODO
    }

    public Vector compute_s_score(Key userKey) {

    }
    public void getRankingForCourse(GoogleUser user, StudentCourse course) {
        InitData data = InitData.Companion.getByUser(user);
        Key userKey =  user.getKey();
        List<Key> pastCourses = StudentCourse.Companion.getAllPastCourseKeys(userKey);
        List<Key> currCourses = StudentCourse.Companion.getAllCurrCourseKeys(userKey);

    }
}
