import java.util.Map;

public class Vector {
    int courseId;
    Map<String, Double> weights;

    Vector(int id, Map<String, Double> w) {
        courseId = id;
        weights = w;
    }

    public int getCourseId() {
        return courseId;
    }

    public Map<String, Double> weights() {
        return weights;
    }


}
