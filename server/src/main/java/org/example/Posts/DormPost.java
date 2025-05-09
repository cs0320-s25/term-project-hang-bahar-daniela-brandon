package org.example.Posts;

import java.time.LocalDateTime;

public class DormPost extends AbstractPost {


	public DormPost(String userID,String postID,String title, String dormName, Integer content, String review, String dateTime) {
		super(userID,postID,"dorm",title, dormName, content, review, dateTime);
	
	}

	
}
