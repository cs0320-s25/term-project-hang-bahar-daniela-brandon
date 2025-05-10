package org.example.Posts;

import org.example.Posts.AbstractPost;
import org.example.Posts.DiningPost;
import org.example.Posts.DormPost;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

import java.io.File;

public class MockPostsDataSource implements PostsDataSource {
	Map<String, Object> diningPosts = new HashMap<>();
	Map<String, Object> dormPosts = new HashMap<>();

	@Override
	public void addPost(AbstractPost post) {
		String type = post.getType();
		String location = post.getLocation();
		Map<String, Object> postValues = new HashMap<>();
		postValues.put("userID", post.getUserID());
		postValues.put("postID", post.getPostID());
		postValues.put("dateTime", post.getDateTime());
		postValues.put("location", post.getLocation());
		postValues.put("rating", post.getRating());
		postValues.put("type", type);
		if (post.getTitle() != null) {
			postValues.put("title", post.getTitle());
		}
		if (post.getContent() != null) {
			postValues.put("content", post.getContent());
		}
		if (type.equals("dining")) {
			this.diningPosts.put(location, postValues);
		} else if (type.equals("dorm")) {
			this.dormPosts.put(location, postValues);
		}
	}

	@Override
	public void deletePost(String userID, String postID, String location, String type) {
		Map<String, Object> posts = new HashMap<>();
		if (type.equals("dining")) {
			posts = diningPosts;
		} else if (type.equals("dorm")) {
			posts = dormPosts;
		}
		List<AbstractPost> postsForLocation = (List<AbstractPost>) posts.get(location);
		postsForLocation.removeIf(post -> post.getUserID().equals(userID) && post.getPostID().equals(postID));
	}

	@Override
	public java.util.List<AbstractPost> getAllPosts() {
		List<AbstractPost> allPosts = new ArrayList<>();
		allPosts.addAll(getAllDiningPost());
		allPosts.addAll(getAllDormPost());
		return allPosts;
	}

	public List<DormPost> getAllDormPost() {
		
		List<DormPost> dormList = new ArrayList<>();

		for (Object obj : dormPosts.values()) {
			Map<String, Object> postMap = (Map<String, Object>) obj;

			String userID = (String) postMap.get("userID");
			String postID = (String) postMap.get("postID");
			String dateTime = (String) postMap.get("dateTime");
			String location = (String) postMap.get("location");
			Integer rating = (Integer) postMap.get("rating");
			String title = (String) postMap.get("title");
			String content = (String) postMap.get("content");
			String imageURL = (String) postMap.get("imageURL");
			if (imageURL == null) {
				imageURL = " ";
			}

			DormPost post = new DormPost(userID, postID, dateTime, location, rating, title, content, imageURL);

			dormList.add(post);
		}

		return dormList;
	}

	public List<DiningPost> getAllDiningPost() {
		List<DiningPost> diningList = new ArrayList<>();

		for (Object obj : diningPosts.values()) {
			Map<String, Object> postMap = (Map<String, Object>) obj;

			String userID = (String) postMap.get("userID");
			String postID = (String) postMap.get("postID");
			String dateTime = (String) postMap.get("dateTime");
			String location = (String) postMap.get("location");
			Integer rating = (Integer) postMap.get("rating");
			String title = (String) postMap.get("title");
			String content = (String) postMap.get("content");
			String meals = (String) postMap.get("meals");
			String imageURL = (String) postMap.get("imageURL");
			if (imageURL == null) {
				imageURL = " ";
			}

			DiningPost post = new DiningPost(userID, postID, title, location, meals, rating, content, dateTime, imageURL);

			diningList.add(post);

		}

		return diningList;
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

	@Override
	public String uploadImage(File file) {
		// Mock implementation, return a dummy URL
		return "http://example.com/image.jpg";
	}

}