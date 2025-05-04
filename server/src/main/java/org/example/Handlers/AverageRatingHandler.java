package org.example.Handlers;
import com.google.gson.Gson;

import org.example.Posts.PostsDataSource;

import spark.Request;
import spark.Response;
import spark.Route;

public class AverageRatingHandler  implements Route {
	private PostsDataSource dataSource;

	public AverageRatingHandler(PostsDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Object handle(Request req, Response res) throws Exception {
		String name = req.queryParams("name");
		if (name == null || name.isEmpty()) {
			res.status(400);
			return "Missing dorm or dining hall name";
		}
		try {
			Integer rating= dataSource.getAverageRatingsByName(name);
			if (rating == null || rating == 0) {
				res.status(404);
				return "No ratings found for the specified dorm or dining hall: " + name;
			}

			return rating;

		} catch (Exception e) {
			res.status(500);
			return "Error processing request: " + e.getMessage();
		}
	}

	
}
