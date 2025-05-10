package org.example.Dorms;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.JsonObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.tartarus.snowball.ext.englishStemmer;

public class FirebaseDormDatasource implements DormDataSource {

  private final Firestore firestore;
  private final CollectionReference dormsRef;
  private List<Dorm> cachedDorms = new ArrayList<>();
  private boolean initialized = false;

  public FirebaseDormDatasource() throws IOException {
    // Initialize Firebase connection
    String workingDirectory = System.getProperty("user.dir");
    Path firebaseConfigPath = Paths.get(workingDirectory, "src", "main", "resources",
        "firebase_config.json");
    FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toFile());

    FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();

    if (FirebaseApp.getApps().isEmpty()) {
      FirebaseApp.initializeApp(options);
    }

    this.firestore = FirestoreClient.getFirestore();
    this.dormsRef = firestore.collection("dorms");

    // Initialize the Firestore listener to keep cached dorms updated
    initializeFirestoreListener();

    // Load accessibility and room type data, then upload to Firestore
    loadAndUploadDormData();
  }

  private void loadAndUploadDormData() {
    try {
      // Fetch accessibility info (keys are standardized names)
      Map<String, Integer> accessibilityMap = AccessibilityFetcher.fetchAccessibility();

      // Parse room types CSV (keys are also standardized names)
      DormRoomTypesParser parser = new DormRoomTypesParser();
      Map<String, Set<String>> roomTypes = parser.parseDormRoomTypes("/Users/bahar/Desktop/dorm.csv");

      // Upload data to Firebase
      uploadDormData(roomTypes, accessibilityMap);

    } catch (Exception e) {
      System.err.println("Error loading and uploading dorm data: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void uploadDormData(Map<String, Set<String>> roomTypes, Map<String, Integer> accessibilityMap) {
    // For each dorm in the room types map
    for (Map.Entry<String, Set<String>> entry : roomTypes.entrySet()) {
      String dormName = entry.getKey();
      Set<String> dormRoomTypes = entry.getValue();

      // Create a new dorm object
      Map<String, Object> dormData = new HashMap<>();
      dormData.put("dormName", dormName);
      dormData.put("roomType", new ArrayList<>(dormRoomTypes));

      // Add accessibility score if available
      if (accessibilityMap.containsKey(dormName)) {
        dormData.put("accessibility", accessibilityMap.get(dormName));
      } else {
        dormData.put("accessibility", "No Accessibility Info Available for " + dormName); // Default value
      }

      // Add proximity and template
      dormData.put("proximity", new ArrayList<>(ProximityAssigner.getProximity(dormName)));
      dormData.put("Community", new ArrayList<>(CommunityHouseAssigner.getCommunity(dormName)));

      // Placeholder fields
      dormData.put("bathrooms", new ArrayList<String>());
      dormData.put("reviews", new ArrayList<String>());

      // Add to Firestore using dorm name as document ID
      dormsRef.document(dormName).set(dormData);
    }
  }

  private void initializeFirestoreListener() {
    dormsRef.addSnapshotListener((snapshots, error) -> {
      if (error != null) {
        System.err.println("Firestore listen failed: " + error.getMessage());
        return;
      }

      if (snapshots != null) {
        List<Dorm> loadedDorms = new ArrayList<>();
        for (var doc : snapshots.getDocuments()) {
          try {
            String name = doc.getString("dormName");

            // Convert to HashSet for roomTypes
            List<Object> roomTypeObjects = (List<Object>) doc.get("roomType");
            Set<String> roomTypes = new HashSet<>();
            if (roomTypeObjects != null) {
              for (Object obj : roomTypeObjects) {
                roomTypes.add(obj.toString());
              }
            }

            // Convert to HashSet for communities
            List<Object> communityObjects = (List<Object>) doc.get("Community");
            Set<String> communities = new HashSet<>();
            if (communityObjects != null) {
              for (Object obj : communityObjects) {
                communities.add(obj.toString());
              }
            }

            // Convert to HashSet for bathrooms
            List<Object> bathroomObjects = (List<Object>) doc.get("bathrooms");
            Set<String> bathrooms = new HashSet<>();
            if (bathroomObjects != null) {
              for (Object obj : bathroomObjects) {
                bathrooms.add(obj.toString());
              }
            }

            Integer accessibility = doc.getLong("accessibility") != null
                ? doc.getLong("accessibility").intValue()
                : 0;

            // Convert to HashSet for proximity
            List<Object> proximityObjects = (List<Object>) doc.get("proximity");
            Set<String> proximity = new HashSet<>();
            if (proximityObjects != null) {
              for (Object obj : proximityObjects) {
                proximity.add(obj.toString());
              }
            }

            // Convert to List for reviews
            List<Object> reviewObjects = (List<Object>) doc.get("reviews");
            List<String> reviews = new ArrayList<>();
            if (reviewObjects != null) {
              for (Object obj : reviewObjects) {
                reviews.add(obj.toString());
              }
            }

            Dorm dorm = new Dorm(name, roomTypes, bathrooms, proximity, communities, accessibility, reviews);
            loadedDorms.add(dorm);
            System.out.println("Successfully loaded dorm: " + name);
          } catch (Exception e) {
            System.err.println("Error parsing dorm document: " + e.getMessage());
            e.printStackTrace();
          }
        }
        cachedDorms = loadedDorms;
        initialized = true;
        System.out.println("Loaded " + cachedDorms.size() + " dorms from Firestore.");
      }
    });
  }

//  public void addDorm(Dorm dorm) {
//    Map<String, Object> dormData = new HashMap<>();
//    dormData.put("dormName", dorm.getName());
//    dormData.put("roomType", dorm.getRoomTypes());
//    dormData.put("Community", dorm.getCommunities());
//    dormData.put("accessibility", dorm.getAccessibility());
//    dormData.put("proximity", dorm.getProximity());
//
//    // Use dorm name as document ID for easier retrieval
//    dormsRef.document(dorm.getName()).set(dormData);
//  }

  @Override
  public List<Dorm> getAllDorms() {
    System.out.println("Fetching all dorms from Firestore.");
    if (!initialized) {
      // If cache isn't ready, fetch directly
      try {
        var documents = dormsRef.get().get().getDocuments();
        List<Dorm> result = new ArrayList<>();

        for (var doc : documents) {
          try {
            String name = doc.getString("dormName");

            // Convert to HashSet for roomTypes
            List<Object> roomTypeObjects = (List<Object>) doc.get("roomType");
            Set<String> roomTypes = new HashSet<>();
            if (roomTypeObjects != null) {
              for (Object obj : roomTypeObjects) {
                roomTypes.add(obj.toString());
              }
            }

            // Convert to HashSet for communities
            List<Object> communityObjects = (List<Object>) doc.get("Community");
            Set<String> communities = new HashSet<>();
            if (communityObjects != null) {
              for (Object obj : communityObjects) {
                communities.add(obj.toString());
              }
            }

            // Convert to HashSet for bathrooms
            List<Object> bathroomObjects = (List<Object>) doc.get("bathrooms");
            Set<String> bathrooms = new HashSet<>();
            if (bathroomObjects != null) {
              for (Object obj : bathroomObjects) {
                bathrooms.add(obj.toString());
              }
            }

            Integer accessibility = doc.getLong("accessibility") != null
                ? doc.getLong("accessibility").intValue()
                : 0;

            // Convert to HashSet for proximity
            List<Object> proximityObjects = (List<Object>) doc.get("proximity");
            Set<String> proximity = new HashSet<>();
            if (proximityObjects != null) {
              for (Object obj : proximityObjects) {
                proximity.add(obj.toString());
              }
            }

            // Convert to List for reviews
            List<Object> reviewObjects = (List<Object>) doc.get("reviews");
            List<String> reviews = new ArrayList<>();
            if (reviewObjects != null) {
              for (Object obj : reviewObjects) {
                reviews.add(obj.toString());
              }
            }

            Dorm dorm = new Dorm(name, roomTypes, bathrooms, proximity, communities, accessibility, reviews);
            result.add(dorm);
          } catch (Exception e) {
            System.err.println("Error parsing dorm: " + e.getMessage());
            e.printStackTrace();
          }
        }
        return result;
      } catch (InterruptedException | ExecutionException e) {
        System.err.println("Error fetching dorms: " + e.getMessage());
        e.printStackTrace();
        return new ArrayList<>();
      }
    }

    // Return cached dorms if initialized
    return new ArrayList<>(cachedDorms);
  }


  @Override
  public List<DormSearchResult> searchDorms(String query) {
    List<DormSearchResult> results = new ArrayList<>();
    List<Dorm> dorms = getAllDorms();

    for (Dorm dorm : dorms) {
      int score = calculateDormScore(dorm, query.toLowerCase());
      if (score > 0) {
        results.add(new DormSearchResult(dorm, score));
      }
    }

    results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

    return results.subList(0, Math.min(3, results.size()));
  }

  /**
   * Stem a word to root form to match semantically related words
   */
  englishStemmer stemmer = new englishStemmer();
  String stem(String word) {
    stemmer.setCurrent(word.toLowerCase());
    stemmer.stem();
    return stemmer.getCurrent();
  }
  private int calculateDormScore(Dorm dorm, String query) {
    LevenshteinDistance ld = new LevenshteinDistance(Integer.MAX_VALUE);
    String stemmedQuery = stem(query.toLowerCase());
    int score = 0;

    // Check dorm name (allow typo)
    String stemmedDorm = stem(dorm.getName().toLowerCase());
    if (stemmedDorm.equals(stemmedQuery)) {
      score += 10;
    } else if (stemmedDorm.contains(stemmedQuery)) {
      score += 7;
    } else {
      int distance = ld.apply(stemmedDorm, stemmedQuery);
      if (distance <= 2) {
        score += 5;
      }
    }
    // Check room types
    for (String roomType : dorm.getRoomTypes()) {
      String stemmedRoomType = stem(roomType.toLowerCase());
      if (stemmedRoomType.contains(stemmedQuery)) {
        score += 5;
      } else {
        int distance = ld.apply(stemmedRoomType, stemmedQuery);
        if (distance <= 2) {
          score += 3;
        }
      }
    }
    // Check communities
    for (String community : dorm.getCommunities()) {
      String stemmedCommunity = stem(community.toLowerCase());
      if (stemmedCommunity.contains(stemmedQuery)) {
        score += 6;
      } else {
        int distance = ld.apply(stemmedCommunity, stemmedQuery);
        if (distance <= 2) {
          score += 4;
        }
      }
    }
    // Check proximity
    for (String location : dorm.getProximity()) {
      String stemmedLocation = stem(location.toLowerCase());
      if (stemmedLocation.contains(stemmedQuery)) {
        score += 3;
      } else {
        int distance = ld.apply(stemmedLocation, stemmedQuery);
        if (distance <= 2) {
          score += 2;
        }
      }
    }
    return score;
  }

  @Override
  public List<DormSearchResult> matchDorms(JsonObject preferences) {
    List<DormSearchResult> results = new ArrayList<>();
    List<Dorm> dorms = getAllDorms();

    for (Dorm dorm : dorms) {
      int score = calculateMatchScoreFromPreferences(dorm, preferences);
      if (score > 0) {
        results.add(new DormSearchResult(dorm, score));
      }
    }

    results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
    return results.subList(0, Math.min(3, results.size()));
  }


  private int calculateMatchScoreFromPreferences(Dorm dorm, JsonObject preferences) {
    int score = 0;

    if (preferences.has("roomType")) {
      String preferredRoom = preferences.get("roomType").getAsString();
      if (dorm.getRoomTypes().stream().anyMatch(type -> type.equalsIgnoreCase(preferredRoom))) {
        score += 10;
      }
    }

    if (preferences.has("community")) {
      String preferredCommunity = preferences.get("community").getAsString();
      if (dorm.getCommunities().stream().anyMatch(c -> c.equalsIgnoreCase(preferredCommunity))) {
        score += 8;
      }
    }

    if (preferences.has("proximity")) {
      String preferredProximity = preferences.get("proximity").getAsString();
      if (dorm.getProximity().stream().anyMatch(p -> p.toLowerCase().contains(preferredProximity.toLowerCase()))) {
        score += 6;
      }
    }

    if (preferences.has("accessibility") && dorm.getAccessibility() > 0) {
      boolean needsAccessibility = preferences.get("accessibility").getAsBoolean();
      if (needsAccessibility) {
        score += dorm.getAccessibility();
      }
    }

    return score;
  }
}

