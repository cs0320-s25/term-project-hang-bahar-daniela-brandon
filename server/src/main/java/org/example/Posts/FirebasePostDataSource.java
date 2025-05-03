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
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

public class FirebasePostDataSource implements PostsDataSource {
	// Firebase database reference
	private final FirebaseDatabase database;
	private final DatabaseReference dormPostsRef;
	private final DatabaseReference diningPostsRef;

	public FirebasePostDataSource() throws IOException {

		String workingDirectory = System.getProperty("user.dir");
		// Path firebaseConfigPath = Paths.get(workingDirectory,
		// "term-project-hang-bahar-daniela-brandon","server","src", "main",
		// "resources", "firebase_config.json");
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
		postValues.put("title", dormPost.getTitle());
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
		postValues.put("title", diningPost.getTitle());
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
	public List<AbstractPost> getAllPosts() {
		// Get all dorm posts and dining posts
		List<DormPost> dormPosts = getAllDormPost();
		List<DiningPost> diningPosts = getAllDiningPost();

		// Combine into a single list of Post objects
		List<AbstractPost> allPosts = new ArrayList<>();
		allPosts.addAll(dormPosts);
		allPosts.addAll(diningPosts);

		// Sort by date
		allPosts.sort((post1, post2) -> post2.getDate().compareTo(post1.getDate())); // Most recent first

		return allPosts;
	}

	@Override
	public List<DormPost> getAllDormPost() {
		// Retrieve all dorm posts from the "dorm_posts" reference
		CompletableFuture<List<DormPost>> future = new CompletableFuture<>();
		List<DormPost> posts = new ArrayList<>();

		dormPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				try {
					for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
						// Create a DormPost from the data
						String title = postSnapshot.child("title").getValue(String.class);
						String dormName = postSnapshot.child("dormName").getValue(String.class);
						Integer rating = postSnapshot.child("rating").getValue(Integer.class);
						String review = postSnapshot.child("review").getValue(String.class);
						LocalDateTime postDate = null;
						try {
							String dateStr = postSnapshot.child("date").getValue(String.class);
							if (dateStr != null) {
								postDate = LocalDateTime.parse(dateStr);
							} else {
								postDate = LocalDateTime.now();
							}
						} catch (Exception e) {
							postDate = LocalDateTime.now();
						}
						DormPost post = new DormPost(title, dormName, rating, review, postDate);
						posts.add(post);
					}
					future.complete(posts);
				} catch (Exception e) {
					e.printStackTrace();
					future.complete(new ArrayList<>());
				}

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				System.err.println("Firebase query cancelled: " + databaseError.getMessage());
				future.complete(new ArrayList<>());
			}
		});

		try {
			return future.get(3, TimeUnit.SECONDS);
		} catch (TimeoutException te) {
			System.err.println("Timeout fetching dining posts after 3 seconds");
			return new ArrayList<>();
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
				try {
					for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
						// Create a DiningPost
						String title = postSnapshot.child("title").getValue(String.class);
						String hallName = postSnapshot.child("hallName").getValue(String.class);
						String meals = postSnapshot.child("meals").getValue(String.class);
						Integer rating = postSnapshot.child("rating").getValue(Integer.class);
						String review = postSnapshot.child("review").getValue(String.class);

						LocalDateTime postDate = null;
						try {
							// Try getting as string first
							String dateStr = postSnapshot.child("date").getValue(String.class);
							if (dateStr != null) {
								postDate = LocalDateTime.parse(dateStr);
							} else {
								// Use current time as default
								postDate = LocalDateTime.now();
							}
						} catch (Exception e) {
							// Default to current time if parsing fails
							postDate = LocalDateTime.now();
						}

						DiningPost post = new DiningPost(title, hallName, meals, rating, review, postDate);
						posts.add(post);

					}
					future.complete(posts);
				} catch (Exception e) {
					e.printStackTrace();
					future.complete(new ArrayList<>()); // Complete with empty list on error
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				System.err.println("Firebase query cancelled: " + databaseError.getMessage());
				future.complete(new ArrayList<>()); // Return empty list instead of throwing
			}
		});

		try {
			return future.get(3, TimeUnit.SECONDS);
		} catch (TimeoutException te) {
			System.err.println("Timeout fetching dining posts after 3 seconds");
			return new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public List<String> getDormReviewsByName(String dormName) {
		CompletableFuture<List<String>> future = new CompletableFuture<>();
		List<String> reviews = new ArrayList<>();

		// Get all dorm posts
		dormPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String searchName = dormName.toLowerCase();

				for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
					String dbDormName = postSnapshot.child("dormName").getValue(String.class);

					if (dbDormName != null && dbDormName.toLowerCase().contains(searchName)) {
						String review = postSnapshot.child("review").getValue(String.class);
						if (review != null) {
							reviews.add(review);
						}
					}
				}
				future.complete(reviews);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				future.complete(reviews);
			}
		});

		try {
			return future.get(3, TimeUnit.SECONDS);
		} catch (Exception e) {
			return reviews;
		}
	}

	@Override
	public Integer getAverageRatingsByName(String name) {
		CompletableFuture<Integer> future = new CompletableFuture<>();
		List<Integer> ratings = new ArrayList<>();
		String searchName = name.toLowerCase();

		// First, check dorm posts
		dormPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
					String dormName = postSnapshot.child("dormName").getValue(String.class);

					if (dormName != null && dormName.toLowerCase().contains(searchName)) {
						Integer rating = postSnapshot.child("rating").getValue(Integer.class);
						if (rating != null) {
							ratings.add(rating);
						}
					}
				}

				// After checking dorms, check dining halls
				checkDiningPostsForAverage(searchName, ratings, future);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				System.err.println("Firebase query cancelled: " + databaseError.getMessage());
				checkDiningPostsForAverage(searchName, ratings, future);
			}
		});

		try {
			// This will block until the future is completed
			return future.get(6, TimeUnit.SECONDS);
		} catch (TimeoutException te) {
			System.err.println("Timeout fetching ratings after 6 seconds");
			return calculateAverage(ratings); // Return average of what we have so far
		} catch (Exception e) {
			e.printStackTrace();
			return calculateAverage(ratings);
		}
	}

	private void checkDiningPostsForAverage(String searchName, List<Integer> ratings,
			CompletableFuture<Integer> future) {
		diningPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
					String hallName = postSnapshot.child("hallName").getValue(String.class);

					if (hallName != null && hallName.toLowerCase().contains(searchName)) {
						Integer rating = postSnapshot.child("rating").getValue(Integer.class);
						if (rating != null) {
							ratings.add(rating);
						}
					}
				}
				// Complete the future with the calculated average
				future.complete(calculateAverage(ratings));
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				System.err.println("Firebase query cancelled: " + databaseError.getMessage());
				// Complete the future with whatever ratings we have
				future.complete(calculateAverage(ratings));
			}
		});
	}

	private Integer calculateAverage(List<Integer> ratings) {
		if (ratings.isEmpty()) {
			return 0;
		}
		int sum = 0;
		for (Integer rating : ratings) {
			sum += rating;
		}
		return sum / ratings.size();
	}
}
