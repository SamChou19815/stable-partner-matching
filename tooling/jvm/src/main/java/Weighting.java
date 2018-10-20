import api.cornell.data.classes.Course;
import com.google.gson.Gson;
import org.json.JSONArray;

import java.io.*;
import java.util.*;

public class Weighting {


    public static String[] readWeights(String path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        Gson gson = new Gson();
        String[] weights;

        weights = gson.fromJson(bufferedReader, String[].class);
        bufferedReader.close();
        return weights;
    }

    public static List<Vector> generateVectors(List<Entry> entryTable, String[] weights) {
        List<Vector> retval = new ArrayList<Vector>();
        for (Entry e : entryTable) {
            Map<String, Double> hm = new HashMap<String, Double>();

            // Init weight set
            Set<String> weightSet = new HashSet<String>();

            for (int i = 0; i < weights.length; i++) {
                String w = weights[i];
                weightSet.add(w);
                hm.put(w, 0.0);
            }

            Map<String, Double> entryKeywords = e.getKeywords();
            Map<String, Double> entryCategories = e.getCategories();

            Set<String> entryKeywordSet = entryKeywords.keySet();
            Set<String> entryCatSet = entryCategories.keySet();

            entryKeywordSet.retainAll(weightSet);
            entryCatSet.retainAll(weightSet);

            for (String kw : entryKeywordSet) {
                hm.put(kw, entryKeywords.get(kw));
            }

            for (String cat : entryCatSet) {
                hm.put(cat, entryCategories.get(cat));
            }
            retval.add(new Vector(e.getSubj(), e.getCatalogNbr(), e.getTitle(), e.getDescription(), hm));

        }
        return retval;
    }

    public static void writeVectors(List<Vector> vectors, String path) throws IOException {
        Gson gson = new Gson();
        System.out.println("Writing data to: " + path);
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        bw.write(gson.toJson(vectors));
        bw.close();
        System.out.println("Finished writing data.");
    }

    public static void main(String[] args) throws FileNotFoundException {
        String path = "weights.json";
        String e_path = "entryData.json";
        try {
            String[] weights = readWeights(path);
            try {
                List<Entry> entryTable = Tool.readEntryData(e_path);
                List<Vector> vectors = generateVectors(entryTable, weights);
                String vpath = "vectors.json";
                try {

                    writeVectors(vectors, vpath);
                } catch (IOException e) {
                    System.out.println("Failed to write to: " + vpath);
                }

            } catch (IOException e) {
                System.out.println("Failed to read from: " + e_path);
            }
        } catch (IOException e) {
            System.out.println("Failed to read from: " + path);
        }

    }
}
