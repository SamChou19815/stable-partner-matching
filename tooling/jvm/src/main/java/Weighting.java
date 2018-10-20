import api.cornell.data.classes.Course;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Weighting {


    public static void main(String[] args) throws FileNotFoundException {
        String path = "keywords.json";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        Gson gson = new Gson();
        Course[] courseArray;
        try {
            courseArray = gson.fromJson(bufferedReader, Course[].class);
            bufferedReader.close();

//            List<Entry> entryTable = createEntryTable(courseArray);
//            try {
//                writeData(entryTable);
//            } catch (IOException e) {
//                System.out.println("Failed to write entry data to json");
//            }

        } catch (IOException e) {
            System.out.println("Failed to read from input file.");
        }
    }
}
