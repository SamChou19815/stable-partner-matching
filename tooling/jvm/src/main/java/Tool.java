import api.cornell.data.classes.Course;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Tool {


    public static void prompter(List<Entry> entryTable) {
        System.out.println("WELCOME TO COURSE READER");
        System.out.println("Initializing keyword and category sets...");

//        Set<String> keywordSet = new HashSet<String>();
        Map<String, Integer> keywordFreq = new HashMap<String, Integer>();

        Map<String, Integer> categoryFreq = new HashMap<String, Integer>();
//        Set<String> categorySet = new HashSet<String>();

        float totalK = 0f;
        float totalC = 0f;
        for (Entry e : entryTable) {
            for (String kw : e.getKeywords().keySet()) {
                totalK += 1;
                Integer curr = keywordFreq.get(kw);
                if (curr != null) {
                    keywordFreq.put(kw, curr + 1);
                } else {
                    keywordFreq.put(kw, 1);
                }

            }
            for (String ca : e.getCategories().keySet()) {
                totalC += 1;
                Integer curr = categoryFreq.get(ca);
                if (curr != null) {
                    categoryFreq.put(ca, curr + 1);
                } else {
                    keywordFreq.put(ca, 1);
                }
            }
        }
        System.out.println("Finished initialization.");

        Scanner reader = new Scanner(System.in);

        while (true) {
            System.out.println("\nEnter 'k' for a list of keywords, 'c' for a list of categories");
            System.out.println("Enter 'find [keyword]' for a list of courses with specified [keyword]. Enter 'cat [category]' for a list of courses with specified [category].");
            System.out.println("Enter 'exit' to end the program.\n");
            String s = reader.nextLine();

            if (s.equals("k")) {
                for (String kw : keywordFreq.keySet()) {
                    System.out.println(String.format("%-30s %f", kw, 100*keywordFreq.get(kw) / totalK) + "%");
                }
            } else if (s.equals("c")) {
                for (String ca : categoryFreq.keySet()) {

                    System.out.println(String.format("%-30s %f", ca, 100*categoryFreq.get(ca) / totalC) + "%");
                    System.out.println(ca);
                }
            } else if (s.startsWith("find")) {
                int spaceIdx = s.indexOf(' ');

                String tgt = s.substring(spaceIdx + 1);

                System.out.println("Searching for: " + tgt);
                if (!keywordFreq.keySet().contains(tgt)) {
                    System.out.println("None of the courses have this keyword.");
                } else {
                    for (Entry e : entryTable) {
                        if (e.getKeywords().containsKey(tgt)) {
                            System.out.println(e.getSubj() + " " + e.getCatalogNbr() + ": " + e.getTitle());
                        }
                    }
                }
            } else if (s.startsWith("cat")) {
                int spaceIdx = s.indexOf(' ');

                String tgt = s.substring(spaceIdx+1);
                System.out.println("Searching for: " + tgt);
                if (!categoryFreq.keySet().contains(tgt)) {
                    System.out.println("None of the courses have this category.");
                } else {
                    for (Entry e : entryTable) {
                        if (e.getCategories().containsKey(tgt)) {
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

    public static List<Entry> readEntryData(String path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        Gson gson = new Gson();
        Entry[] entryArray;
        entryArray = gson.fromJson(bufferedReader, Entry[].class);
        bufferedReader.close();
        List<Entry> entryTable = new ArrayList<Entry>();
        for (int i = 0; i < entryArray.length; i++) {
            entryTable.add(entryArray[i]);
        }
        return entryTable;
    }
    public static void main(String[] args) throws FileNotFoundException {
        String path = "entryData.json";

        try {

            List<Entry> entryTable = readEntryData(path);

            prompter(entryTable);

        } catch (IOException e) {
            System.out.println("Failed to read from input file.");
        } finally {

        }
    }

}
