package org.example.Posts;

/**
 * AbstractPost class representing a post with common attributes.
 * This class serves as a base class for different types of posts.
 * It contains attributes such as rating, content, dateTime, title, type,
 * location, userID, postID, and imageURL.
 * The class provides getter methods to access these attributes.
 */
public abstract class AbstractPost {
	protected Integer rating;
	protected String content;
	protected String dateTime;
	protected String title;
	protected String type;
	protected String location;
	protected String userID;
	protected String postID;
	protected String imageURL;

	public AbstractPost(String userID, String postID, String type, String title, String location, Integer rating,
			String content, String dateTime, String imageURL) {
		this.userID = userID;
		this.postID = postID;
		this.type = type;
		this.title = title;
		this.location = location;
		this.rating = rating;
		this.content = content;
		this.dateTime = dateTime;
		this.imageURL = imageURL;
	}

	public String getTitle() {
		return title;
	}
	public String getType() {
		return type;
	}
	public String getLocation() {
		return location;
	}
	public Integer getRating() {
		return rating;
	}
	public String getContent() {
		return content;
	}
	public String getDateTime() {
		return dateTime;
	}
	public String getUserID() {
		return userID;
	}
	public String getPostID() {
		return postID;
	}
	public String getImageURL() {
		return imageURL;
	}

}
