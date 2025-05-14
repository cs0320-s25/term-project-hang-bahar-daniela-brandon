package org.example.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.example.Posts.PostsDataSource;

import org.example.Posts.AbstractPost;
import org.example.Posts.DiningPost;
import org.example.Posts.DormPost;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * AddPostHandler class to handle requests for adding posts.
 * This class implements the Route interface from the Spark framework.
 */
public class AddPostHandler implements spark.Route {

	private PostsDataSource dataSource;
	private Gson gson = new Gson();

	public AddPostHandler(PostsDataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Handles the request to add a new post.
	 *
	 * @param req The HTTP request object.
	 * @param res The HTTP response object.
	 * @return A JSON representation of the added post or an error message.
	 * @throws Exception If an error occurs while processing the request.
	 */
	@Override
	public Object handle(Request req, Response res) throws Exception {

		try {
			JsonObject body = gson.fromJson(req.body(), JsonObject.class);

			if (body.get("type") == null) {
				res.status(400);
				return "Missing post type: must be 'dining' or 'dorm'";
			}
			if (body.get("userID") == null) {
				res.status(400);
				return "Missing userID";
			}
			if (body.get("location") == null) {
				res.status(400);
				return "Missing location";
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
			return gson.toJson(post);

		} catch (Exception e) {
			res.status(500);
			return "Error processing request: " + e.getMessage();
		}
	}
}