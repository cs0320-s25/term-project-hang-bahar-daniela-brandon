package org.example.Dorms;

// MockDormDataSource.java
import com.google.gson.JsonObject;
import java.util.*;


/**
 * This class is similar to the FirebaseDormDataSource. It has a mock list of dorms and their corresponding attributes. 
 * Includes methods to search through the dorm given the search query and match with a dorm based on user input.
 */
public class MockDormDataSource implements DormDataSource {
	private List<Dorm> dorms;

	public MockDormDataSource() {
		initializeMockData();
	}

	private void initializeMockData() {
		dorms = new ArrayList<>(); // Initialize the dorms list first

		// Goddard dorm
		Map<String, Object> post1 = new HashMap<>();
		post1.put("userID", "user1");
		post1.put("postID", "post1");
		post1.put("dateTime", "2023-10-01T12:00:00Z");
		post1.put("location", "Goddard");
		post1.put("rating", 5);
		post1.put("type", "dorm");
		post1.put("title", "Great dorm!");
		post1.put("content", "this dorm is really good and clean and nice!");
		post1.put("imageURL", "http://example.com/image1.jpg");

		Map<String, Object> post2 = new HashMap<>();
		post2.put("userID", "user2");
		post2.put("postID", "post2");
		post2.put("dateTime", "2023-10-02T12:00:00Z");
		post2.put("location", "Goddard");
		post2.put("rating", 4);
		post2.put("type", "dorm");
		post2.put("title", null); // null is allowed in HashMap
		post2.put("content", "can be loud with parties");
		post2.put("imageURL", null); // null is allowed in HashMap

		Map<String, Object> post3 = new HashMap<>();
		post3.put("userID", "user3");
		post3.put("postID", "post3");
		post3.put("dateTime", "2023-10-03T12:00:00Z");
		post3.put("location", "Goddard");
		post3.put("rating", 3);
		post3.put("type", "dorm");
		post3.put("title", null);
		post3.put("content", "highly recommend");
		post3.put("imageURL", null);

		List<Map<String, Object>> goddardPosts = new ArrayList<>();
		goddardPosts.add(post1);
		goddardPosts.add(post2);
		goddardPosts.add(post3);

		dorms.add(new Dorm(
				"Goddard",
				new HashSet<>(Arrays.asList("single", "double")),
				new HashSet<>(Arrays.asList("shared", "private")),
				new HashSet<>(Arrays.asList("Sharpe Refectory", "Main Green")),
				new HashSet<>(Arrays.asList("sorority housing")),
				3,
				"1999",
				"ddddd",
				goddardPosts));

		// Do the same for the other dorms...
		// Hegeman dorm
		Map<String, Object> hegemanPost1 = new HashMap<>();
		hegemanPost1.put("userID", "user1");
		hegemanPost1.put("postID", "post1");
		hegemanPost1.put("dateTime", "2023-10-01T12:00:00Z");
		hegemanPost1.put("location", "Hegeman");
		hegemanPost1.put("rating", 5);
		hegemanPost1.put("type", "dorm");
		hegemanPost1.put("title", null);
		hegemanPost1.put("content", "quiet housing");
		hegemanPost1.put("imageURL", null);

		Map<String, Object> hegemanPost2 = new HashMap<>();
		hegemanPost2.put("userID", "user2");
		hegemanPost2.put("postID", "post2");
		hegemanPost2.put("dateTime", "2023-10-02T12:00:00Z");
		hegemanPost2.put("location", "Hegeman");
		hegemanPost2.put("rating", 4);
		hegemanPost2.put("type", "dorm");
		hegemanPost2.put("title", null);
		hegemanPost2.put("content", "loud thayer");
		hegemanPost2.put("imageURL", null);

		List<Map<String, Object>> hegemanPosts = new ArrayList<>();
		hegemanPosts.add(hegemanPost1);
		hegemanPosts.add(hegemanPost2);

		dorms.add(new Dorm(
				"Hegeman",
				new HashSet<>(Arrays.asList("double", "3-persons suite")),
				new HashSet<>(Collections.singletonList("shared")),
				new HashSet<>(Arrays.asList("Sharpe Refectory", "Sciences Library")),
				new HashSet<>(Arrays.asList("quiet housing")),
				2,
				"1999",
				"ddddd",
				hegemanPosts));

		// Barbour dorm
		Map<String, Object> barbourPost1 = new HashMap<>();
		barbourPost1.put("userID", "user1");
		barbourPost1.put("postID", "post1");
		barbourPost1.put("dateTime", "2023-10-01T12:00:00Z");
		barbourPost1.put("location", "Barbour");
		barbourPost1.put("rating", 5);
		barbourPost1.put("type", "dorm");
		barbourPost1.put("title", null);
		barbourPost1.put("content", "so far away from everything, but suites come with kitchens and private bathroom");
		barbourPost1.put("imageURL", null);

		List<Map<String, Object>> barbourPosts = new ArrayList<>();
		barbourPosts.add(barbourPost1);

		dorms.add(new Dorm(
				"Barbour",
				new HashSet<>(Arrays.asList("4-persons suite")),
				new HashSet<>(Collections.singletonList("private")),
				new HashSet<>(Arrays.asList("TF Green Hall", "Orwig Music Library")),
				new HashSet<>(Arrays.asList("religious housing")),
				1,
				"1999",
				"ddddd",
				barbourPosts));
	}

	/**
	 * @return: returns the list of dorms
	 */

	@Override
	public List<Dorm> getAllDorms() {
		return new ArrayList<>(dorms);
	}

	/**
	 * searches through the mock dorm list
 	 * @param: word query to search for
    	 * @return: list of dorms that has the search query
	 */
	@Override
	public List<DormSearchResult> searchDorms(String query) {
		List<DormSearchResult> results = new ArrayList<>();

		for (Dorm dorm : dorms) {
			int score = calculateDormScore(dorm, query.toLowerCase());
			if (score > 0) {
				results.add(new DormSearchResult(dorm, score));
			}
		}

		// Sort by score in descending order
		results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

		return results;
	}

	@Override
	public List<DormSearchResult> matchDorms(JsonObject preferences) {
		System.out.println("entered matchdorms in mock");
		List<DormSearchResult> results = new ArrayList<>();

		for (Dorm dorm : dorms) {
			int score = calculateMatchScoreFromPreferences(dorm, preferences);
			if (score > 0) {
				results.add(new DormSearchResult(dorm, score));
			}
		}

		results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
		return results;
	}

	@Override
	public List<DormSearchResult> getInfo(JsonObject info) {
		return List.of();
	}

	private int calculateMatchScoreFromPreferences(Dorm dorm, JsonObject preferences) {
		int score = 0;

		if (preferences.has("roomType")) {
			String preferredRoom = preferences.get("roomType").getAsString();
			if (dorm.getRoomTypes().stream().anyMatch(type -> type.equalsIgnoreCase(preferredRoom))) {
				score += 10;
			}
		}

		if (preferences.has("bathroomType")) {
			String preferredBathroom = preferences.get("bathroomType").getAsString();
			if (dorm.getBathrooms().stream().anyMatch(bath -> bath.equalsIgnoreCase(preferredBathroom))) {
				score += 8;
			}
		}

		if (preferences.has("proximity")) {
			String preferredProximity = preferences.get("proximity").getAsString();
			if (dorm.getProximity().stream()
					.anyMatch(p -> p.toLowerCase().contains(preferredProximity.toLowerCase()))) {
				score += 6;
			}
		}

		if (preferences.has("community")) {
			String preferredCommunity = preferences.get("community").getAsString();
			if (dorm.getCommunities().stream().anyMatch(c -> c.equalsIgnoreCase(preferredCommunity))) {
				score += 10;
			}
		}

		if (preferences.has("accessible")) {
			boolean needsAccessible = preferences.get("accessible").getAsBoolean();
			if (needsAccessible && dorm.isAccessible()) {
				score += 10;
			}
		}

		return score;
	}

	private int calculateDormScore(Dorm dorm, String query) {
		int score = 0;

		// Check dorm name (exact match)
		if (dorm.getName().toLowerCase().equals(query)) {
			score += 50;
		}
		// Partial match in name
		else if (dorm.getName().toLowerCase().contains(query)) {
			score += 30;
		}

		for (String community : dorm.getCommunities()) {
			if (community.toLowerCase().contains(query)) {
				score += 30;
			}
		}

		// Check room types
		for (String roomType : dorm.getRoomTypes()) {
			if (roomType.toLowerCase().contains(query)) {
				score += 20;
			}
		}

		// Check bathroom types
		for (String bathroom : dorm.getBathrooms()) {
			if (bathroom.toLowerCase().contains(query)) {
				score += 10;
			}
		}

		// Check proximity
		for (String location : dorm.getProximity()) {
			if (location.toLowerCase().contains(query)) {
				score += 5;
			}
		}

		// Check reviews
		int reviewMatches = 0;
		for (String review : dorm.getReviews()) {
			if (review.toLowerCase().contains(query)) {
				reviewMatches++;
			}
		}
		score += reviewMatches * 5;

		if (query.contains("accessible") || query.contains("accessibility") && (dorm.isAccessible())) {
			score += 30;
		}

		return score;
	}
}
