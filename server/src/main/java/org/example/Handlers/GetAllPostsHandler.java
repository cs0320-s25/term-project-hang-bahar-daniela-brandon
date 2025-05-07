package org.example.Handlers;

import com.google.gson.Gson;

import org.example.Posts.PostsDataSource;

import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;
import org.example.Posts.AbstractPost;

public class GetAllPostsHandler implements Route {
	private PostsDataSource dataSource;
	private Gson gson = new Gson();

	public GetAllPostsHandler(PostsDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Object handle(Request req, Response res) throws Exception {
		try {
			List<AbstractPost> posts = dataSource.getAllPosts();

			if (posts == null || posts.isEmpty()) {
				res.status(404);
				return "No posts found";
			}

			return gson.toJson(posts);
		} catch (Exception e) {
			res.status(500);
			return "Error processing request: " + e.getMessage();
		}
	}

}