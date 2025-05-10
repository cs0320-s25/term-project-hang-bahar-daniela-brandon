package org.example.Posts;

public class DiningPost extends AbstractPost {
	private String meals;

	public DiningPost(String userID, String postID, String title, String location, String meals, Integer rating,
			String content, String dateTime, String imageURL) {
		super(userID, postID, "dining", title, location, rating, content, dateTime, imageURL);
		this.meals = meals;

	}

	// Getters
	public String getMeals() {
		return meals;
	}

}
