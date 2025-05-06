package org.example;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
import java.util.*;

public class DormDataUploader {
  private FirebaseDatabase database = FirebaseDatabase.getInstance();
  private DatabaseReference myRef = database.getReference("dorms");
  private FirebaseDormDatasource firebaseDormDatasource;

  // Hardcoded proximity and communities
  private static final Set<String> PROXIMITY = Set.of("Near Main Green", "Wriston Quad");
  private static final Set<String> COMMUNITIES = Set.of("First-Year", "Sophomore");

  public DormDataUploader(FirebaseDormDatasource firebaseDormDatasource) throws IOException {
    this.firebaseDormDatasource = firebaseDormDatasource;
  }

  public void uploadDormData(
      Map<String, Set<String>> roomTypes,
      Map<String, Integer> accessibilityMap) {

    for (String dormKey : roomTypes.keySet()) {
      Set<String> types = roomTypes.getOrDefault(dormKey, Set.of());

      Integer accessibility = accessibilityMap.getOrDefault(dormKey, null);
      if (accessibility == null) continue;

      DormData dormData = new DormData(
          dormKey,
          types,
          Set.of(), // bathrooms, unused for now
          PROXIMITY,
          COMMUNITIES,
          accessibility,
          List.of() // reviews placeholder
      );

      myRef.child(dormKey).setValue(dormData, null);
//          .addOnSuccessListener(aVoid ->
//              System.out.println("Dorm data uploaded for: " + dormKey))
//          .addOnFailureListener(e ->
//              System.err.println("Failed to upload dorm: " + dormKey + " -> " + e.getMessage()));
    }
  }
}
