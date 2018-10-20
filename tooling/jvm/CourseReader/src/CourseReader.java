import java.io.*;
import java.util.ArrayList;
import java.util.List;

import api.cornell.data.classes.*;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class CourseReader {

    public static class Entry {

        List<String> keywords;
        List<String> categories;
        String title;
        String desc;
        String subject;
        String catalogNbr;
        Entry(String title, String subj, String nbr, String desc, List<String> keywords, List<String> categories) {
            title = title;
            subject = subj;
            catalogNbr = nbr;
            desc = desc;
            keywords = keywords;
            categories = categories;
        }
    }

    private static List<Entry> createEntryTable(Course[] courseArray) {
        List<Entry> entryTable = new ArrayList<Entry>();
        for (int i = 0; i < courseArray.length; i++) {
            Course c = courseArray[i];
            String title = c.getLongTitle();
            String subject = c.getSubject().toString();
            String catalogNbr = c.getCatalogNumber();
            String desc = c.getDescription();
            List<String> keywords = null;
            try {
                keywords = Analyze.analyzeEntitiesText(title + ". " + desc);
            } catch (Exception e) {
                System.out.println("Could not analyze entities for: " + title);
                System.out.println(e.getMessage());
            }
            List<String> categories = null;
            try {
                categories = Analyze.analyzeCategoriesText(title + ". " + desc);
            } catch (Exception e) {
                System.out.println("Could not analyze categories for: " + title);
                System.out.println(e.getMessage());
            }
            entryTable.add(new Entry(title, subject, catalogNbr, desc, keywords, categories));
        }
        return entryTable;

    }

    public void main(String[] args) throws FileNotFoundException {
        String path = "singlecourse.json";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        Gson gson = new Gson();
        Course[] courseArray = gson.fromJson(bufferedReader, Course[].class);

        List<Entry> entryTable = createEntryTable(courseArray);




    }

}
