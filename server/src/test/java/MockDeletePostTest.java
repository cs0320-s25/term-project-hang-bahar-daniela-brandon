import org.example.Posts.MockPostsDataSource;
import org.example.Handlers.AddPostHandler;
import org.example.Handlers.DeletePostHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gson.JsonObject;

import spark.Request;
import spark.Response;

public class MockDeletePostTest {
	private MockPostsDataSource dataSource;
	private DeletePostHandler deletePostHandler;

	@Mock
	private Request request;

	@Mock
	private Response response;

	public String tryRequest(JsonObject body) {
		AddPostHandler addPostHandler = new AddPostHandler(dataSource);
		when(request.body()).thenReturn(body.toString());
		String result = " ";
		try {
			result = (String) addPostHandler.handle(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Exception during handle method: " + e.getMessage());
		}
		return result;
	}

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		this.dataSource = new MockPostsDataSource();
		this.deletePostHandler = new DeletePostHandler(dataSource);

		// Add dorm post
		JsonObject dormBody = new JsonObject();
		dormBody.addProperty("type", "dorm");
		dormBody.addProperty("postID", "123");
		dormBody.addProperty("userID", "456");
		dormBody.addProperty("location", "Goddard");
		dormBody.addProperty("rating", 4.5);
		dormBody.addProperty("content", "good dorm");
		dormBody.addProperty("dateTime", "2023-10-02T11:00:00");
		dormBody.addProperty("title", "Great dorm experience!");
		tryRequest(dormBody);

		JsonObject dormBody2 = new JsonObject();
		dormBody2.addProperty("postID", "124");
		dormBody2.addProperty("userID", "456");
		dormBody2.addProperty("location", "Goddard");
		dormBody2.addProperty("rating", 4.5);
		dormBody2.addProperty("content", "good dorm");
		dormBody2.addProperty("dateTime", "2023-10-02T11:00:00");
		dormBody2.addProperty("title", "Great dorm experience!");
		dormBody2.addProperty("type", "dorm");
		dormBody2.addProperty("imageURL", "https://drive.google.com/uc?id=1DrYrEQWsJGyYuOIvN0GB8JiDAYmo7d0a");
		tryRequest(dormBody2);


		// Add dining post
		JsonObject diningBody = new JsonObject();
		diningBody.addProperty("type", "dining");
		diningBody.addProperty("postID", "789");
		diningBody.addProperty("userID", "101112");
		diningBody.addProperty("location", "Ivy Room");
		diningBody.addProperty("meals","Pretzels");
		diningBody.addProperty("rating", 3.5);
		diningBody.addProperty("content", "good food");
		diningBody.addProperty("dateTime", "2023-10-02T12:00:00");
		diningBody.addProperty("title", "Decent dining experience!");
		tryRequest(diningBody);



	}

	public String tryRequest(String postID, String userID, String location, String type) {
		when(request.queryParams("postID")).thenReturn(postID);
		when(request.queryParams("userID")).thenReturn(userID);
		when(request.queryParams("location")).thenReturn(location);
		when(request.queryParams("type")).thenReturn(type);
		String result = " ";
		try {
			result = (String) deletePostHandler.handle(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Exception during handle method: " + e.getMessage());
		}
		return result;
	}

	@Test
	public void testDeletePost() {
		String result = tryRequest("123", "456", "Goddard", "dorm");
		assertTrue(result.contains("Post deleted successfully"));
		assertTrue(dataSource.getAllPosts().size()==2);
	}

}
