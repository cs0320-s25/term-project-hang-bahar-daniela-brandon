package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DormRoomTypesParser {

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

  public static String toTitleCase(String input) {
    if (input == null || input.isEmpty()) return input;

    StringBuilder result = new StringBuilder();
    for (String word : input.toLowerCase().split(" ")) {
      if (!word.isEmpty()) {
        result.append(Character.toUpperCase(word.charAt(0)));
        result.append(word.substring(1));
        result.append(" ");
      }
    }
    return result.toString().trim();
  }

  private static String setToString(Set<String> set) {
    return set.stream().map(s -> "\"" + s + "\"").reduce((a, b) -> a + ", " + b).orElse("");
  }

  private static String listToString(List<String> list) {
    return list.stream().map(s -> "\"" + s + "\"").reduce((a, b) -> a + ", " + b).orElse("");
  }
}
