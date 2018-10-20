import api.cornell.data.classes.Course;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseReader {

    private static List<Entry> createEntryTable(Course[] courseArray) {
        List<Entry> entryTable = new ArrayList<Entry>();
        for (int i = 0; i < courseArray.length; i++) {
            Course c = courseArray[i];
            String title = c.getLongTitle();
            String subject = c.getSubject().toString();
            String catalogNbr = c.getCatalogNumber();
            String desc = c.getDescription();
            int id = c.getCourseId();
            Map<String, Double> keywords = null;
            System.out.println("Analyzing: " + subject + " " + catalogNbr + ": " + title + "...");
            try {
                keywords = Analyze.analyzeEntitiesText(title + ". " + desc);
            } catch (Exception e) {
                System.out.println("Could not initialize entity analysis for: " + title);
                System.out.println(e.getMessage());
            }
            Map<String, Double> categories = null;
            try {
                categories = Analyze.analyzeCategoriesText(title + ". " + desc);
            } catch (Exception e) {
                System.out.println("Could not initialize category analysis for: " + title);
                System.out.println(e.getMessage());
            }
            entryTable.add(new Entry(title, subject, catalogNbr, desc, keywords, categories));
        }
        return entryTable;

    }

    public static void writeData(List<Entry> entryTable, String path) throws IOException {
        Gson gson = new Gson();
        System.out.println("Writing data to: " + path);
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        bw.write(gson.toJson(entryTable));
        bw.close();
        System.out.println("Finished writing data.");
    }

    public static void main(String[] args) throws FileNotFoundException {
        String path = "cs-courses.json";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        Gson gson = new Gson();
        Course[] courseArray;
        try {
            courseArray = gson.fromJson(bufferedReader, Course[].class);
            bufferedReader.close();

            List<Entry> entryTable = createEntryTable(courseArray);
            try {
                writeData(entryTable, "data.json");
            } catch (IOException e) {
                System.out.println("Failed to write entry data to json");
            }

        } catch (IOException e) {
            System.out.println("Failed to read from input file.");
        }
    }

}
