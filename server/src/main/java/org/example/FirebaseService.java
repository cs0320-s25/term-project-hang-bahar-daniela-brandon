package org.example;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;

/**
 * FirebaseService class to initialize and manage Firebase database connection.
 * This class is responsible for setting up the Firebase application
 * and providing access to the Firebase database instance.
 */
public class FirebaseService {
  private FirebaseDatabase database;

  public FirebaseService() throws IOException {
    // Initialize the app
    FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .setDatabaseUrl("https://your-project.firebaseio.com")
        .build();

    FirebaseApp.initializeApp(options);

    // Get database instance
    this.database = FirebaseDatabase.getInstance();
  }

  public FirebaseDatabase getDatabase() {
    return database;
  }
}