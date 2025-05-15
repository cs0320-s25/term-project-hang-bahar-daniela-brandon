package org.example.Handlers;

import org.example.Posts.PostsDataSource;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * DeletePostHandler class to handle requests for deleting posts.
 * This class implements the Route interface from the Spark framework.
 */
public class DeletePostHandler implements Route {
	private PostsDataSource dataSource;

	public DeletePostHandler(PostsDataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Handles the request to delete a post.
	 *
	 * @param req The HTTP request object.
	 * @param res The HTTP response object.
	 * @return A message indicating the result of the deletion operation.
	 * @throws Exception If an error occurs while processing the request.
	 */
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
			return "Post deleted successfully!";
		} catch (Exception e) {
			res.status(500);
			return "Error processing request: " + e.getMessage();
		}
	}
}
