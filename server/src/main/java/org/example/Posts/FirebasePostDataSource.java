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

		String postType = post.getType();

		try {
			switch (postType) {
				case "dorm":
					postValues.put("type", postType);
					ApiFuture<DocumentReference> dormFuture = dormPostsRef.add(postValues);
					try {
						DocumentReference documentReference = dormFuture.get();
						System.out.println("Dorm post saved successfully with ID: " + documentReference.getId());
					} catch (InterruptedException | ExecutionException e) {
						System.err.println("Data could not be saved: " + e.getMessage());
					}
					break;
				case "dining":
					postValues.put("type", postType);
					DiningPost diningPost = (DiningPost) post;
					postValues.put("type", post.getType());
					postValues.put("meals", diningPost.getMeals());

					ApiFuture<DocumentReference> diningFuture = diningPostsRef.add(postValues);
					try {
						DocumentReference documentReference = diningFuture.get();
						System.out.println("Dining post saved successfully with ID: " + documentReference.getId());
					} catch (InterruptedException | ExecutionException e) {
						System.err.println("Data could not be saved: " + e.getMessage());
					}

					break;
				default:
					System.err.println("Unknown post type: " + postType);

			}

		} catch (Exception e) {
			System.err.println("Error adding post: " + e.getMessage());
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

		return allPosts;
	}

	@Override
	public Integer getAverageRatingsByName(String name) {
		List<AbstractPost> allPosts = getAllPosts();
		List<Integer> ratings = new ArrayList<>();

		for (AbstractPost post : allPosts) {
			if (post.getName().toLowerCase().contains(name.toLowerCase())) {
				ratings.add(post.getRating());
			}
		}
		return calculateAverage(ratings);
	}

	@Override
	public List<String> getDormReviewsByName(String dormName) {
		List<String> reviews = new ArrayList<>();
		List<DormPost> dormPosts = getAllDormPost();
		for (DormPost post : dormPosts) {
			if (post.getName().toLowerCase().contains(dormName.toLowerCase())) {
				reviews.add(post.getReview());
			}
		}
		return reviews;
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