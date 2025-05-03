package org.example.Handlers;

import com.google.gson.Gson;
import org.example.Posts.PostsDataSource;


import org.example.Posts.DormPost;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddDormPostsHandler implements Route {

	private PostsDataSource dataSource;
	private Gson gson = new Gson();

	public AddDormPostsHandler(PostsDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Object handle(Request req, Response res) throws Exception {
		String body = req.body();
		if (body == null || body.isEmpty()) {
			res.status(400);
			return "Missing request body";
		}

		try {
			// Assuming the body contains a JSON representation of a post
			DormPost post = gson.fromJson(body, DormPost.class);
			if (post.getDormName() == null || post.getDormName().isEmpty()) {
				res.status(400);
				return "Invalid Dorm name data";
			}
			if (post.getRating() < 0 || post.getRating() > 5 || post.getRating() == null
					|| post.getReview().isEmpty()) {
				res.status(400);
				return "Invalid rating value. Must be between 0 and 5.";
			}

			if (post.getReview() == null || post.getReview().isEmpty()) {
				res.status(400);
				return "Invalid review data";
			}
			if (post.getDate() == null || post.getDate().isEmpty()) {
				res.status(400);
				return "Invalid date data";
			}

			dataSource.addDormPost(post);
			res.status(201); // Created

			return gson.toJson(post);
		} catch (Exception e) {
			res.status(500);
			return "Error processing request: " + e.getMessage();
		}

	}
}