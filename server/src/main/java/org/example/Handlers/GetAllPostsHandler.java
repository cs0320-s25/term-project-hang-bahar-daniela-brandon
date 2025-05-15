package org.example.Handlers;

import com.google.gson.Gson;

import org.example.Posts.PostsDataSource;

import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;
import org.example.Posts.AbstractPost;

/**
 * GetAllPostsHandler class to handle requests for retrieving all posts.
 * This class implements the Route interface from the Spark framework.
 */
public class GetAllPostsHandler implements Route {
	private PostsDataSource dataSource;
	private Gson gson = new Gson();

	public GetAllPostsHandler(PostsDataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Handles the request to retrieve all posts.
	 *
	 * @param req The HTTP request object.
	 * @param res The HTTP response object.
	 * @return A JSON representation of all posts or an error message.
	 * @throws Exception If an error occurs while processing the request.
	 */
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