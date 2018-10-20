import java.util.Map;

public class WeightVector {
    String subject;
    String code;
    String title;
    String description;
    Map<String, Double> weightVector;

    WeightVector(String subj, String nbr, String ttl, String dsc, Map<String, Double> w) {
        subject = subj;
        code = nbr;
        title = ttl;
        description = dsc;
        weightVector = w;
    }

    public Map<String, Double> getWeightVector() {
        return weightVector;
    }


}
