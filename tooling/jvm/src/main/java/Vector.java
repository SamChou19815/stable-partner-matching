import java.util.Map;

public class Vector {
    String subject;
    String code;
    String title;
    String description;
    Map<String, Double> weightVector;

    Vector(String subj, String nbr, String ttl, String dsc, Map<String, Double> w) {
        subject = subj;
        code = nbr;
        title = ttl;
        description = dsc;
        weightVector = w;
    }




}
