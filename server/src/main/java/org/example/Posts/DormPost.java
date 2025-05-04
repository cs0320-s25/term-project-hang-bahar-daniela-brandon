package org.example.Posts;

import java.time.LocalDateTime;

public class DormPost extends AbstractPost {


	public DormPost(String title, String dormName, Integer rating, String review, LocalDateTime date) {
		super("dorm",title, dormName, rating, review, date);
	
	}

	
}
