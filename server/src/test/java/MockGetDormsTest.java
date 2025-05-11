import org.example.Dorms.MockDormDataSource;
import org.example.Handlers.GetDormsHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import spark.Request;
import spark.Response;

public class MockGetDormsTest {
	private MockDormDataSource dataSource;
	private GetDormsHandler getDormsHandler;

	@Mock
	private Request request;

	@Mock
	private Response response;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		// Use the actual MockDormDataSource rather than mocking it
		dataSource = new MockDormDataSource();
		getDormsHandler = new GetDormsHandler(dataSource);

	}

	public String tryRequest() {
		String result = " ";
		try {
			result = (String) getDormsHandler.handle(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Exception during handle method: " + e.getMessage());
		}
		return result;
	}

	@Test
	public void testGetDorms() {
		String result = tryRequest();
		assertTrue(result.contains("Goddard"));
		assertTrue(result.contains("Hegeman"));
		assertTrue(result.contains("Barbour"));
	}
}
