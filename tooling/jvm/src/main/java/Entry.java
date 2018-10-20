import java.util.List;

public class Entry {

    public List<String> keywords;
    public List<String> categories;
    public String title;
    public String subject;
    public String catalogNbr;

    public Entry(String t, String s, String nbr, List<String> kw, List<String> cg) {
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

    public List<String> getKeywords() {
        return keywords;
    }

    public List<String> getCategories() {
        return categories;
    }


}
