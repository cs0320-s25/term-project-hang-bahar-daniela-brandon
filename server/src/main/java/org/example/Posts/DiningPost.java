package org.example.Posts;

import java.time.LocalDateTime;

public class DiningPost extends AbstractPost {
	private String hallName;
	private String meals;

	public DiningPost(String title, String hallName, String meals, Integer rating, String review, LocalDateTime date) {
		super(title, rating, review, date);
		this.hallName = hallName;
		this.meals = meals;

	}

	// Getters
	public String getHallName() {
		return hallName;
	}

	public String getMeals() {
		return meals;
	}

}
