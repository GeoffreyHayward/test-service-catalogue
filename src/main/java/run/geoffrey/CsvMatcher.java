package run.geoffrey;

import com.opencsv.CSVReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CsvMatcher {

    public static void main(String[] args) {
        // File paths relative to the resources folder
        String file1 = "non-prod.csv"; // Replace with your actual file name
        String file2 = "prod-data.csv"; // Replace with your actual file name

        try {
            // Read CSV data into lists for comparison
            List<String[]> rows1 = readRowsFromCsv(file1);
            List<String[]> rows2 = readRowsFromCsv(file2);

            // Extract paths for easier comparison
            Set<String> paths1 = extractPaths(rows1);
            Set<String> paths2 = extractPaths(rows2);

            // Find non-matches
            Set<String> onlyInFirst = new HashSet<>(paths1);
            onlyInFirst.removeAll(paths2);

            Set<String> onlyInSecond = new HashSet<>(paths2);
            onlyInSecond.removeAll(paths1);

            // Print non-matching rows
            System.out.println("Paths only in the first file:");
            rows1.stream()
                    .filter(row -> onlyInFirst.contains(row[1]))
                    .forEach(row -> System.out.println(row[0].trim()));

            System.out.println("\nPaths only in the second file:");
            rows2.stream()
                    .filter(row -> onlyInSecond.contains(row[1]))
                    .forEach(row -> System.out.println(String.join(", ", row)));

            // Print matching rows
            System.out.println("\nMatching rows:");
            rows1.stream()
                    .filter(row -> paths2.contains(row[1]))
                    .forEach(row -> {
                        System.out.println(String.join(", ", row));
                    });

        } catch (Exception e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }

    private static List<String[]> readRowsFromCsv(String fileName) throws Exception {
        List<String[]> rows = new ArrayList<>();

        // Load file from resources using ClassLoader
        try (InputStream inputStream = CsvMatcher.class.getClassLoader().getResourceAsStream(fileName);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             CSVReader reader = new CSVReader(inputStreamReader)) {

            rows = reader.readAll(); // Read all rows
        }

        return rows;
    }

    private static Set<String> extractPaths(List<String[]> rows) {
        Set<String> paths = new HashSet<>();
        for (String[] row : rows) {
            if (row.length > 1) {
                paths.add(row[1]); // Add the "Path" column (index 1) to the set
            }
        }
        return paths;
    }
}