package org.example.Posts;

import java.time.LocalDateTime;

public class DiningPost extends AbstractPost {
	private String meals;
	

	public DiningPost(String userID,String postID,String title, String location, String meals, Integer rating, String content, String dateTime) {
		super(userID,postID,"dining",title, location,rating, content, dateTime);
		this.meals = meals;

	}

	// Getters
	public String getMeals() {
		return meals;
	}

}
