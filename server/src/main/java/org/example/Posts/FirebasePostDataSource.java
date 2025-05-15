package org.example.Posts;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.FileContent;

import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * FirebasePostDataSource is a class that implements the PostsDataSource
 * interface
 * to interact with Firebase Firestore for managing posts related to dorms and
 * dining.
 * It provides methods to add, delete, and retrieve posts, as well as upload
 * images to Google Drive.
 * It also includes helper for uploading images and average rating
 * calculation.
 * 
 */
public class FirebasePostDataSource implements PostsDataSource {
	// Firebase Firestore references
	private final Firestore firestore;
	private final CollectionReference dormPostsRef;
	private final CollectionReference diningPostsRef;
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private final String SERVICE_ACCOUNT_KEY_PATH;

	public FirebasePostDataSource() throws IOException {
		String workingDirectory = System.getProperty("user.dir");
		Path firebaseConfigPath = Paths.get(workingDirectory, "src", "main", "resources", "firebase_config.json");
		Path googleCredentialsPath = Paths.get(workingDirectory, "src", "main", "resources", "google_config.json");
		this.SERVICE_ACCOUNT_KEY_PATH = googleCredentialsPath.toString();
		FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toFile());

		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}

		this.firestore = FirestoreClient.getFirestore();
		this.dormPostsRef = firestore.collection("dorms");
		this.diningPostsRef = firestore.collection("dining_posts");
	}

	/**
	 * Adds a post to the appropriate collection in Firestore based on its type
	 * (dorm or dining).
	 * The post is stored as a map with relevant details.
	 *
	 * @param post The post to be added.
	 */
	public void addPost(AbstractPost post) {

		Map<String, Object> postValues = new HashMap<>();
		String postType = post.getType();
		String location = post.getLocation();
		String postID = firestore.collection("ids").document().getId();

		postValues.put("userID", post.getUserID());
		postValues.put("postID", postID);
		postValues.put("dateTime", post.getDateTime().toString());
		postValues.put("location", post.getLocation());

		postValues.put("type", postType);

		if (post.getTitle() != null) {
			postValues.put("title", post.getTitle());
		}
		if (post.getContent() != null) {
			postValues.put("content", post.getContent());
		}

		if (postType.equals("dining")) {
			DiningPost diningPost = (DiningPost) post;
			postValues.put("meals", diningPost.getMeals());
		}
		if (post.getRating() != null) {
			postValues.put("rating", post.getRating());
		}

		if (post.getImageURL() != null) {
			postValues.put("imageURL", post.getImageURL());

		}

		try {
			// Determine which collection to use based on post type
			DocumentReference locationDocRef = postType.equals("dorm")
					? dormPostsRef.document(location)
					: diningPostsRef.document(location);
			DocumentSnapshot document = locationDocRef.get().get();
			if (document.exists()) {
				locationDocRef.update("posts", FieldValue.arrayUnion(postValues));
			} else {
				// Document doesn't exist, create it with the first post
				Map<String, Object> docData = new HashMap<>();
				docData.put("posts", Arrays.asList(postValues));
				locationDocRef.set(docData).get();
			}
			locationDocRef.update("posts", FieldValue.arrayUnion(postValues));

		} catch (Exception e) {
			throw new RuntimeException("Error adding post: " + e.getMessage(), e);
		}
	}

	/**
	 * Uploads an image file to Google Drive and returns the public URL of the
	 * uploaded image.
	 *
	 * @param file The image file to be uploaded.
	 * @return The public URL of the uploaded image.
	 */
	@Override
	public String uploadImage(File file) {
		try {

			String folderID = "1OGme3Hy5p4qeqBZyKAE6YSL9wIqNbDgc";
			Drive drive = createDriveService();
			com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
			fileMetadata.setName(file.getName());
			fileMetadata.setParents(Collections.singletonList(folderID));
			String mimeType = getMimeType(file);
			FileContent content = new FileContent(mimeType, file);
			com.google.api.services.drive.model.File uploadedFile = drive.files()
					.create(fileMetadata, content)
					.setFields("id")
					.execute();
			String imageURL = "https://drive.google.com/uc?id=" + uploadedFile.getId();
			file.delete();
			return imageURL;
		} catch (Exception e) {
			throw new RuntimeException("Error uploading image: " + e.getMessage(), e);
		}
	}

	/**
	 * Deletes a post from the appropriate collection in Firestore based on its
	 * type (dorm or dining).
	 * The post is identified by its userID, postID, location, and type.
	 *
	 * @param userID   The ID of the user who created the post.
	 * @param postID   The ID of the post to be deleted.
	 * @param location The location associated with the post.
	 * @param type     The type of the post (dorm or dining).
	 */
	@Override
	public void deletePost(String userID, String postID, String location, String type) {
		try {
			CollectionReference collectionRef;
			List<AbstractPost> allPosts = getAllPosts();
			// Determine which collection to use based on post type
			if ("dorm".equals(type)) {
				collectionRef = dormPostsRef;
			} else if ("dining".equals(type)) {
				collectionRef = diningPostsRef;
			} else {
				throw new IllegalArgumentException("Unknown post type: " + type);
			}

			DocumentReference docRef = collectionRef.document(location);

			// Use a transaction to ensure atomicity
			firestore.runTransaction(transaction -> {
				DocumentSnapshot snapshot = transaction.get(docRef).get();
				List<Map<String, Object>> posts = (List<Map<String, Object>>) snapshot.get("posts");

				// Check if the posts list is null or empty
				if (posts == null || posts.isEmpty()) {
					throw new NoSuchElementException("No posts found for location: " + location);
				}

				// Find the post to delete
				for (Map<String, Object> post : posts) {
					if (userID.equals(post.get("userID")) && postID.equals(post.get("postID"))) {
						transaction.update(docRef, "posts", FieldValue.arrayRemove(post));
						allPosts.removeIf(p -> p.getPostID().equals(postID) && p.getUserID().equals(userID)
								&& p.getLocation().equals(location) && p.getType().equals(type));
						break;
					}
				}
				return null;
			}).get();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error deleting post: " + e.getMessage(), e);
		}

	}

	/**
	 * Retrieves all posts from both dorm and dining collections in Firestore.
	 *
	 * @return A list of all posts.
	 */
	@Override
	public List<AbstractPost> getAllPosts() {
		List<AbstractPost> allPosts = new ArrayList<>();
		allPosts.addAll(getAllDormPost());
		allPosts.addAll(getAllDiningPost());
		return allPosts;
	}

	/**
	 * Retrieves the average rating of posts for a given location.
	 *
	 * @param location The location for which to calculate the average rating.
	 * @return The average rating for the specified location.
	 */
	@Override
	public Integer getAverageRatingsByLocation(String location) {
		List<AbstractPost> allPosts = getAllPosts();
		List<Integer> ratings = new ArrayList<>();

		for (AbstractPost post : allPosts) {
			if (post.getLocation().toLowerCase().equals(location.toLowerCase())) {
				if (post.getRating() != null) {
					ratings.add(post.getRating());

				}

			}
		}
		return calculateAverage(ratings);
	}

	// HELPER FUNCTIONS
	/**
	 * Retrieves all dorm posts from Firestore.
	 *
	 * @return A list of all dorm posts.
	 */
	List<DormPost> getAllDormPost() {
		List<DormPost> posts = new ArrayList<>();

		try {
			var documents = dormPostsRef.get().get().getDocuments();
			// Iterate through each document in the dorms collection
			for (var doc : documents) {
				String dormLocation = doc.getId();
				List<Map<String, Object>> postsList = (List<Map<String, Object>>) doc.get("posts");

				if (postsList != null) {
					// Iterate through each post in the posts list
					for (Map<String, Object> postData : postsList) {
						// Create a DormPost object from the post data
						DormPost post = createDormPostFromMap(postData, dormLocation);
						posts.add(post);
					}
				}
			}
			return posts;
		} catch (Exception e) {
			throw new RuntimeException("Error fetching dorm posts: " + e.getMessage(), e);
		}
	}

	/**
	 * Creates a DormPost object from a map of post data.
	 *
	 * @param postData     The map containing post data.
	 * @param dormLocation The location of the dorm.
	 * @return A DormPost object created from the map data.
	 */
	private DormPost createDormPostFromMap(Map<String, Object> postData, String dormLocation) {
		String postID = (String) postData.get("postID");
		String userID = (String) postData.get("userID");
		String postDate = (String) postData.get("dateTime");
		String title = postData.get("title") != null ? (String) postData.get("title") : " ";
		Integer rating = ((Long) postData.get("rating")).intValue();
		String content = postData.get("content") != null ? (String) postData.get("content") : " ";
		String imageURL = postData.get("imageURL") != null ? (String) postData.get("imageURL") : " ";

		return new DormPost(userID, postID, title, dormLocation, rating, content, postDate, imageURL);
	}

	/**
	 * Retrieves all dining posts from Firestore.
	 *
	 * @return A list of all dining posts.
	 */
	public List<DiningPost> getAllDiningPost() {
		List<DiningPost> posts = new ArrayList<>();

		try {
			var documents = diningPostsRef.get().get().getDocuments();

			// Iterate through each document in the dining_posts collection
			for (var doc : documents) {
				String diningLocation = doc.getId();
				List<Map<String, Object>> postsList = (List<Map<String, Object>>) doc.get("posts");

				if (postsList != null) {
					// Iterate through each post in the posts list
					for (Map<String, Object> postData : postsList) {
						// Create a DiningPost object from the post data
						DiningPost post = createDiningPostFromMap(postData, diningLocation);
						posts.add(post);
					}
				}

			}
			return posts;
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException("Error fetching dining posts: " + e.getMessage(), e);
		}
	}

	/**
	 * Creates a DiningPost object from a map of post data.
	 *
	 * @param postData       The map containing post data.
	 * @param diningLocation The location of the dining.
	 * @return A DiningPost object created from the map data.
	 */
	private DiningPost createDiningPostFromMap(Map<String, Object> postData, String diningLocation) {
		String postID = (String) postData.get("postID");
		String userID = (String) postData.get("userID");
		String postDate = (String) postData.get("dateTime");
		String title = postData.get("title") != null ? (String) postData.get("title") : " ";
		Integer rating = postData.get("rating") != null ? ((Long) postData.get("rating")).intValue() : null;
		String content = postData.get("content") != null ? (String) postData.get("content") : " ";
		String meals = postData.get("meals") != null ? (String) postData.get("meals") : " ";
		String imageURL = postData.get("imageURL") != null ? (String) postData.get("imageURL") : " ";

		return new DiningPost(userID, postID, title, diningLocation, meals, rating, content, postDate, imageURL);
	}

	/**
	 * Calculates the average rating from a list of ratings.
	 *
	 * @param ratings The list of ratings.
	 * @return The average rating.
	 */
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

	/**
	 * Creates a Google Drive service using the provided service account key.
	 *
	 * @return A Drive service instance.
	 */
	private Drive createDriveService() {
		try {
			GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
					.createScoped(Collections.singleton(DriveScopes.DRIVE));

			HttpTransport httpTransport = null;
			try {
				httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			} catch (java.security.GeneralSecurityException e) {
				throw new RuntimeException("Error creating Drive service: " + e.getMessage(), e);
			}
			return new Drive.Builder(httpTransport, JSON_FACTORY, credential)
					.setApplicationName("BrownBnB")
					.build();

		} catch (IOException e) {
			throw new RuntimeException("Error creating Drive service: " + e.getMessage(), e);
		}
	}

	/**
	 * Determines the MIME type of a file based on its extension.
	 *
	 * @param file The file for which to determine the MIME type.
	 * @return The MIME type of the file.
	 */
	private String getMimeType(File file) {
		String fileName = file.getName().toLowerCase();
		if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
			return "image/jpeg";
		} else if (fileName.endsWith(".png")) {
			return "image/png";
		} else if (fileName.endsWith("jpg")) {
			return "image/jpg";
		} else {
			return "application/octet-stream";
		}
	}

}
