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

public class DeletePostHandler implements Route {
	private PostsDataSource dataSource;

	public DeletePostHandler(PostsDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Object handle(Request req, Response res) throws Exception {
		String postID = req.queryParams("postID");
		String userID = req.queryParams("userID");
		String location = req.queryParams("location");
		String type = req.queryParams("type");
		if (postID == null || postID.isEmpty()) {
			res.status(400);
			return "Missing postID";
		}
		if (userID == null || userID.isEmpty()) {
			res.status(400);
			return "Missing userID";
		}
		if (location == null || location.isEmpty()) {
			res.status(400);
			return "Missing location";
		}
		if (type == null || type.isEmpty()) {
			res.status(400);
			return "Missing post type: must be 'dining' or 'dorm'";
		}

		try {
			dataSource.deletePost(userID, postID, location, type);
			res.status(200);
			return "Post deleted successfully " + postID;
		} catch (Exception e) {
			res.status(500);
			return "Error processing request: " + e.getMessage();
		}
	}
}
