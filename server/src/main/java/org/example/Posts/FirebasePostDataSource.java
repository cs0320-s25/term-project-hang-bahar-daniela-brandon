package org.example.Posts;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;

import java.util.*;
import java.util.concurrent.*;
import com.google.api.core.ApiFuture;
import com.google.api.client.http.FileContent;

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
		this.dormPostsRef = firestore.collection("dorm_posts");
		this.diningPostsRef = firestore.collection("dining_posts");
	}

	public void addPost(AbstractPost post) {
		Map<String, Object> postValues = new HashMap<>();
		String postType = post.getType();
		String location = post.getLocation();

		postValues.put("userID", post.getUserID());
		postValues.put("postID", post.getPostID());
		postValues.put("dateTime", post.getDateTime().toString());
		postValues.put("location", post.getLocation());
		postValues.put("rating", post.getRating());
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

		if (post.getImageURL() != null) {
			postValues.put("imageURL", post.getImageURL());

		}

		try {
			// Determine which collection to use based on post type
			DocumentReference locationDocRef = postType.equals("dorm") ? dormPostsRef.document(location)
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

		} catch (Exception e) {
			System.err.println("Error adding post: " + e.getMessage());
		}
	}

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
			com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetadata, content)
					.setFields("id")
					.execute();
			String imageURL = "https://drive.google.com/uc?id=" + uploadedFile.getId();
			file.delete();
			return imageURL;
		} catch (Exception e) {
			System.err.println("Error uploading image: " + e.getMessage());
		}

		return null;
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
	List<DormPost> getAllDormPost() {
		List<DormPost> posts = new ArrayList<>();

		try {
			QuerySnapshot querySnapshot = dormPostsRef.get().get();

			for (DocumentSnapshot document : querySnapshot.getDocuments()) {
				String dormLocation = document.getId();
				List<Map<String, Object>> postsList = (List<Map<String, Object>>) document.get("posts");

				if (postsList != null) {
					for (Map<String, Object> postData : postsList) {
						// Create DormPost from the map data with proper null handling
						DormPost post = createDormPostFromMap(postData, dormLocation);
						posts.add(post);
					}
				}
			}

			return posts;
		} catch (Exception e) {
			System.err.println("Error fetching dorm posts: " + e.getMessage());
			return new ArrayList<>();
		}
	}

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

	public List<DiningPost> getAllDiningPost() {
		List<DiningPost> posts = new ArrayList<>();

		try {
			QuerySnapshot querySnapshot = diningPostsRef.get().get();

			for (DocumentSnapshot document : querySnapshot.getDocuments()) {
				String diningLocation = document.getId();
				List<Map<String, Object>> postsList = (List<Map<String, Object>>) document.get("posts");

				if (postsList != null) {
					for (Map<String, Object> postData : postsList) {
						// Create DiningPost from the map data with proper null handling
						DiningPost post = createDiningPostFromMap(postData, diningLocation);
						posts.add(post);
					}
				}

			}
			return posts;
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("Error fetching dining posts: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	private DiningPost createDiningPostFromMap(Map<String, Object> postData, String diningLocation) {
		String postID = (String) postData.get("postID");
		String userID = (String) postData.get("userID");
		String postDate = (String) postData.get("dateTime");
		String title = postData.get("title") != null ? (String) postData.get("title") : " ";
		Integer rating = postData.get("rating") != null ? ((Long) postData.get("rating")).intValue() : 0;
		String content = postData.get("content") != null ? (String) postData.get("content") : " ";
		String meals = postData.get("meals") != null ? (String) postData.get("meals") : " ";
		String imageURL = postData.get("imageURL") != null ? (String) postData.get("imageURL") : " ";

		return new DiningPost(userID, postID, title, diningLocation, meals, rating, content, postDate, imageURL);
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

	private Drive createDriveService() {
		try {
			GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
					.createScoped(Collections.singleton(DriveScopes.DRIVE));

			HttpTransport httpTransport = null;
			try {
				httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			} catch (java.security.GeneralSecurityException e) {
				System.err.println("Error creating Drive service: " + e.getMessage());
				return null;
			}
			return new Drive.Builder(httpTransport, JSON_FACTORY, credential)
					.setApplicationName("BrownBnB")
					.build();

		} catch (IOException e) {
			System.err.println("Error creating Drive service: " + e.getMessage());
			return null;
		}
	}

	private String getMimeType(File file) {
		String fileName = file.getName().toLowerCase();
		if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
			return "image/jpeg";
		} else if (fileName.endsWith(".png")) {
			return "image/png";
		} else if(fileName.endsWith("jpg")){
			return "image/jpg";
		}
		else {
			return "application/octet-stream";
		}
	}

}
