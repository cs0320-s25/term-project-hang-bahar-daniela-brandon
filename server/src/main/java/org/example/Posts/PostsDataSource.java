package org.example.Posts;
import org.example.Posts.DormPost;
import org.example.Posts.DiningPost;
import java.util.List;

public interface PostsDataSource {
	void addDormPost(DormPost post);
	void addDiningPost(DiningPost post);

	List<DormPost> getAllDormPost();
	List<DiningPost> getAllDiningPost();

	List<String> getDormReviewsByName(String dormName);
	
}
