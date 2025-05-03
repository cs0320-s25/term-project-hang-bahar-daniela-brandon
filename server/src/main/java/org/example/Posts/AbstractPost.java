package org.example.Posts;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractPost {
	protected Integer rating;
	protected String review;
	protected String date;
	protected String title;

	public AbstractPost(String title, Integer rating, String review, LocalDateTime date) {
		this.title = title;
		this.rating = rating;
		this.review = review;
		this.date = date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null;
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

	public String getTitle() {
		return title;
	}
}
