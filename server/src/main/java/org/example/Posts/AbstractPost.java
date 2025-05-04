package org.example.Posts;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractPost {
	protected Integer rating;
	protected String review;
	protected String date;
	protected String title;
	protected String type;
	protected String name;
	public AbstractPost(String type, String title, String name, Integer rating, String review, LocalDateTime date) {
		this.type = type;
		this.title = title;
		this.name = name;
		this.rating = rating;
		this.review = review;
		this.date = date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null;
	}

	public String getTitle() {
		return title;
	}
	public String getType() {
		return type;
	}

	
	public String getName() {
		return name;
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
