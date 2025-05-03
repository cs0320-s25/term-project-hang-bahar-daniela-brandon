package org.example.Posts;

import java.util.List;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.nio.file.Path;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FirebasePostDataSource implements PostsDataSource {
	// Firebase database reference
	private final FirebaseDatabase database;
	private final DatabaseReference dormPostsRef;
	private final DatabaseReference diningPostsRef;

	public FirebasePostDataSource() throws IOException {

		String workingDirectory = System.getProperty("user.dir");
		// Path firebaseConfigPath = Paths.get(workingDirectory, "term-project-hang-bahar-daniela-brandon","server","src", "main", "resources", "firebase_config.json");
		Path firebaseConfigPath = Paths.get(workingDirectory, "src", "main", "resources", "firebase_config.json");
		FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toString());
		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setDatabaseUrl("https://brownbnb-e9ab8-default-rtdb.firebaseio.com/")
				.build();
		FirebaseApp.initializeApp(options);

		this.database = FirebaseDatabase.getInstance();
		this.dormPostsRef = database.getReference("dorm_posts");
		this.diningPostsRef = database.getReference("dining_posts");

	}

	@Override
	public void addDormPost(DormPost dormPost) {
		String postId = dormPostsRef.push().getKey();
		Map<String, Object> postValues = new HashMap<>();
		postValues.put("dormName", dormPost.getDormName());
		postValues.put("rating", dormPost.getRating());
		postValues.put("review", dormPost.getReview());
		postValues.put("date", dormPost.getDate());
		dormPostsRef.child(postId).setValue(postValues, (databaseError, databaseReference) -> {
			if (databaseError != null) {
				System.err.println("Data could not be saved: " + databaseError.getMessage());
			} else {
				System.out.println("Dorm post saved successfully!");
			}
		});
	}

	@Override
	public void addDiningPost(DiningPost diningPost) {
		String postId = diningPostsRef.push().getKey();
		Map<String, Object> postValues = new HashMap<>();
		postValues.put("hallName", diningPost.getHallName());
		postValues.put("meals", diningPost.getMeals());
		postValues.put("rating", diningPost.getRating());
		postValues.put("review", diningPost.getReview());
		postValues.put("date", diningPost.getDate());
		diningPostsRef.child(postId).setValue(postValues, (databaseError, databaseReference) -> {
			if (databaseError != null) {
				System.err.println("Data could not be saved: " + databaseError.getMessage());
			} else {
				System.out.println("Dining post saved successfully!");
			}
		});
	}

	@Override
	public List<DormPost> getAllDormPost() {
		// Retrieve all dorm posts from the "dorm_posts" reference
		CompletableFuture<List<DormPost>> future = new CompletableFuture<>();
		List<DormPost> posts = new ArrayList<>();

		dormPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
					// Create a DormPost from the data
					String dormName = postSnapshot.child("dormName").getValue(String.class);
					Integer rating = postSnapshot.child("rating").getValue(Integer.class);
					String review = postSnapshot.child("review").getValue(String.class);
					String postDate = postSnapshot.child("date").getValue(String.class);
					DormPost post = new DormPost(dormName, rating, review, postDate);
					posts.add(post);
				}
				future.complete(posts);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				future.completeExceptionally(databaseError.toException());
			}
		});

		try {
			return future.get();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public List<DiningPost> getAllDiningPost() {
		// Retrieve all dining posts from Firebase
		CompletableFuture<List<DiningPost>> future = new CompletableFuture<>();
		List<DiningPost> posts = new ArrayList<>();
		diningPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
					// Create a DiningPost from the data
					String hallName = postSnapshot.child("hallName").getValue(String.class);
					String meals = postSnapshot.child("meals").getValue(String.class);
					Integer rating = postSnapshot.child("rating").getValue(Integer.class);
					String review = postSnapshot.child("review").getValue(String.class);
					String postDate = postSnapshot.child("date").getValue(String.class);
					DiningPost post = new DiningPost(hallName, meals, rating, review, postDate);
					posts.add(post);
				}
				future.complete(posts);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				future.completeExceptionally(databaseError.toException());
			}
		});
		try {
			return future.get();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public List<String> getDormReviewsByName(String dormName) {
		// Retrieve dorm reviews by name from Firebase
		CompletableFuture<List<String>> future = new CompletableFuture<>();
		List<String> reviews = new ArrayList<>();
		dormPostsRef.orderByChild("dormName").equalTo(dormName)
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
							String review = postSnapshot.child("review").getValue(String.class);
							reviews.add(review);
						}
						future.complete(reviews);
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						future.completeExceptionally(databaseError.toException());
					}
				});
		try {
			return future.get();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

}
