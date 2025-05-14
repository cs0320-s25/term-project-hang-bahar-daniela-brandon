package org.example.Dorms;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Utility class for parsing dorm room types from a CSV file.
 * 
 * Each row in the CSV represents a dorm with a corresponding room type.
 * This parser standardizes dorm names (e.g., lowercased and trimmed) and builds
 * a mapping of dorm names to their respective room types.
 */

public class DormRoomTypesParser {

    /**
     * Parses a CSV file and creates a mapping from dorm names to a set of room types.
     *
     * @param csvFilePath the path to the CSV file containing dorm information
     * @return a map where each key is a standardized dorm name and the value is a set of room types
     * @throws IOException if the file cannot be read
     */

  public Map<String, Set<String>> parseDormRoomTypes(String csvFilePath) throws IOException {
    Map<String, Set<String>> dormRoomTypes = new HashMap<>();

    try (BufferedReader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {
      String line;
      boolean firstLine = true;

      while ((line = reader.readLine()) != null) {
        if (firstLine || line.startsWith("1,")) {
          firstLine = false;
          continue;
        }

        String[] parts = line.split(",", -1);
        if (parts.length < 7) continue;

        String dormName = parts[1].trim().toLowerCase().replaceAll("\\s+", "");
        String roomType = parts[6].trim();    // e.g., Double (Suite/Apartment)

        dormRoomTypes
            .computeIfAbsent(dormName, k -> new HashSet<>())
            .add(roomType);
      }
    }

    return dormRoomTypes;
  }

  
    /**
     * Converts a set of strings into a comma-separated string, quoted.
     *
     * @param set the set to convert
     * @return a string representation of the set
     */

  private static String setToString(Set<String> set) {
    return set.stream().map(s -> "\"" + s + "\"").reduce((a, b) -> a + ", " + b).orElse("");
  }

   /**
     * Converts a list of strings into a comma-separated string, quoted.
     *
     * @param list the list to convert
     * @return a string representation of the list
     */

  private static String listToString(List<String> list) {
    return list.stream().map(s -> "\"" + s + "\"").reduce((a, b) -> a + ", " + b).orElse("");
  }
}
