package org.example.Posts;

import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiningPost implements Post {
	private String hallName;
	private String meals;
	private Integer rating;
	private String review;
	private String date;

	public DiningPost(String hallName, String meals, Integer rating, String review, LocalDateTime date) {
		this.hallName = hallName;
		this.meals = meals;
		this.rating = rating;
		this.review = review;
		this.date = date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null;
		;
	}

	// Getters
	public String getHallName() {
		return hallName;
	}

	public String getMeals() {
		return meals;
	}

	public Integer getRating() {
		return rating;
	}

	public String getReview() {
		return review;
	}

	public String getDate() {
		return date;
	}

}
