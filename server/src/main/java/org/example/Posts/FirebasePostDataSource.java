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
	// private List<DormPost> dormPosts;
	// private List<DiningPost> diningPosts;

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
		// this.dormPosts = new ArrayList<>();
		// this.diningPosts = new ArrayList<>();
	}

	public void addPost(AbstractPost post) {
		Map<String, Object> postValues = new HashMap<>();
		postValues.put("userID", post.getUserID());
		postValues.put("postID", post.getPostID());
		postValues.put("title", post.getTitle());
		postValues.put("rating", post.getRating());
		postValues.put("content", post.getContent());
		postValues.put("dateTime", post.getDateTime().toString());

		String postType = post.getType();
		String location = post.getLocation();

		try {
			// Reference to the document with Location as the key
			DocumentReference LocationDocRef;

			switch (postType) {
				case "dorm":
					LocationDocRef = dormPostsRef.document(location);
					postValues.put("type", postType);
					break;
				case "dining":
					LocationDocRef = diningPostsRef.document(location);
					postValues.put("type", postType);

					// Add dining-specific fields
					DiningPost diningPost = (DiningPost) post;
					postValues.put("meals", diningPost.getMeals());
					break;
				default:
					System.err.println("Unknown post type: " + postType);
					return;
			}

			ApiFuture<DocumentSnapshot> future = LocationDocRef.get();
			DocumentSnapshot document = future.get();

			if (document.exists()) {
				LocationDocRef.update("posts", FieldValue.arrayUnion(postValues));
			} else {
				List<Map<String, Object>> posts = new ArrayList<>();
				posts.add(postValues);

				Map<String, Object> docData = new HashMap<>();
				docData.put("posts", posts);

				ApiFuture<WriteResult> writeResult = LocationDocRef.set(docData);
				writeResult.get();
			}
		} catch (Exception e) {
			System.err.println("Error adding post: " + e.getMessage());
		}
	}


	@Override
	public void deletePost(String userID, String postID, String location, String type) {
		try {
			CollectionReference collectionRef;
			List<AbstractPost> allPosts = getAllPosts();
			if ("dorm".equals(type)) {
				collectionRef = dormPostsRef;
			} else if ("dining".equals(type)) {
				collectionRef = diningPostsRef;
			} else {
				System.err.println("Unknown post type: " + type);
				return;
			}

			DocumentReference docRef = collectionRef.document(location);

			firestore.runTransaction(transaction -> {
				// Get the current document
				DocumentSnapshot snapshot = transaction.get(docRef).get();

				List<Map<String, Object>> posts = (List<Map<String, Object>>) snapshot.get("posts");

				if (posts == null || posts.isEmpty()) {
					System.err.println("No posts found for location: " + location);
					return null;
				}

				Map<String, Object> postToDelete = null;
				for (Map<String, Object> post : posts) {
					if (userID.equals(post.get("userID")) && postID.equals(post.get("postID"))) {
						postToDelete = post;
						allPosts.removeIf(p -> p.getPostID().equals(postID) && p.getUserID().equals(userID)
								&& p.getLocation().equals(location) && p.getType().equals(type));
						break;
					}
				}

				if (postToDelete != null) {
					transaction.update(docRef, "posts", FieldValue.arrayRemove(postToDelete));
					System.out.println("Post deleted successfully");
				} else {
					System.err.println("Post not found");
				}

				return null;
			}).get();

		} catch (Exception e) {
			System.err.println("Error deleting post: " + e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public List<AbstractPost> getAllPosts() {
		List<AbstractPost> allPosts = new ArrayList<>();
		allPosts.addAll(getAllDormPost());
		allPosts.addAll(getAllDiningPost());
		return allPosts;
	}

	@Override
	public Integer getAverageRatingsByLocation(String location) {
		List<AbstractPost> allPosts = getAllPosts();
		List<Integer> ratings = new ArrayList<>();

		for (AbstractPost post : allPosts) {
			if (post.getLocation().toLowerCase().contains(location.toLowerCase())) {
				ratings.add(post.getRating());
			}
		}
		return calculateAverage(ratings);
	}

	// HELPER FUNCTIONS
	private List<DormPost> getAllDormPost() {
		List<DormPost> posts = new ArrayList<>();

		try {
			ApiFuture<QuerySnapshot> future = dormPostsRef.get();
			QuerySnapshot querySnapshot = future.get();

			for (DocumentSnapshot document : querySnapshot.getDocuments()) {
				try {
					String dormLocation = document.getId(); // Location is now the document ID
					List<Map<String, Object>> postsList = (List<Map<String, Object>>) document.get("posts");

					if (postsList != null) {
						for (Map<String, Object> postData : postsList) {
							String userID = (String) postData.get("userID");
							String postID = (String) postData.get("postID");
							String title = (String) postData.get("title");
							Long ratingLong = (Long) postData.get("rating");
							Integer rating = ratingLong != null ? ratingLong.intValue() : 0;
							String content = (String) postData.get("content");
							LocalDateTime postDate = null;

							try {
								String dateStr = (String) postData.get("dateTime");
								if (dateStr != null) {
									postDate = LocalDateTime.parse(dateStr);
								} else {
									postDate = LocalDateTime.now();
								}
							} catch (Exception e) {
								postDate = LocalDateTime.now();
							}

							DormPost post = new DormPost(userID, postID, title, dormLocation, rating, content,
									postDate);
							posts.add(post);
						}
					}
				} catch (Exception e) {
					System.err.println("Error parsing dorm document: " + e.getMessage());
				}
			}

			return posts;
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("Error fetching dorm posts: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	public List<DiningPost> getAllDiningPost() {
		List<DiningPost> posts = new ArrayList<>();

		try {
			ApiFuture<QuerySnapshot> future = diningPostsRef.get();
			QuerySnapshot querySnapshot = future.get();

			for (DocumentSnapshot document : querySnapshot.getDocuments()) {
				try {
					String diningLocation = document.getId();
					List<Map<String, Object>> postsList = (List<Map<String, Object>>) document.get("posts");

					if (postsList != null) {
						for (Map<String, Object> postData : postsList) {
							String postID = (String) postData.get("postID");
							String userID = (String) postData.get("userID");
							String title = (String) postData.get("title");
							Long ratingLong = (Long) postData.get("rating");
							Integer rating = ratingLong != null ? ratingLong.intValue() : 0;
							String content = (String) postData.get("content");
							String meals = (String) postData.get("meals");
							LocalDateTime postDate = null;

							try {
								String dateStr = (String) postData.get("dateTime");
								if (dateStr != null) {
									postDate = LocalDateTime.parse(dateStr);
								} else {
									postDate = LocalDateTime.now();
								}
							} catch (Exception e) {
								postDate = LocalDateTime.now();
							}

							DiningPost post = new DiningPost(userID, postID, title, diningLocation, meals, rating,
									content, postDate);
							posts.add(post);
						}
					}
				} catch (Exception e) {
					System.err.println("Error parsing dining document: " + e.getMessage());
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
