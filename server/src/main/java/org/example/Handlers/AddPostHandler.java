package org.example.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.example.Posts.PostsDataSource;

import org.example.Posts.AbstractPost;
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

		try {
			JsonObject body = gson.fromJson(req.body(), JsonObject.class);

			if (body.get("type") == null) {
				res.status(400);
				return "Missing post type: must be 'dining' or 'dorm'";
			}
			if (body.get("postID") == null) {
				res.status(400);
				return "Missing postID";
			}
			if (body.get("userID") == null) {
				res.status(400);
				return "Missing userID";
			}
			if (body.get("location") == null) {
				res.status(400);
				return "Missing location";
			}
			if (body.get("rating") == null) {
				res.status(400);
				return "Missing rating";
			}

			AbstractPost post;
			String type = body.get("type").getAsString();

			switch (type) {
				case "dining":
					post = gson.fromJson(body, DiningPost.class);
					break;
				case "dorm":
					post = gson.fromJson(body, DormPost.class);
					break;
				default:
					res.status(400);
					return "Unknown post type: " + type;
			}

			dataSource.addPost(post);
			return "Post added successfully: " + gson.toJson(post);

		} catch (Exception e) {
			res.status(500);
			return "Error processing request: " + e.getMessage();
		}
	}
}