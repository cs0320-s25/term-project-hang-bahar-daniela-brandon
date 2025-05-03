
// import org.example.Handlers.SearchHandler;
// import org.example.MockDormDataSource;

// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import static org.mockito.Mockito.when;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;


// import spark.Request;
// import spark.Response;

// public class MockSearchTest {

// 	private MockDormDataSource dataSource;
// 	public SearchHandler searchHandler;

// 	@Mock
// 	private Request request;

// 	@Mock
// 	private Response response;

// 	@BeforeEach
// 	public void setUp() {
// 		MockitoAnnotations.openMocks(this);
// 		// Use the actual MockDormDataSource rather than mocking it
// 		dataSource = new MockDormDataSource();
// 		searchHandler = new SearchHandler(dataSource);
// 	}

// 	public String tryRequest(String query) {
// 		when(request.queryParams("query")).thenReturn(query);
// 		String result = " ";
// 		try {
// 			result = (String) searchHandler.handle(request, response);
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 			Assertions.fail("Exception during handle method: " + e.getMessage());
// 		}
// 		return result;
// 	}

// 	@Test
// 	public void testSearch() {
// 		// Set up the request and response objects
// 		String result = tryRequest("quiet");

// 		Assertions.assertTrue(result.contains("Hegeman"));
// 	}

// 	@Test
// 	public void testSearchEmptyQuery() {
// 		String result = tryRequest("");
// 		Assertions.assertTrue(result.contains("Missing query parameter"));
// 	}

// 	@Test
// 	public void testSearchNullQuery() {
// 		String result = tryRequest(null);
// 		Assertions.assertTrue(result.contains("Missing query parameter"));
// 	}
	
// 	@Test
// 	public void testSearchNonexistent() {
// 		String result = tryRequest("quiet@#$%");
// 		Assertions.assertTrue(result.contains("No dorms found for the given query:quiet@#$%"));
// 	}

// 	@Test
// 	public void testSearchMultipleResults() {
// 		String result = tryRequest("Sharpe Refectory");
// 		Assertions.assertTrue(result.contains("Hegeman"));
// 		Assertions.assertTrue(result.contains("Goddard"));
// 	}

// }
