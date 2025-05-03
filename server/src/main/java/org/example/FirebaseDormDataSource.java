package org.example;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import org.example.Posts.PostsDataSource;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.auth.oauth2.GoogleCredentials;

public class FirebaseDormDataSource implements DormDataSource{
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

}