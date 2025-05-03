package org.example.Posts;

import java.util.List;

public interface PostsDataSource {
	void addDormPost(DormPost post);

	void addDiningPost(DiningPost post);

	List<DormPost> getAllDormPost();

	List<DiningPost> getAllDiningPost();

	List<String> getDormReviewsByName(String dormName);

	List<AbstractPost> getAllPosts();

}
