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
        System.out.println("WELCOME TO COURSE READER");
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



        Scanner reader = new Scanner(System.in);

        while (true) {
            System.out.println("\nEnter 'k' for a list of keywords, 'c' for a list of categories");
            System.out.println("Enter 'find [keyword]' for a list of courses with specified [keyword]. Enter 'cat [category]' for a list of courses with specified [category].");
            System.out.println("Enter 'exit' to end the program.\n");
            String s = reader.nextLine();

            if (s.equals("k")) {
                for (String kw : keywordSet) {
                    System.out.println(kw);
                }
            } else if (s.equals("c")) {
                for (String ca : categorySet) {
                    System.out.println(ca);
                }
            } else if (s.startsWith("find")) {
                String[] in = s.split(" ");

                String tgt = null;
                for (int i = 1; i < in.length; i++) {
                    tgt += in[i] + " ";
                }
                tgt = tgt.trim();
                if (!keywordSet.contains(tgt)) {
                    System.out.println("None of the courses have this keyword.");
                } else {
                    for (Entry e : entryTable) {
                        if (e.getKeywords().contains(tgt)) {
                            System.out.println(e.getSubj() + " " + e.getCatalogNbr() + ": " + e.getTitle());
                        }
                    }
                }
            } else if (s.startsWith("cat")) {
                String[] in = s.split(" ");

                String tgt = null;
                for (int i = 1; i < in.length; i++) {
                    tgt += in[i] + " ";
                }
                tgt = tgt.trim();
                if (!categorySet.contains(tgt)) {
                    System.out.println("None of the courses have this category.");
                } else {
                    for (Entry e : entryTable) {
                        if (e.getCategories().contains(tgt)) {
                            System.out.println(e.getSubj() + " " + e.getCatalogNbr() + ": " + e.getTitle());
                        }
                    }
                }
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

        List<Entry> entryTable = createEntryTable(courseArray);

        prompter(entryTable);

    }

}
