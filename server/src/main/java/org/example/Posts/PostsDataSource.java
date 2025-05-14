package org.example.Posts;

import java.util.List;
import java.io.File;

/**
 * PostsDataSource interface defining methods for managing posts.
 * This interface provides methods to add, delete, and retrieve posts,
 * as well as upload images and calculate average ratings.
 */
public interface PostsDataSource {
	void addPost(AbstractPost post);
	String uploadImage(File file);

	void deletePost(String userID, String postID, String location, String type);

	List<AbstractPost> getAllPosts();

	Integer getAverageRatingsByLocation(String location);

}
