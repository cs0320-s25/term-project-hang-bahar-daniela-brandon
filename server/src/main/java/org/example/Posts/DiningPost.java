package org.example.Posts;

import java.time.LocalDateTime;

public class DiningPost extends AbstractPost {
	private String meals;

	public DiningPost(String title, String hallName, String meals, Integer rating, String review, LocalDateTime date) {
		super("dining",title, hallName,rating, review, date);
		this.meals = meals;

	}

	// Getters
	public String getMeals() {
		return meals;
	}

}
