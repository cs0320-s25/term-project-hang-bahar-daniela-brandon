package org.example.Posts;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
		if (post.getImageURL() != null) {
			postValues.put("imageURL", post.getImageURL());
		}

		switch (type) {
			case "dining":
				DiningPost diningPost = (DiningPost) post;
				postValues.put("meals", diningPost.getMeals());
				if (diningPosts.containsKey(location)) {
					List<Object> existingPosts = (List<Object>) diningPosts.get(location);
					existingPosts.add(postValues);
				} else {
					diningPosts.put(location, new ArrayList<>(Collections.singletonList(postValues)));
				}
				this.diningPosts.put(location, Arrays.asList(postValues));
				break;
			case "dorm":
				if (dormPosts.containsKey(location)) {
					List<Object> existingPosts = (List<Object>) dormPosts.get(location);
					existingPosts.add(postValues);
				} else {
					dormPosts.put(location, new ArrayList<>(Collections.singletonList(postValues)));
				}

				break;
			default:
				throw new IllegalArgumentException("Invalid post type: " + type);
		}

	}

	@Override
	public void deletePost(String userID, String postID, String location, String type) {
		Map<String, Object> posts = new HashMap<>();
		List<AbstractPost> allPosts = getAllPosts();
		if (type.equals("dining")) {
			posts = diningPosts;
		} else if (type.equals("dorm")) {
			posts = dormPosts;
		}
		List<Map<String, Object>> postList = (List<Map<String, Object>>) posts.get(location);
		for (Map<String, Object> postData : postList) {
			if (postData.get("userID").equals(userID) && postData.get("postID").equals(postID)) {
				postList.remove(postData);
				break;
			}
		}
		allPosts.removeIf(p -> p.getPostID().equals(postID) && p.getUserID().equals(userID)
				&& p.getLocation().equals(location) && p.getType().equals(type));
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
			for (Object postObj : (List<Object>) obj) {
				Map<String, Object> postData = (Map<String, Object>) postObj;
				String location = (String) postData.get("location");
				String postID = (String) postData.get("postID");
				String userID = (String) postData.get("userID");
				String dateTime = (String) postData.get("dateTime");
				String title = postData.get("title") != null ? (String) postData.get("title") : " ";
				Integer rating = (Integer) postData.get("rating");
				String content = postData.get("content") != null ? (String) postData.get("content") : " ";
				String imageURL = postData.get("imageURL") != null ? (String) postData.get("imageURL") : " ";

				DormPost post = new DormPost(userID, postID, dateTime, location, rating, title, content, imageURL);

				dormList.add(post);

			}

		}
		return dormList;
	}

	public List<DiningPost> getAllDiningPost() {
		List<DiningPost> diningList = new ArrayList<>();

		for (Object obj : diningPosts.values()) {
			for (Object postObj : (List<Object>) obj) {
				Map<String, Object> postData = (Map<String, Object>) postObj;
				String location = (String) postData.get("location");
				String postID = (String) postData.get("postID");
				String userID = (String) postData.get("userID");
				String dateTime = (String) postData.get("dateTime");
				String title = postData.get("title") != null ? (String) postData.get("title") : " ";
				Integer rating = (Integer) postData.get("rating");
				String content = postData.get("content") != null ? (String) postData.get("content") : " ";
				String imageURL = postData.get("imageURL") != null ? (String) postData.get("imageURL") : " ";
				String meals = postData.get("meals") != null ? (String) postData.get("meals") : " ";

				DiningPost post = new DiningPost(userID, postID, title, location, meals, rating, content, dateTime,
						imageURL);

				diningList.add(post);
			}
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
		if (file == null) {
			return null;
		}
		return "https://mock-drive.example.com/" + file.getName();
	}

}