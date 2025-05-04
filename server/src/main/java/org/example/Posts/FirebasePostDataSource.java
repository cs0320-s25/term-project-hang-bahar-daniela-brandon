package org.example.Posts;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import com.google.api.core.ApiFuture;

public class FirebasePostDataSource implements PostsDataSource {
	// Firebase Firestore references
	private final Firestore firestore;
	private final CollectionReference dormPostsRef;
	private final CollectionReference diningPostsRef;

	public FirebasePostDataSource() throws IOException {
		String workingDirectory = System.getProperty("user.dir");
		Path firebaseConfigPath = Paths.get(workingDirectory, "src", "main", "resources", "firebase_config.json");
		FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toFile());

		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}

		this.firestore = FirestoreClient.getFirestore();
		this.dormPostsRef = firestore.collection("dorm_posts");
		this.diningPostsRef = firestore.collection("dining_posts");
	}

	@Override
	public void addPost(AbstractPost post) {
		Map<String, Object> postValues = new HashMap<>();
		postValues.put("title", post.getTitle());
		postValues.put("name", post.getName());
		postValues.put("rating", post.getRating());
		postValues.put("review", post.getReview());
		postValues.put("date", post.getDate().toString());

		try {
			if (isDormPost(post.getType())) {
				postValues.put("type", "dorm");
				ApiFuture<DocumentReference> future = dormPostsRef.add(postValues);
				try {
					DocumentReference documentReference = future.get();
					System.out.println("Dorm post saved successfully with ID: " + documentReference.getId());
				} catch (InterruptedException | ExecutionException e) {
					System.err.println("Data could not be saved: " + e.getMessage());
				}
			} else if (isDiningPost(post.getType())) {
				DiningPost diningPost = (DiningPost) post;
				postValues.put("type", "dining");
				postValues.put("meals", diningPost.getMeals());

				ApiFuture<DocumentReference> future = diningPostsRef.add(postValues);
				try {
					DocumentReference documentReference = future.get();
					System.out.println("Dining post saved successfully with ID: " + documentReference.getId());
				} catch (InterruptedException | ExecutionException e) {
					System.err.println("Data could not be saved: " + e.getMessage());
				}
			} else {
				System.err.println("Unknown post type: " + post.getType());
			}
		} catch (Exception e) {
			System.err.println("Error adding post: " + e.getMessage());
			e.printStackTrace();
		}
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
	public Integer getAverageRatingsByName(String postType) {
		List<AbstractPost> allPosts = getAllPosts();
		List<Integer> ratings = new ArrayList<>();
		String searchName = postType.toLowerCase();
		for (AbstractPost post : allPosts) {
			if (isDormPost(post.getType())) {
				DormPost dormPost = (DormPost) post;
				if (dormPost.getName().toLowerCase().contains(searchName)) {
					ratings.add(dormPost.getRating());
				}
			} else if (isDiningPost(post.getType())) {
				DiningPost diningPost = (DiningPost) post;
				if (diningPost.getName().toLowerCase().contains(searchName)) {
					ratings.add(diningPost.getRating());
				}
			}
		}
		return calculateAverage(ratings);
	}

	@Override
	public List<String> getDormReviewsByName(String dormName) {
		CompletableFuture<List<String>> future = new CompletableFuture<>();
		List<String> reviews = new ArrayList<>();

		String dormNameLower = dormName.toLowerCase();

		ApiFuture<QuerySnapshot> querySnapshot = dormPostsRef.get();

		try {
			QuerySnapshot snapshot = querySnapshot.get();

			for (DocumentSnapshot document : snapshot.getDocuments()) {
				// Get the name field from each document
				String docName = document.getString("name");

				if (docName != null && docName.toLowerCase().equals(dormNameLower)) {
					String review = document.getString("review");
					if (review != null) {
						reviews.add(review);
					}
				}
			}
			future.complete(reviews);
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("Error getting dorm reviews: " + e.getMessage());
			future.complete(reviews);
		}

		try {
			return future.get(3, TimeUnit.SECONDS);
		} catch (Exception e) {
			System.err.println("Error or timeout getting dorm reviews: " + e.getMessage());
			return reviews;
		}
	}

	// HELPER FUNCTIONS
	private List<DormPost> getAllDormPost() {
		try {
			ApiFuture<QuerySnapshot> future = dormPostsRef.get();
			List<DormPost> posts = new ArrayList<>();

			QuerySnapshot querySnapshot = future.get();
			for (DocumentSnapshot document : querySnapshot.getDocuments()) {
				try {
					String title = document.getString("title");
					String name = document.getString("name");
					Long ratingLong = document.getLong("rating");
					Integer rating = ratingLong != null ? ratingLong.intValue() : 0;
					String review = document.getString("review");
					LocalDateTime postDate = null;

					try {
						String dateStr = document.getString("date");
						if (dateStr != null) {
							postDate = LocalDateTime.parse(dateStr);
						} else {
							postDate = LocalDateTime.now();
						}
					} catch (Exception e) {
						postDate = LocalDateTime.now();
					}

					DormPost post = new DormPost(title, name, rating, review, postDate);
					posts.add(post);
				} catch (Exception e) {
					System.err.println("Error parsing dorm post: " + e.getMessage());
				}
			}

			return posts;
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("Error fetching dorm posts: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<DiningPost> getAllDiningPost() {
		try {
			ApiFuture<QuerySnapshot> future = diningPostsRef.get();
			List<DiningPost> posts = new ArrayList<>();

			QuerySnapshot querySnapshot = future.get();
			for (DocumentSnapshot document : querySnapshot.getDocuments()) {
				try {
					String title = document.getString("title");
					String name = document.getString("name");
					String meals = document.getString("meals");
					Long ratingLong = document.getLong("rating");
					Integer rating = ratingLong != null ? ratingLong.intValue() : 0;
					String review = document.getString("review");
					LocalDateTime postDate = null;

					try {
						String dateStr = document.getString("date");
						if (dateStr != null) {
							postDate = LocalDateTime.parse(dateStr);
						} else {
							postDate = LocalDateTime.now();
						}
					} catch (Exception e) {
						postDate = LocalDateTime.now();
					}

					DiningPost post = new DiningPost(title, name, meals, rating, review, postDate);
					posts.add(post);
				} catch (Exception e) {
					System.err.println("Error parsing dining post: " + e.getMessage());
				}
			}

			return posts;
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("Error fetching dining posts: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	public Boolean isDormPost(String postType) {
		return postType.equalsIgnoreCase("dorm");
	}

	public Boolean isDiningPost(String postType) {
		return postType.equalsIgnoreCase("dining");
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