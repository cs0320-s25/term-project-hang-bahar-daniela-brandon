package org.example.Posts;

public class DormPost extends AbstractPost {

	public DormPost(String userID, String postID, String title, String dormName, Integer content, String review,
			String dateTime, String imageURL) {
		super(userID, postID, "dorm", title, dormName, content, review, dateTime, imageURL);

	}

}
