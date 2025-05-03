package org.example.Handlers;

import com.google.gson.Gson;

import org.example.Posts.PostsDataSource;

import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;
import org.example.Posts.Post;

public class GetAllPostsHandler implements Route {
	private PostsDataSource dataSource;
	private Gson gson = new Gson();

	public GetAllPostsHandler(PostsDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Object handle(Request req, Response res) throws Exception {
		try {
			List<Post> allPosts = dataSource.getAllPosts();
			return gson.toJson(allPosts);
		} catch (Exception e) {
			res.status(500);
			return "Error processing request: " + e.getMessage();
		}
	}

}