
import org.example.Posts.MockPostsDataSource;
import org.example.Handlers.AddPostHandler;
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

public class MockAddPostTest {
	private MockPostsDataSource dataSource;
	private AddPostHandler addPostHandler;

	@Mock
	private Request request;

	@Mock
	private Response response;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		// Use the actual MockDormDataSource rather than mocking it
		dataSource = new MockPostsDataSource();
		addPostHandler = new AddPostHandler(dataSource);

	}

	// ADD POST TESTS
	public String tryRequest(JsonObject body) {
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

	@Test
	public void testAddDormPost() {
		JsonObject body = new JsonObject();
		body.addProperty("type", "dorm");
		body.addProperty("postID", "123");
		body.addProperty("userID", "456");
		body.addProperty("location", "Goddard");
		body.addProperty("rating", 4.5);
		body.addProperty("content", "good dorm");
		body.addProperty("dateTime", "2023-10-02T11:00:00");
		body.addProperty("title", "Great dorm experience!");

		String result = tryRequest(body);
		assertTrue(result.contains("Post added successfully"));
	}

	@Test
	public void testAddDiningPost() {
		JsonObject body = new JsonObject();
		body.addProperty("type", "dining");
		body.addProperty("postID", "789");
		body.addProperty("userID", "101112");
		body.addProperty("location", "Ivy Room");
		body.addProperty("rating", 3.5);
		body.addProperty("content", "good food");
		body.addProperty("dateTime", "2023-10-02T12:00:00");
		body.addProperty("title", "Decent dining experience!");

		String result = tryRequest(body);
		assertTrue(result.contains("Post added successfully"));
	}

	// Test cases for missing fields

	@Test
	public void testAddPostMissingType() {
		JsonObject body = new JsonObject();
		body.addProperty("postID", "123");
		body.addProperty("userID", "456");
		body.addProperty("location", "Goddard");
		body.addProperty("rating", 4.5);
		body.addProperty("content", "good dorm");
		body.addProperty("dateTime", "2023-10-02T11:00:00");
		body.addProperty("title", "Great dorm experience!");

		String result = tryRequest(body);
		assertTrue(result.contains("Missing post type"));
	}


	@Test
	public void testAddPostMissingUserID() {
		JsonObject body = new JsonObject();
		body.addProperty("type", "dorm");
		body.addProperty("postID", "123");
		body.addProperty("location", "Goddard");
		body.addProperty("rating", 4.5);
		body.addProperty("content", "good dorm");
		body.addProperty("dateTime", "2023-10-02T11:00:00");
		body.addProperty("title", "Great dorm experience!");

		String result = tryRequest(body);
		assertTrue(result.contains("Missing userID"));
	}

	@Test
	public void testAddPostMissingLocation() {
		JsonObject body = new JsonObject();
		body.addProperty("type", "dorm");
		body.addProperty("postID", "123");
		body.addProperty("userID", "456");
		body.addProperty("rating", 4.5);
		body.addProperty("content", "good dorm");
		body.addProperty("dateTime", "2023-10-02T11:00:00");
		body.addProperty("title", "Great dorm experience!");

		String result = tryRequest(body);
		assertTrue(result.contains("Missing location"));
	}



	@Test
	public void testAddPostMissingContent() {
		JsonObject body = new JsonObject();
		body.addProperty("type", "dorm");
		body.addProperty("postID", "123");
		body.addProperty("userID", "456");
		body.addProperty("location", "Goddard");
		body.addProperty("rating", 4.5);
		body.addProperty("dateTime", "2023-10-02T11:00:00");
		body.addProperty("title", "Great dorm experience!");

		String result = tryRequest(body);
		assertTrue(result.contains("Post added successfully"));
	}

	@Test
	public void testAddPostMissingTitle() {
		JsonObject body = new JsonObject();
		body.addProperty("type", "dorm");
		body.addProperty("postID", "123");
		body.addProperty("userID", "456");
		body.addProperty("location", "Goddard");
		body.addProperty("rating", 4.5);
		body.addProperty("content", "good dorm");
		body.addProperty("dateTime", "2023-10-02T11:00:00");

		String result = tryRequest(body);
		assertTrue(result.contains("Post added successfully"));
	}

	@Test
	public void testAddPostInvalidType() {
		JsonObject body = new JsonObject();
		body.addProperty("type", "invalid");
		body.addProperty("postID", "123");
		body.addProperty("userID", "456");
		body.addProperty("location", "Goddard");
		body.addProperty("rating", 4.5);
		body.addProperty("content", "good dorm");
		body.addProperty("dateTime", "2023-10-02T11:00:00");
		body.addProperty("title", "Great dorm experience!");

		String result = tryRequest(body);
		assertTrue(result.contains("Unknown post type"));
	}

	

}
