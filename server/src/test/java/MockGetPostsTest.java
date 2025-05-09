
import org.example.Posts.MockPostsDataSource;
import org.example.Handlers.AddPostHandler;
import org.example.Handlers.GetAllPostsHandler;
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

public class MockGetPostsTest {
	private MockPostsDataSource dataSource;
	private AddPostHandler addPostHandler;
	private GetAllPostsHandler getAllPostsHandler;

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
		getAllPostsHandler = new GetAllPostsHandler(dataSource);

	}


	// GET ALL POSTS TESTS
	public String tryRequest() {
		String result = " ";
		try {
			result = (String) getAllPostsHandler.handle(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Exception during handle method: " + e.getMessage());
		}
		return result;
	}

	public void tryRequest(JsonObject body) {
		when(request.body()).thenReturn(body.toString());
		try {
			addPostHandler.handle(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Exception during handle method: " + e.getMessage());
		}
		
	}

	@Test
	public void testGetAllPosts() {
		JsonObject body1 = new JsonObject();
		body1.addProperty("type", "dorm");
		body1.addProperty("postID", "123");
		body1.addProperty("userID", "456");
		body1.addProperty("location", "Goddard");
		body1.addProperty("rating", 4.5);
		body1.addProperty("content", "good dorm");
		body1.addProperty("dateTime", "2023-10-02T11:00:00");
		body1.addProperty("title", "Great dorm experience!");
		tryRequest(body1);

		String result = tryRequest();
		assertTrue(result.contains("Goddard"));
	}
}
