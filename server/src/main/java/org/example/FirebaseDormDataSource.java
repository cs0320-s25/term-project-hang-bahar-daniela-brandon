package org.example;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.auth.oauth2.GoogleCredentials;
import java.util.stream.Collectors;

public class FirebaseDormDataSource implements DormDataSource {
  private List<Dorm> cachedDorms = new ArrayList<>();
  private boolean initialized = false;

  public FirebaseDormDataSource() {
    initializeRealTimeListener();
  }

  private void initializeRealTimeListener() {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dormsRef = database.getReference("dorms");

    // Initial load + real-time updates
    dormsRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        // Handle successful data retrieval
        if (dataSnapshot.exists()) {
          // Process your data here
          Dorm dorm = dataSnapshot.getValue(Dorm.class);
          System.out.println("Dorm name: " + dorm.getName());
        } else {
          System.out.println("No data available");
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        // Handle errors
        System.err.println("Database error: " + databaseError.getMessage());
        System.err.println("Details: " + databaseError.getDetails());
        System.err.println("Code: " + databaseError.getCode());
      }


    });
  }

  @Override
  public List<DormSearchResult> searchDorms(String query) {
    if (!initialized) {
      throw new IllegalStateException("Database not initialized yet");
    }

    List<DormSearchResult> results = new ArrayList<>();
    for (Dorm dorm : cachedDorms) {
      int score = calculateDormScore(dorm, query.toLowerCase());
      if (score > 0) {
        results.add(new DormSearchResult(dorm, score));
      }
    }
    results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
    return results;
  }


  @Override
  public List<Dorm> getAllDorms() {
    return new ArrayList<>(cachedDorms);
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

    // Check bathroom types
    for (String bathroom : dorm.getBathrooms()) {
      if (bathroom.toLowerCase().contains(query)) {
        score += 20;
      }
    }

    // Check proximity
    for (String location : dorm.getProximity()) {
      if (location.toLowerCase().contains(query)) {
        score += 15;
      }
    }

    // Check reviews
    int reviewMatches = 0;
    for (String review : dorm.getReviews()) {
      if (review.toLowerCase().contains(query)) {
        reviewMatches++;
      }
    }
    score += reviewMatches * 10;

    return score;
  }


  @Override
  public List<DormSearchResult> matchDorms(JsonObject preferences) {
    System.out.println("entered matchdorms in firebase");
    List<DormSearchResult> results = new ArrayList<>();

    List<Dorm> dormsFromFirebase = getAllDorms();  // Assumes this is implemented and working

    for (Dorm dorm : dormsFromFirebase) {
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

    if (preferences.has("bathroomType")) {
      String preferredBathroom = preferences.get("bathroomType").getAsString();
      if (dorm.getBathrooms().stream().anyMatch(bath -> bath.equalsIgnoreCase(preferredBathroom))) {
        score += 8;
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
        score += 10;
      }
    }

    if (preferences.has("ac")) {
      boolean prefersAC = preferences.get("ac").getAsBoolean();
      if (prefersAC == dorm.hasAC()) {
        score += 5;
      }
    }

    if (preferences.has("accessible")) {
      boolean needsAccessible = preferences.get("accessible").getAsBoolean();
      if (needsAccessible && dorm.isAccessible()) {
        score += 10;
      }
    }

    return score;
  }



}