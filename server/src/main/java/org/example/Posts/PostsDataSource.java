package org.example.Posts;

import java.util.List;

public interface PostsDataSource {
	// void addDormPost(DormPost post);

	// void addDiningPost(DiningPost post);

	void addPost(AbstractPost post);



	// List<DormPost> getAllDormPost();

	// List<DiningPost> getAllDiningPost();

	List<String> getDormReviewsByName(String dormName);

	List<AbstractPost> getAllPosts();

	Integer getAverageRatingsByName(String name);

}
