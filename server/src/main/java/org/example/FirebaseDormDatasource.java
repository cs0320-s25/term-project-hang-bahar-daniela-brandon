package org.example;

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
        dormData.put("accessibility", 0); // Default value
      }

      // Add empty lists for other attributes that might be populated later
      dormData.put("Community", new ArrayList<String>());
      dormData.put("proximity", new ArrayList<String>());

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

  public void addDorm(Dorm dorm) {
    Map<String, Object> dormData = new HashMap<>();
    dormData.put("dormName", dorm.getName());
    dormData.put("roomType", dorm.getRoomTypes());
    dormData.put("Community", dorm.getCommunities());
    dormData.put("accessibility", dorm.getAccessibility());
    dormData.put("proximity", dorm.getProximity());

    // Use dorm name as document ID for easier retrieval
    dormsRef.document(dorm.getName()).set(dormData);
  }

  @Override
  public List<Dorm> getAllDorms() {
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
    return results;
  }

  private int calculateDormScore(Dorm dorm, String query) {
    int score = 0;

    // Check dorm name (exact match)
    if (dorm.getName().toLowerCase().equals(query)) {
      score += 50;
    }
    // Partial match in name
    else if (dorm.getName().toLowerCase().contains(query)) {
      score += 30;
    }

    // Check room types
    for (String roomType : dorm.getRoomTypes()) {
      if (roomType.toLowerCase().contains(query)) {
        score += 20;
      }
    }

    // Check proximity
    for (String location : dorm.getProximity()) {
      if (location.toLowerCase().contains(query)) {
        score += 15;
      }
    }

    // Check communities
    for (String community : dorm.getCommunities()) {
      if (community.toLowerCase().contains(query)) {
        score += 20;
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
    return results;
  }

  private int calculateMatchScoreFromPreferences(Dorm dorm, JsonObject preferences) {
    int score = 0;

    if (preferences.has("roomType")) {
      String preferredRoom = preferences.get("roomType").getAsString();
      if (dorm.getRoomTypes().stream().anyMatch(type -> type.equalsIgnoreCase(preferredRoom))) {
        score += 10;
      }
    }

    if (preferences.has("proximity")) {
      String preferredProximity = preferences.get("proximity").getAsString();
      if (dorm.getProximity().stream().anyMatch(p -> p.toLowerCase().contains(preferredProximity.toLowerCase()))) {
        score += 6;
      }
    }

    if (preferences.has("community")) {
      String preferredCommunity = preferences.get("community").getAsString();
      if (dorm.getCommunities().stream().anyMatch(c -> c.equalsIgnoreCase(preferredCommunity))) {
        score += 11;
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

//package org.example;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.firestore.CollectionReference;
//import com.google.cloud.firestore.Firestore;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.cloud.FirestoreClient;
//import com.google.gson.JsonObject;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class FirebaseDormDatasource implements DormDataSource{
//
//  private final com.google.cloud.firestore.Firestore firestore;
//  private final CollectionReference dormsref;
//
//  public FirebaseDormDatasource() throws IOException {
//    String workingDirectory = System.getProperty("user.dir");
//    Path firebaseConfigPath = Paths.get(workingDirectory, "src", "main", "resources",
//        "firebase_config.json");
//    FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toFile());
//
//    FirebaseOptions options = FirebaseOptions.builder()
//        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//        .build();
//
//    if (FirebaseApp.getApps().isEmpty()) {
//      FirebaseApp.initializeApp(options);
//    }
//
//    this.firestore = FirestoreClient.getFirestore();
//    this.dormsref = firestore.collection("dorms");
////    Dorm testdorm = new Dorm("Perkins", >"double", null, null, null, null);
//    addDorm(dorm);
//
//  }
//
//  public void addDorm(Dorm dorm) {
//    Map<String, Object> postValues = new HashMap<>();
//    postValues.put("dormName", dorm.getName());
//    postValues.put("roomType", dorm.getRoomTypes());
//    postValues.put("Community", dorm.getCommunities());
//    postValues.put("accessibility", dorm.getAccessibility());
//    postValues.put("proximity", dorm.getProximity());
//    dormsref.add(postValues);
//  }
//
//  @Override
//  public List<Dorm> getAllDorms() {
//    return List.of();
//  }
//
//  @Override
//  public List<DormSearchResult> searchDorms(String query) {
//    return List.of();
//  }
//
//  @Override
//  public List<DormSearchResult> matchDorms(JsonObject preferences) {
//    return List.of();
//  }
//}
//
//
////
////  @Override
////  public List<AbstractPost> getAllPosts() {
////    // Get all dorm posts and dining posts
////    List<DormPost> dormPosts = getAllDormPost();
////    List<DiningPost> diningPosts = getAllDiningPost();
////
////    // Combine into a single list of Post objects
////    List<AbstractPost> allPosts = new ArrayList<>();
////    allPosts.addAll(dormPosts);
////    allPosts.addAll(diningPosts);
////
////    return allPosts;
////  }
////
////  @Override
////  public Integer getAverageRatingsByName(String name) {
////    List<AbstractPost> allPosts = getAllPosts();
////    List<Integer> ratings = new ArrayList<>();
////
////    for (AbstractPost post : allPosts) {
////      if (post.getName().toLowerCase().contains(name.toLowerCase())) {
////        ratings.add(post.getRating());
////      }
////    }
////    return calculateAverage(ratings);
////  }
////
////  @Override
////  public List<String> getDormReviewsByName(String dormName) {
////    List<String> reviews = new ArrayList<>();
////    List<DormPost> dormPosts = getAllDormPost();
////    for (DormPost post : dormPosts) {
////      if (post.getName().toLowerCase().contains(dormName.toLowerCase())) {
////        reviews.add(post.getReview());
////      }
////    }
////    return reviews;
////  }
////
////  // HELPER FUNCTIONS
////  private List<DormPost> getAllDormPost() {
////    try {
////      ApiFuture<QuerySnapshot> future = dormPostsRef.get();
////      List<DormPost> posts = new ArrayList<>();
////
////      QuerySnapshot querySnapshot = future.get();
////      for (DocumentSnapshot document : querySnapshot.getDocuments()) {
////        try {
////          String title = document.getString("title");
////          String name = document.getString("name");
////          Long ratingLong = document.getLong("rating");
////          Integer rating = ratingLong != null ? ratingLong.intValue() : 0;
////          String review = document.getString("review");
////          LocalDateTime postDate = null;
////
////          try {
////            String dateStr = document.getString("date");
////            if (dateStr != null) {
////              postDate = LocalDateTime.parse(dateStr);
////            } else {
////              postDate = LocalDateTime.now();
////            }
////          } catch (Exception e) {
////            postDate = LocalDateTime.now();
////          }
////
////          DormPost post = new DormPost(title, name, rating, review, postDate);
////          posts.add(post);
////        } catch (Exception e) {
////          System.err.println("Error parsing dorm post: " + e.getMessage());
////        }
////      }
////
////      return posts;
////    } catch (InterruptedException | ExecutionException e) {
////      System.err.println("Error fetching dorm posts: " + e.getMessage());
////      return new ArrayList<>();
////    }
////  }
////
////  public List<DiningPost> getAllDiningPost() {
////    try {
////      ApiFuture<QuerySnapshot> future = diningPostsRef.get();
////      List<DiningPost> posts = new ArrayList<>();
////
////      QuerySnapshot querySnapshot = future.get();
////      for (DocumentSnapshot document : querySnapshot.getDocuments()) {
////        try {
////          String title = document.getString("title");
////          String name = document.getString("name");
////          String meals = document.getString("meals");
////          Long ratingLong = document.getLong("rating");
////          Integer rating = ratingLong != null ? ratingLong.intValue() : 0;
////          String review = document.getString("review");
////          LocalDateTime postDate = null;
////
////          try {
////            String dateStr = document.getString("date");
////            if (dateStr != null) {
////              postDate = LocalDateTime.parse(dateStr);
////            } else {
////              postDate = LocalDateTime.now();
////            }
////          } catch (Exception e) {
////            postDate = LocalDateTime.now();
////          }
////
////          DiningPost post = new DiningPost(title, name, meals, rating, review, postDate);
////          posts.add(post);
////        } catch (Exception e) {
////          System.err.println("Error parsing dining post: " + e.getMessage());
////        }
////      }
////
////      return posts;
////    } catch (InterruptedException | ExecutionException e) {
////      System.err.println("Error fetching dining posts: " + e.getMessage());
////      return new ArrayList<>();
////    }
////  }
////
////  private Integer calculateAverage(List<Integer> ratings) {
////    if (ratings.isEmpty()) {
////      return 0;
////    }
////    int sum = 0;
////    for (Integer rating : ratings) {
////      sum += rating;
////    }
////    return sum / ratings.size();
////  }
////
////}
//
////  private boolean initialized = false;
////
////  public FirebaseDormDataSource() throws IOException {
////    initializeFirestoreListener();
////    //fetch accessibility info (keys are standardized names)
////    Map<String, Integer> accessibilityMap = AccessibilityFetcher.fetchAccessibility();
////
////    // Parse room types CSV (keys are also standardized names)
////    DormRoomTypesParser parser = new DormRoomTypesParser();
////    Map<String, Set<String>> roomTypes = parser.parseDormRoomTypes("/Users/bahar/Desktop/dorm.csv");
////
////    // Upload to Firebase
////    DormDataUploader uploader = new DormDataUploader();
////    uploader.uploadDormData(roomTypes, accessibilityMap);
////  }
////
////  private void initializeFirebase() throws IOException {
////    FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase_config.json");
////
////    FirebaseOptions options = new FirebaseOptions.Builder()
////        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
////        .setDatabaseUrl("https://brownbnb-e9ab8-default-rtdb.firebaseio.com/")
////        .build();
////
////    if (FirebaseApp.getApps().isEmpty()) {
////      FirebaseApp.initializeApp(options);
////    }
////  }
////
////
////  private void initializeFirestoreListener() {
////    try {
////      if (FirebaseApp.getApps().isEmpty()) {
////        initializeFirebase();
////      }
////
////      Firestore firestore = FirestoreClient.getFirestore();
////      CollectionReference dormsRef = firestore.collection("dorms");
////
////      dormsRef.addSnapshotListener((snapshots, error) -> {
////        if (error != null) {
////          System.err.println("Firestore listen failed: " + error.getMessage());
////          return;
////        }
////
////        if (snapshots != null) {
////          List<Dorm> loadedDorms = new ArrayList<>();
////          for (DocumentSnapshot doc : snapshots.getDocuments()) {
////            Dorm dorm = doc.toObject(Dorm.class);
////            if (dorm != null) {
////              loadedDorms.add(dorm);
////            }
////          }
////          cachedDorms = loadedDorms;
////          initialized = true;
////          System.out.println("Loaded " + cachedDorms.size() + " dorms from Firestore.");
////        }
////      });
////
////    } catch (IOException e) {
////      throw new RuntimeException("Failed to initialize Firestore: " + e.getMessage(), e);
////    }
////  }
//
//
////
////  @Override
////  public List<DormSearchResult> searchDorms(String query) {
////    if (!initialized) {
////      throw new IllegalStateException("Database not initialized yet");
////    }
////
////    List<DormSearchResult> results = new ArrayList<>();
////    for (Dorm dorm : cachedDorms) {
////      int score = calculateDormScore(dorm, query.toLowerCase());
////      if (score > 0) {
////        results.add(new DormSearchResult(dorm, score));
////      }
////    }
////    results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
////    return results;
////  }
////
////
////  @Override
////  public List<Dorm> getAllDorms() {
////    return new ArrayList<>(cachedDorms);
////  }
////
////  private int calculateDormScore(Dorm dorm, String query) {
////    int score = 0;
////
////    // Check dorm name (exact match)
////    if (dorm.getName().toLowerCase().equals(query)) {
////      score += 50;
////    }
////    // Partial match in name
////    else if (dorm.getName().toLowerCase().contains(query)) {
////      score += 30;
////    }
////
////    // Check room types
////    for (String roomType : dorm.getRoomTypes()) {
////      if (roomType.toLowerCase().contains(query)) {
////        score += 20;
////      }
////    }
////
////    // Check bathroom types
////    for (String bathroom : dorm.getBathrooms()) {
////      if (bathroom.toLowerCase().contains(query)) {
////        score += 20;
////      }
////    }
////
////    // Check proximity
////    for (String location : dorm.getProximity()) {
////      if (location.toLowerCase().contains(query)) {
////        score += 15;
////      }
////    }
////
////    // Check reviews
////    int reviewMatches = 0;
////    for (String review : dorm.getReviews()) {
////      if (review.toLowerCase().contains(query)) {
////        reviewMatches++;
////      }
////    }
////    score += reviewMatches * 10;
////
////    return score;
////  }
////
////
////  @Override
////  public List<DormSearchResult> matchDorms(JsonObject preferences) {
////    System.out.println("entered matchdorms in firebase");
////    List<DormSearchResult> results = new ArrayList<>();
////
////    List<Dorm> dormsFromFirebase = getAllDorms();  // Assumes this is implemented and working
////
////    for (Dorm dorm : dormsFromFirebase) {
////      int score = calculateMatchScoreFromPreferences(dorm, preferences);
////      if (score > 0) {
////        results.add(new DormSearchResult(dorm, score));
////      }
////    }
////
////    results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
////    return results;
////  }
////
////  private int calculateMatchScoreFromPreferences(Dorm dorm, JsonObject preferences) {
////    int score = 0;
////
////    if (preferences.has("roomType")) {
////      String preferredRoom = preferences.get("roomType").getAsString();
////      if (dorm.getRoomTypes().stream().anyMatch(type -> type.equalsIgnoreCase(preferredRoom))) {
////        score += 10;
////      }
////    }
////
////
////    if (preferences.has("proximity")) {
////      String preferredProximity = preferences.get("proximity").getAsString();
////      if (dorm.getProximity().stream().anyMatch(p -> p.toLowerCase().contains(preferredProximity.toLowerCase()))) {
////        score += 6;
////      }
////    }
////
////    if (preferences.has("community")) {
////      String preferredCommunity = preferences.get("community").getAsString();
////      if (dorm.getCommunities().stream().anyMatch(c -> c.equalsIgnoreCase(preferredCommunity))) {
////        score += 11;
////      }
////    }
////
////    return score;
////  }
////
////
////
////}
//
