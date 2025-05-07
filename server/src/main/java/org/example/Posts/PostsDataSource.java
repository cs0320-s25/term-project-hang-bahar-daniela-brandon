package org.example.Posts;

import java.util.List;

public interface PostsDataSource {
	void addPost(AbstractPost post);

	void deletePost(String userID, String postID, String location, String type);

	// List<DormPost> getAllDormPost();

	// List<DiningPost> getAllDiningPost();

	List<AbstractPost> getAllPosts();

	Integer getAverageRatingsByLocation(String location);

}
