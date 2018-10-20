import java.io.*;
import java.util.ArrayList;
import java.util.List;

import api.cornell.data.classes.*;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

public class CourseReader {



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
            System.out.println(title);
            entryTable.add(new Entry(title, subject, catalogNbr, desc, keywords, categories));
        }
        return entryTable;

    }

    public static void prompter(List<Entry> entryTable) {
        System.out.println("Initializing keyword and category sets");

        Set<String> keywordSet = new HashSet<String>();
        Set<String> categorySet = new HashSet<String>();

        for (Entry e : entryTable) {
            for (String kw : e.getKeywords()) {
                keywordSet.add(kw);

            }
            for (String ca : e.getCategories()) {
                categorySet.add(ca);
            }
        }
        System.out.println("Initialized sets");

        for
        System.out.println("Enter 'k' for a list of keywords, 'c' for a list of categories");
        System.out.println("Enter 'find [keyword]' for a list of courses with specified [keyword]");
        System.out.println("Enter 'cat [category]' for a list of courses with specified [category]");
        System.out.println("Enter 'exit' to end the program.");

        Scanner reader = new Scanner(System.in);

        while (true) {
            String s = reader.next();

            if (s.equals("k")) {
                for (String kw : keywordSet) {
                    System.out.println(kw);
                }
            } else if (s.equals("c")) {
                for (String ca : categorySet) {
                    System.out.println(ca);
                }
            } else if (s.startsWith("find")) {
                String[]  s.split(" ")
            } else if (s.startsWith("cat")) {

            } else if (s.equals("exit")) {
                return;
            } else {
                System.out.println("Invalid command.");
            }
        }
    }
    public static void main(String[] args) throws FileNotFoundException {
        String path = "single.json";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        Gson gson = new Gson();
        Course[] courseArray = gson.fromJson(bufferedReader, Course[].class);

        System.out.println(courseArray[0]);
        Course c = courseArray[0];
        System.out.println(c.getSubject().toString());
        List<Entry> entryTable = createEntryTable(courseArray);

        Entry e = entryTable.get(0);
        List<String> kw = e.getKeywords();
        for (String s : kw) {
            System.out.println(s);
        }

        List<String> cat = e.getCategories();
        for (String ca : cat) {
            System.out.println(ca);
        }
    }

}
