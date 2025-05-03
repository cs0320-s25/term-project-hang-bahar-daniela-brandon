package org.example.Posts;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DormPost implements Post {
	private String dormName;
	private Integer rating;
	private String review;
	private String date;

	public DormPost(String dormName, Integer rating, String review, LocalDateTime date) {
		this.dormName = dormName;
		this.rating = rating;
		this.review = review;
		this.date = date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null;
	}

	// Getters
	public String getDormName() {
		return dormName;
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