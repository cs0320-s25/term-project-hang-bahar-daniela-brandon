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
	// AbstractPost ratty = new DiningPost("user1", "post1", "Food Options", "The
	// Ratty",
	// "Cream tomato soup, cheese pizza", 5, "Many great food options!",
	// "2023-10-01T10:00:00");
	// AbstractPost minden = new DormPost("user2", "post2", "Laundry", "Smitty B",
	// 3, "Laundry machines broken.",
	// "2023-10-02T11:00:00");
	// AbstractPost andrews = new DormPost("user1", "post1", "Roommate", "Andrews
	// Hall", 4, "Great roommate!",
	// "2023-10-02T11:00:00");
	// AbstractPost andrews2 = new DormPost("user1", "post2", "Room", "Andrews
	// Hall", 5, "in-room sink great amenity!",
	// "2023-10-02T11:00:00");
	// AbstractPost ivy = new DiningPost("user1", "post2", "Food Options", "Ivy
	// Room", "Smoothie", 5,
	// "Smoothie really goodQ", "2023-10-02T11:00:00");
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
			diningPosts.put(location, postValues);
		} else if (type.equals("dorm")) {
			dormPosts.put(location, postValues);
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

	private List<DormPost> getAllDormPost() {
		List<DormPost> dormList = this.dormPosts.values().stream().map(obj -> (DormPost) obj)
				.collect(Collectors.toList());
		return dormList;
	}

	private List<DiningPost> getAllDiningPost() {
		List<DiningPost> diningList = this.diningPosts.values().stream().map(obj -> (DiningPost) obj)
				.collect(Collectors.toList());
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