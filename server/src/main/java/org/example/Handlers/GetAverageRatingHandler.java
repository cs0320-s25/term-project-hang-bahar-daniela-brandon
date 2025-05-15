package org.example.Handlers;

import com.google.gson.Gson;

import org.example.Posts.PostsDataSource;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * GetAverageRatingHandler class to handle requests for retrieving average
 * ratings for dorms and dining halls.
 * This class implements the Route interface from the Spark framework.
 */
public class GetAverageRatingHandler implements Route {
	private PostsDataSource dataSource;

	public GetAverageRatingHandler(PostsDataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Handles the request to retrieve average ratings for a specific location.
	 *
	 * @param req The HTTP request object.
	 * @param res The HTTP response object.
	 * @return A JSON representation of the average rating or an error message.
	 * @throws Exception If an error occurs while processing the request.
	 */
	@Override
	public Object handle(Request req, Response res) throws Exception {
		String location = req.queryParams("location");
		if (location == null || location.isEmpty()) {
			res.status(400);
			return "Missing dorm or dining hall location";
		}
		try {
			return dataSource.getAverageRatingsByLocation(location);

		} catch (Exception e) {
			res.status(500);
			return "Error processing request: " + e.getMessage();
		}
	}

}
