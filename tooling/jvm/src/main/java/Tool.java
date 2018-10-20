import api.cornell.data.classes.Course;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;

public class Tool {


    public static void prompter(List<Entry> entryTable) {
        System.out.println("WELCOME TO COURSE READER");
        System.out.println("Initializing keyword and category sets...");

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
        System.out.println("Finished initialization.");

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

                String tgt = "";
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

                String tgt = "";
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
        String path = "entryData.json";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        Gson gson = new Gson();
        Entry[] entryArray;

        try {
            entryArray = gson.fromJson(bufferedReader, Entry[].class);
            bufferedReader.close();

            List<Entry> entryTable = new ArrayList<Entry>();
            for (int i = 0; i < entryArray.length; i++) {
                entryTable.add(entryArray[i]);
            }
            prompter(entryTable);

        } catch (IOException e) {
            System.out.println("Failed to read from input file.");
        }
    }

}
