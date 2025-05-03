package org.example.Handlers;

import com.google.gson.Gson;

import org.example.Posts.PostsDataSource;

import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

// gets a list of reviews for a dorm
public class GetDormReviews implements Route {
	private PostsDataSource dataSource;
	private Gson gson = new Gson();

	public GetDormReviews(PostsDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Object handle(Request req, Response res) throws Exception {
		String dormName = req.queryParams("name");
		if (dormName == null || dormName.isEmpty()) {
			res.status(400);
			return "Missing dorm name";
		}
		try {
			List<String> reviews = dataSource.getDormReviewsByName(dormName);
			if (reviews == null || reviews.isEmpty()) {
				res.status(404);
				return "No reviews found for the specified dorm";
			}

			return gson.toJson(reviews);

		} catch (Exception e) {
			res.status(500);
			return "Error processing request: " + e.getMessage();
		}
	}
}