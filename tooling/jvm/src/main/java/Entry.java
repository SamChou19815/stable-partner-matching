import java.util.List;
import java.util.Map;

public class Entry {

    Map<String, Double> keywords;
    Map<String, Double> categories;
    String title;
    String subject;
    String catalogNbr;
    int courseId = -1;

    public Entry(String t, String s, String nbr, int id, Map<String, Double> kw, Map<String, Double> cg) {
        title = t;
        subject = s;
        catalogNbr = nbr;
        keywords = kw;
        categories = cg;
        courseId = id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubj() {
        return subject;
    }

    public String getCatalogNbr() {
        return catalogNbr;
    }

    public int getCourseId() {
        return courseId;
    }
    public Map<String, Double> getKeywords() {
        return keywords;
    }

    public Map<String, Double> getCategories() {
        return categories;
    }


}
