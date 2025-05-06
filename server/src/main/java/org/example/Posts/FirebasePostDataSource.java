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


   public void addPost(AbstractPost post) {
       Map<String, Object> postValues = new HashMap<>();
       postValues.put("title", post.getTitle());
       postValues.put("rating", post.getRating());
       postValues.put("review", post.getReview());
       postValues.put("date", post.getDate().toString());


       String postType = post.getType();
       String name = post.getName();


       try {
           // Reference to the document with name as the key
           DocumentReference nameDocRef;


           switch (postType) {
               case "dorm":
                   nameDocRef = dormPostsRef.document(name);
                   postValues.put("type", postType);
                   break;
               case "dining":
                   nameDocRef = diningPostsRef.document(name);
                   postValues.put("type", postType);


                   // Add dining-specific fields
                   DiningPost diningPost = (DiningPost) post;
                   postValues.put("meals", diningPost.getMeals());
                   break;
               default:
                   System.err.println("Unknown post type: " + postType);
                   return;
           }


           ApiFuture<DocumentSnapshot> future = nameDocRef.get();
           DocumentSnapshot document = future.get();


           if (document.exists()) {
               nameDocRef.update("posts", FieldValue.arrayUnion(postValues));
           } else {
               List<Map<String, Object>> posts = new ArrayList<>();
               posts.add(postValues);


               Map<String, Object> docData = new HashMap<>();
               docData.put("posts", posts);


               ApiFuture<WriteResult> writeResult = nameDocRef.set(docData);
               writeResult.get();
           }
       } catch (Exception e) {
           System.err.println("Error adding post: " + e.getMessage());
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
       List<DormPost> posts = new ArrayList<>();


       try {
           ApiFuture<QuerySnapshot> future = dormPostsRef.get();
           QuerySnapshot querySnapshot = future.get();


           for (DocumentSnapshot document : querySnapshot.getDocuments()) {
               try {
                   String dormName = document.getId(); // Name is now the document ID
                   List<Map<String, Object>> postsList = (List<Map<String, Object>>) document.get("posts");


                   if (postsList != null) {
                       for (Map<String, Object> postData : postsList) {
                           String title = (String) postData.get("title");
                           Long ratingLong = (Long) postData.get("rating");
                           Integer rating = ratingLong != null ? ratingLong.intValue() : 0;
                           String review = (String) postData.get("review");
                           LocalDateTime postDate = null;


                           try {
                               String dateStr = (String) postData.get("date");
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
                   String diningName = document.getId();
                   List<Map<String, Object>> postsList = (List<Map<String, Object>>) document.get("posts");


                   if (postsList != null) {
                       for (Map<String, Object> postData : postsList) {
                           String title = (String) postData.get("title");
                           Long ratingLong = (Long) postData.get("rating");
                           Integer rating = ratingLong != null ? ratingLong.intValue() : 0;
                           String review = (String) postData.get("review");
                           String meals = (String) postData.get("meals");
                           LocalDateTime postDate = null;


                           try {
                               String dateStr = (String) postData.get("date");
                               if (dateStr != null) {
                                   postDate = LocalDateTime.parse(dateStr);
                               } else {
                                   postDate = LocalDateTime.now();
                               }
                           } catch (Exception e) {
                               postDate = LocalDateTime.now();
                           }


                           DiningPost post = new DiningPost(title, diningName, meals, rating, review, postDate);
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
