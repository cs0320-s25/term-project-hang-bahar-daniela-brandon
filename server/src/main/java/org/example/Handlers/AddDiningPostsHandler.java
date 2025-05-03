package org.example.Handlers;

public class AddDiningPostsHandler implements spark.Route {

	private org.example.Posts.PostsDataSource dataSource;
	private com.google.gson.Gson gson = new com.google.gson.Gson();

	public AddDiningPostsHandler(org.example.Posts.PostsDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Object handle(spark.Request req, spark.Response res) throws Exception {
		String body = req.body();
		return body;
	}
}
