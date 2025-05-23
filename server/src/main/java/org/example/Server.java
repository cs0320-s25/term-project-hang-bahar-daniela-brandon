package org.example;

import static spark.Spark.after;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.example.Dorms.AccessibilityFetcher;
import org.example.Dorms.DormDataSource;
import org.example.Dorms.DormDataSourceFactory;
import org.example.Dorms.DormRoomTypesParser;
import org.example.Dorms.DormDataSourceFactory.DataSourceType;
import org.example.Dorms.FirebaseDormDatasource;
import org.example.Handlers.GetDormsHandler;
import org.example.Handlers.MatchHandler;
import org.example.Handlers.DeletePostHandler;

import org.example.Handlers.AddPostHandler;
import org.example.Handlers.GetAllPostsHandler;
import org.example.Handlers.GetAverageRatingHandler;
import org.example.Handlers.UploadPostImageHandler;
import org.example.Posts.PostsDataSource;

import org.example.Handlers.SearchHandler;
import org.example.Posts.FirebasePostDataSource;
import spark.Filter;
import spark.Spark;

/**
 * Top Level class for our project, utilizes spark to create and maintain our
 * server.
 */
public class Server {

	public static void setUpServer() throws IOException {
		System.out.println("Starting server...");
		int port = 5678;
		Spark.port(port);
		after(
				(Filter) (request, response) -> {
					response.header("Access-Control-Allow-Origin", "*");
					response.header("Access-Control-Allow-Methods", "*");
				});

		DormDataSource mockDS = DormDataSourceFactory.createDataSource(DormDataSourceFactory.DataSourceType.MOCK);
		DormDataSource firebaseDS = DormDataSourceFactory.createDataSource(DataSourceType.FIREBASE);
		PostsDataSource postsDataSource;
		try {
			postsDataSource = new FirebasePostDataSource();
		} catch (IOException e) {
			System.err.println("Failed to initialize FirebasePostDataSource: " + e.getMessage());
			throw new RuntimeException(e); // Or handle the exception as appropriate for your application
		}
		
		Spark.get("/search", new SearchHandler(firebaseDS));
		Spark.get("/info", new GetDormsHandler(firebaseDS));
		Spark.post("/add-post", new AddPostHandler(postsDataSource));
		Spark.get("/get-posts", new GetAllPostsHandler(postsDataSource));
		Spark.post("/match", new MatchHandler(firebaseDS));
		Spark.post("/upload-image", new UploadPostImageHandler(postsDataSource));
		Spark.get("/average-rating", new GetAverageRatingHandler(postsDataSource));
		Spark.get("/delete-post", new DeletePostHandler(postsDataSource));

		Spark.init();
		Spark.awaitInitialization();
		System.out.println("Server started at http://localhost:" + port);
	}

	public static void printDormRoomTypes() {
		try {
			Map<String, Set<String>> dorm_room_types = new DormRoomTypesParser().parseDormRoomTypes(
					"/Users/danielaponce/Documents/GitHub/CSCI0320/term-project-hang-bahar-daniela-brandon/dorm.csv");
			Map<String, Integer> accessibilityMap = AccessibilityFetcher.fetchAccessibility();


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Runs Server.
	 *
	 * @param args none
	 */
	public static void main(String[] args) throws IOException {
		// FirebaseDormDatasource firebaseDormDatasource = new FirebaseDormDatasource();
		setUpServer();
		printDormRoomTypes();
	}
}
