package org.example.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.example.Posts.PostsDataSource;

import org.example.Posts.DiningPost;
import org.example.Posts.DormPost;

import spark.Request;
import spark.Response;
import spark.Route;

public class AddPostHandler implements spark.Route {

	private PostsDataSource dataSource;
	private Gson gson = new Gson();

	public AddPostHandler(PostsDataSource dataSource) {
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
			JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
			String postOption = jsonObject.get("option").getAsString();

			if (postOption == null || postOption.isEmpty()) {
				res.status(400);
				return "Missing post option";
			}

			if (postOption.equals("dining")) {
				return handleDiningPost(body, res);
			} else if (postOption.equals("dorm")) {
				return handleDormPost(body, res);
			} else {
				res.status(400);
				return "Invalid post option";
			}

		} catch (Exception e) {
			res.status(500);
			return "Error processing request: " + e.getMessage();
		}

	}

	private Object handleDormPost(String body, Response res) {
		// Handle dining post
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
		if (post.getDate() == null) {
			res.status(400);
			return "Invalid date data";
		}

		dataSource.addDormPost(post);
		res.status(201); // Created

		return gson.toJson(post);
	}

	private Object handleDiningPost(String body, Response res) {
		// Handle dining post
		DiningPost post = gson.fromJson(body, DiningPost.class);
		if (post.getHallName() == null || post.getHallName().isEmpty()) {
			res.status(400);
			return "Invalid Dining Hall name data";
		}

		if (post.getMeals() == null || post.getMeals().isEmpty()) {
			res.status(400);
			return "Invalid meal data";
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
		if (post.getDate() == null) {
			res.status(400);
			return "Invalid date data";
		}

		dataSource.addDiningPost(post);
		res.status(201); // Created

		return gson.toJson(post);
	}

}