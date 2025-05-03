package org.example.Posts;

import java.time.LocalDateTime;

public class DormPost extends AbstractPost {
	private String dormName;

	public DormPost(String title, String dormName, Integer rating, String review, LocalDateTime date) {
		super(title, rating, review, date);
		this.dormName = dormName;
	}

	// Getters
	public String getDormName() {
		return dormName;
	}
}
