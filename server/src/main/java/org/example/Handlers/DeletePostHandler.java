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
		String body = req.body();
		JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

		if (jsonObject.get("userID") == null) {
			res.status(400);
			return "Missing user ID";
		}
		if (jsonObject.get("type") == null) {
			res.status(400);
			return "Missing post type: must be 'dining' or 'dorm'";
		}
		if (jsonObject.get("location") == null) {
			res.status(400);
			return "Missing name of dorm or dining hall";
		}
		if (jsonObject.get("postID") == null) {
			res.status(400);
			return "Missing post ID";
		}

		String userID = jsonObject.get("userID").getAsString();
		String postID = jsonObject.get("postID").getAsString();
		String type = jsonObject.get("type").getAsString();
		String location = jsonObject.get("location").getAsString();

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
