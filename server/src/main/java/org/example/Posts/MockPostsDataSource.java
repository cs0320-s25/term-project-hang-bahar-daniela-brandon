package org.example.Posts;

import org.example.Posts.AbstractPost;
import org.example.Posts.DiningPost;
import org.example.Posts.DormPost;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

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
		// Create an empty list to hold the DormPost objects
		List<DormPost> dormList = new ArrayList<>();

		// Iterate through each entry in the dormPosts map
		for (Object obj : dormPosts.values()) {
			// Cast the object to Map<String, Object>
			Map<String, Object> postMap = (Map<String, Object>) obj;

			// Extract values from the map
			String userID = (String) postMap.get("userID");
			String postID = (String) postMap.get("postID");
			String dateTime = (String) postMap.get("dateTime");
			String location = (String) postMap.get("location");
			Integer rating = (Integer) postMap.get("rating");
			String title = (String) postMap.get("title");
			String content = (String) postMap.get("content");

			// Create a new DormPost using the constructor
			DormPost post = new DormPost(userID, postID, dateTime, location, rating, title, content);

			dormList.add(post);
		}

		return dormList;
	}

	public List<DiningPost> getAllDiningPost() {
		// Create an empty list and add each converted DiningPost to it
		List<DiningPost> diningList = new ArrayList<>();

		for (Object obj : diningPosts.values()) {
			// You need to convert the Map<String, Object> to a DormPost
			Map<String, Object> postMap = (Map<String, Object>) obj;

			// Extract values from the map
			String userID = (String) postMap.get("userID");
			String postID = (String) postMap.get("postID");
			String dateTime = (String) postMap.get("dateTime");
			String location = (String) postMap.get("location");
			Integer rating = (Integer) postMap.get("rating");
			String title = (String) postMap.get("title");
			String content = (String) postMap.get("content");
			String meals = (String) postMap.get("meals");

			// Create a new DormPost using the constructor
			DiningPost post = new DiningPost(userID, postID, title, location, meals, rating, content, dateTime);

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

}