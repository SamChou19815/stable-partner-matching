import java.util.List;
import java.util.Map;

public class Entry {

    Map<String, Double> keywords;
    Map<String, Double> categories;
    String title;
    String subject;
    String catalogNbr;
    String desc;

    public Entry(String t, String s, String nbr, String dsc, Map<String, Double> kw, Map<String, Double> cg) {
        title = t;
        subject = s;
        catalogNbr = nbr;
        desc = dsc;
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

    public String getDescription() { return desc; }

    public Map<String, Double> getKeywords() {
        return keywords;
    }

    public Map<String, Double> getCategories() {
        return categories;
    }


}
