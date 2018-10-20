import java.util.List;
import java.util.Map;

public class Entry {

    public Map<String, Double> keywords;
    public Map<String, Double> categories;
    public String title;
    public String subject;
    public String catalogNbr;

    public Entry(String t, String s, String nbr, Map<String, Double> kw, Map<String, Double> cg) {
        title = t;
        subject = s;
        catalogNbr = nbr;
        keywords = kw;
        categories = cg;
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

    public Map<String, Double> getKeywords() {
        return keywords;
    }

    public Map<String, Double> getCategories() {
        return categories;
    }


}
