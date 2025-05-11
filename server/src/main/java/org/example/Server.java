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
		// fetches the data at http://localhost:3232/search?query=quiet Or
		// /info?query=getAllDorms
		Spark.get("/search", new SearchHandler(firebaseDS));
		System.out.print("calling info");
		Spark.get("/info", new GetDormsHandler(firebaseDS));
		Spark.post("/add-post", new AddPostHandler(postsDataSource));
		Spark.get("/get-posts", new GetAllPostsHandler(postsDataSource));
		Spark.post("/match", new MatchHandler(firebaseDS));
		Spark.post("/upload-image", new UploadPostImageHandler(postsDataSource));
		Spark.get("/average-rating", new GetAverageRatingHandler(postsDataSource));
		Spark.delete("/delete-post", new DeletePostHandler(postsDataSource));
		// Spark.get("/get-dorms", new GetDormsHandler(firebaseDS));
		// Spark.get("/get-dining-halls", new GetDiningHallsHandler(firebaseDS));
		// Spark.get("/get-dining-halls", new GetDiningHallsHandler(firebaseDS));

		// Spark.get(
		// "/",
		// (request, response) -> {
		// return "Welcome to the server!";
		// });

		// Spark.get(
		// "/hello",
		// (request, response) -> {
		// return "Hello, World!";
		// });

		// Spark.notFound(
		// (request, response) -> {
		// response.status(404); // Not Found
		// System.err.println("ERROR");
		// return "404 Not Found - The requested endpoint does not exist.";
		// });

		Spark.init();
		Spark.awaitInitialization();
		System.out.println("Server started at http://localhost:" + port);
	}

	public static void printDormRoomTypes() {
		try {

			// Map<String, Set<String>> dorm_room_types = new
			// DormRoomTypesParser().parseDormRoomTypes(
			// "/Users/hangnguyen/Desktop/Academics/CS320/term-project-hang-bahar-daniela-brandon/dorm.csv");
			Map<String, Set<String>> dorm_room_types = new DormRoomTypesParser().parseDormRoomTypes(
					"/Users/bahar/Desktop/dorm.csv");
			Map<String, Integer> accessibilityMap = AccessibilityFetcher.fetchAccessibility();

			// System.out.println("printing dorm accessibility");
			// for (Map.Entry<String, Integer> entry : accessibilityMap.entrySet()) {
			// System.out.println(entry.getKey() + " => " + entry.getValue());
			// }
			//
			// for (Map.Entry<String, Set<String>> entry : dorm_room_types.entrySet()) {
			// System.out.println(entry.getKey() + " => " + entry.getValue());
			// }

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
       FirebaseDormDatasource firebaseDormDatasource = new FirebaseDormDatasource();
       setUpServer();
       printDormRoomTypes();
   }
}
