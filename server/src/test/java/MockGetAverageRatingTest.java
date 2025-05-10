import org.example.Posts.MockPostsDataSource;
import org.example.Handlers.AddPostHandler;
import org.example.Handlers.GetAverageRatingHandler;
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

public class MockGetAverageRatingTest {
	private MockPostsDataSource dataSource;
	private AddPostHandler addPostHandler;
	private GetAverageRatingHandler getAverageRatingHandler;

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
		// Use the actual MockDormDataSource rather than mocking it
		dataSource = new MockPostsDataSource();
		addPostHandler = new AddPostHandler(dataSource);
		getAverageRatingHandler = new GetAverageRatingHandler(dataSource);

		// Add dorm post
		JsonObject dormBody = new JsonObject();
		dormBody.addProperty("type", "dorm");
		dormBody.addProperty("postID", "123");
		dormBody.addProperty("userID", "456");
		dormBody.addProperty("location", "Goddard");
		dormBody.addProperty("rating", 4);
		dormBody.addProperty("content", "good dorm");
		dormBody.addProperty("dateTime", "2023-10-02T11:00:00");
		dormBody.addProperty("title", "Great dorm experience!");
		tryRequest(dormBody);

		JsonObject dormBody2 = new JsonObject();
		dormBody2.addProperty("postID", "124");
		dormBody2.addProperty("userID", "456");
		dormBody2.addProperty("location", "Goddard");
		dormBody2.addProperty("rating", 4);
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
		diningBody.addProperty("meals", "Pretzels");
		diningBody.addProperty("rating", 3);
		diningBody.addProperty("content", "good food");
		diningBody.addProperty("dateTime", "2023-10-02T12:00:00");
		diningBody.addProperty("title", "Decent dining experience!");
		tryRequest(diningBody);
	}

	public Integer tryRequest(String location) {
		when(request.queryParams("location")).thenReturn(location);
		Integer result = 0;
		try {
			result = (Integer) getAverageRatingHandler.handle(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Exception during handle method: " + e.getMessage());
		}
		return result;
	}

	@Test
	public void testGetAverageRating() {
		Integer result = tryRequest("Goddard");
		assertTrue(result.equals(4));
	}
}