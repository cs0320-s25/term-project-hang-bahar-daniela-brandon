package org.example.Dorms;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.JsonObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.tartarus.snowball.ext.englishStemmer;

/**
 * A data source implementation that connects to a Firestore backend to store and retrieve dorm data.
 * It handles real-time updates, search, and matching functionality.
 */
public class FirebaseDormDatasource implements DormDataSource {

	private final Firestore firestore;
	private final CollectionReference dormsRef;
	private List<Dorm> cachedDorms = new ArrayList<>();
	private boolean initialized = false;

	/**
     	* Initializes Firebase connection, sets up Firestore listener, and loads initial dorm data.
     	*
     	* @throws IOException if Firebase credentials are not found or readable
     	*/

	public FirebaseDormDatasource() throws IOException {
		// Initialize Firebase connection
		String workingDirectory = System.getProperty("user.dir");
		Path firebaseConfigPath = Paths.get(workingDirectory, "src", "main", "resources",
				"firebase_config.json");
		FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toFile());

		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}

		this.firestore = FirestoreClient.getFirestore();
		this.dormsRef = firestore.collection("dorms");

		// Initialize the Firestore listener to keep cached dorms updated
		initializeFirestoreListener();

		// Load accessibility and room type data, then upload to Firestore
		loadAndUploadDormData();
	}

	private void loadAndUploadDormData() {
		try {
			// Fetch accessibility info (keys are standardized names)
			Map<String, Integer> accessibilityMap = AccessibilityFetcher.fetchAccessibility();

			// Fetch description and year built info
			Map<String, Map<String, String>> dormInfoMap = DormInfoFetcher.fetchDormInfo();

			// Parse room types CSV (keys are also standardized names)
			DormRoomTypesParser parser = new DormRoomTypesParser();
			Map<String, Set<String>> roomTypes = parser.parseDormRoomTypes(
					"/Users/danielaponce/Documents/GitHub/CSCI0320/term-project-hang-bahar-daniela-brandon/dorm.csv");

			// Upload data to Firebase
			uploadDormData(roomTypes, accessibilityMap, dormInfoMap);

		} catch (Exception e) {
			System.err.println("Error loading and uploading dorm data: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	* Uploads dorm data from external sources (CSV and JSON) to Firestore.
	* Data includes accessibility, room types, descriptions, and proximity.
	*/

	private void uploadDormData(Map<String, Set<String>> roomTypes, Map<String, Integer> accessibilityMap,
			Map<String, Map<String, String>> dormInfoMap) {
		// For each dorm in the room types map
		try {
			var documents = dormsRef.get().get().getDocuments();
			if (!documents.isEmpty()) {
				System.out.println("Data already exists in Firestore. Skipping upload.");
				return;
			}
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("Failed to retrieve documents: " + e.getMessage());
			return;
		}
		for (Map.Entry<String, Set<String>> entry : roomTypes.entrySet()) {
			String dormName = entry.getKey();
			Set<String> dormRoomTypes = entry.getValue();

			// Create a new dorm object
			Map<String, Object> dormData = new HashMap<>();
			dormData.put("dormName", dormName);
			dormData.put("roomType", new ArrayList<>(dormRoomTypes));

			// Add accessibility score if available
			if (accessibilityMap.containsKey(dormName)) {
				dormData.put("accessibility", accessibilityMap.get(dormName));
			} else {
				dormData.put("accessibility", "No Accessibility Info Available for " + dormName); // Default value
			}

			// Add dorm info map if available
			if (dormInfoMap.containsKey(dormName)) {
				dormData.put("description", dormInfoMap.get(dormName).get("description"));
				dormData.put("built", dormInfoMap.get(dormName).get("built"));
			} else {
				dormData.put("description", "No description"); // Default value
				dormData.put("built", "Unknown"); // Default value
			}

			// Add proximity and template
			dormData.put("proximity", new ArrayList<>(ProximityAssigner.getProximity(dormName)));
			dormData.put("Community", new ArrayList<>(CommunityHouseAssigner.getCommunity(dormName)));

			// Placeholder fields
			dormData.put("bathrooms", new ArrayList<String>());
			// dormData.put("reviews", new ArrayList<String>());

			// Add to Firestore using dorm name as document ID
			dormsRef.document(dormName).set(dormData);
		}
	}

	/**
	* Sets up a real-time listener on Firestore to keep the local dorm list up to date.
	*/
	private void initializeFirestoreListener() {
		dormsRef.addSnapshotListener((snapshots, error) -> {
			if (error != null) {
				System.err.println("Firestore listen failed: " + error.getMessage());
				return;
			}

			if (snapshots != null) {
				List<Dorm> loadedDorms = new ArrayList<>();
				for (var doc : snapshots.getDocuments()) {
					try {
						String name = doc.getString("dormName");

						// Convert to HashSet for roomTypes
						List<Object> roomTypeObjects = (List<Object>) doc.get("roomType");
						Set<String> roomTypes = new HashSet<>();
						if (roomTypeObjects != null) {
							for (Object obj : roomTypeObjects) {
								roomTypes.add(obj.toString());
							}
						}

						// Convert to HashSet for communities
						List<Object> communityObjects = (List<Object>) doc.get("Community");
						Set<String> communities = new HashSet<>();
						if (communityObjects != null) {
							for (Object obj : communityObjects) {
								communities.add(obj.toString());
							}
						}

						// Convert to HashSet for bathrooms
						List<Object> bathroomObjects = (List<Object>) doc.get("bathrooms");
						Set<String> bathrooms = new HashSet<>();
						if (bathroomObjects != null) {
							for (Object obj : bathroomObjects) {
								bathrooms.add(obj.toString());
							}
						}

						Integer accessibility = doc.getLong("accessibility") != null
								? doc.getLong("accessibility").intValue()
								: 0;

						// Convert to HashSet for proximity
						List<Object> proximityObjects = (List<Object>) doc.get("proximity");
						Set<String> proximity = new HashSet<>();
						if (proximityObjects != null) {
							for (Object obj : proximityObjects) {
								proximity.add(obj.toString());
							}
						}

						// Convert to List for reviews
						List<Map<String, Object>> reviewObjects = (List<Map<String, Object>>) doc.get("posts");
						// List<String> reviews = new ArrayList<>();
						// if (reviewObjects != null) {
						// for (Object obj : reviewObjects) {
						// reviews.add(obj.toString());
						// }
						// }

						// Get description and year built
						String built = doc.getString("built");
						String description = doc.getString("description");

						Dorm dorm = new Dorm(name, roomTypes, bathrooms, proximity, communities, accessibility, built,
								description, reviewObjects);
						loadedDorms.add(dorm);
						// System.out.println(description);
						System.out.println("Successfully loaded dorm: " + name);
					} catch (Exception e) {
						System.err.println("Error parsing dorm document: " + e.getMessage());
						e.printStackTrace();
					}
				}
				cachedDorms = loadedDorms;
				initialized = true;
				System.out.println("Loaded " + cachedDorms.size() + " dorms from Firestore.");
			}
		});
	}

	/**
	* Retrieves all dorms from Firestore, using cached list if available.
	*
	* @return a list of Dorm objects
	*/
	@Override
	public List<Dorm> getAllDorms() {
		System.out.println("Fetching all dorms from Firestore.");
		if (!initialized) {
			// If cache isn't ready, fetch directly
			try {
				var documents = dormsRef.get().get().getDocuments();
				List<Dorm> result = new ArrayList<>();

				for (var doc : documents) {
					try {
						String name = doc.getString("dormName");

						// Convert to HashSet for roomTypes
						List<Object> roomTypeObjects = (List<Object>) doc.get("roomType");
						Set<String> roomTypes = new HashSet<>();
						if (roomTypeObjects != null) {
							for (Object obj : roomTypeObjects) {
								roomTypes.add(obj.toString());
							}
						}

						// Convert to HashSet for communities
						List<Object> communityObjects = (List<Object>) doc.get("Community");
						Set<String> communities = new HashSet<>();
						if (communityObjects != null) {
							for (Object obj : communityObjects) {
								communities.add(obj.toString());
							}
						}

						// Convert to HashSet for bathrooms
						List<Object> bathroomObjects = (List<Object>) doc.get("bathrooms");
						Set<String> bathrooms = new HashSet<>();
						if (bathroomObjects != null) {
							for (Object obj : bathroomObjects) {
								bathrooms.add(obj.toString());
							}
						}

						Integer accessibility = doc.getLong("accessibility") != null
								? doc.getLong("accessibility").intValue()
								: 0;

						// Convert to HashSet for proximity
						List<Object> proximityObjects = (List<Object>) doc.get("proximity");
						Set<String> proximity = new HashSet<>();
						if (proximityObjects != null) {
							for (Object obj : proximityObjects) {
								proximity.add(obj.toString());
							}
						}

						// Convert to List for reviews
						List<Map<String, Object>> reviewObjects = (List<Map<String, Object>>) doc.get("reviews");

						// Get description and year built
						String built = doc.getString("built");
						String description = doc.getString("description");

						Dorm dorm = new Dorm(name, roomTypes, bathrooms, proximity, communities, accessibility, built,
								description, reviewObjects);
						result.add(dorm);
					} catch (Exception e) {
						System.err.println("Error parsing dorm: " + e.getMessage());
						e.printStackTrace();
					}
				}
				return result;
			} catch (InterruptedException | ExecutionException e) {
				System.err.println("Error fetching dorms: " + e.getMessage());
				e.printStackTrace();
				return new ArrayList<>();
			}
		}

		// Return cached dorms if initialized
		return new ArrayList<>(cachedDorms);
	}

	

    /**
     * Searches all dorms by comparing the query to dorm name, room type, community, and proximity.
     * Uses stemming and Levenshtein distance to score matches.
     *
     * @param query user input text
     * @return top 3 best-matching dorms based on the query
     */
	@Override
	public List<DormSearchResult> searchDorms(String query) {
		List<DormSearchResult> results = new ArrayList<>();
		List<Dorm> dorms = getAllDorms();

		for (Dorm dorm : dorms) {
			int score = calculateDormScore(dorm, query.toLowerCase());
			if (score > 0) {
				results.add(new DormSearchResult(dorm, score));
			}
		}

		results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

		return results.subList(0, Math.min(3, results.size()));
	}

	/**
	 * Stem a word to root form to match semantically related words
	 */
	englishStemmer stemmer = new englishStemmer();
	
	/**
	* Stems a given word using the Snowball stemmer.
	*
	* @param word the input word
	* @return the stemmed form of the word
	*/
	String stem(String word) {
		stemmer.setCurrent(word.toLowerCase());
		stemmer.stem();
		return stemmer.getCurrent();
	}

	/**
	* Calculates a score for a dorm based on similarity to a search query.
	*
	* @param dorm the dorm to evaluate
	* @param query the search query
	* @return an integer score representing relevance
	*/
	private int calculateDormScore(Dorm dorm, String query) {
		LevenshteinDistance ld = new LevenshteinDistance(Integer.MAX_VALUE);
		String stemmedQuery = stem(query.toLowerCase());
		int score = 0;

		// Check dorm name (allow typo)
		String stemmedDorm = stem(dorm.getName().toLowerCase());
		if (stemmedDorm.equals(stemmedQuery)) {
			score += 10;
		} else if (stemmedDorm.contains(stemmedQuery)) {
			score += 7;
		} else {
			int distance = ld.apply(stemmedDorm, stemmedQuery);
			if (distance <= 2) {
				score += 5;
			}
		}
		// Check room types
		for (String roomType : dorm.getRoomTypes()) {
			String stemmedRoomType = stem(roomType.toLowerCase());
			if (stemmedRoomType.contains(stemmedQuery)) {
				score += 5;
			} else {
				int distance = ld.apply(stemmedRoomType, stemmedQuery);
				if (distance <= 2) {
					score += 3;
				}
			}
		}
		// Check communities
		for (String community : dorm.getCommunities()) {
			String stemmedCommunity = stem(community.toLowerCase());
			if (stemmedCommunity.contains(stemmedQuery)) {
				score += 6;
			} else {
				int distance = ld.apply(stemmedCommunity, stemmedQuery);
				if (distance <= 2) {
					score += 4;
				}
			}
		}
		// Check proximity
		for (String location : dorm.getProximity()) {
			String stemmedLocation = stem(location.toLowerCase());
			if (stemmedLocation.contains(stemmedQuery)) {
				score += 3;
			} else {
				int distance = ld.apply(stemmedLocation, stemmedQuery);
				if (distance <= 2) {
					score += 2;
				}
			}
		}

		return score;
	}
	
	/**
	* Matches dorms based on user-defined preferences such as room type, community, proximity, and accessibility.
	*
	* @param preferences a JSON object representing user preferences
	* @return top 3 matching dorms
	*/
	@Override
	public List<DormSearchResult> matchDorms(JsonObject preferences) {
		List<DormSearchResult> results = new ArrayList<>();
		List<Dorm> dorms = getAllDorms();

		for (Dorm dorm : dorms) {
			int score = calculateMatchScoreFromPreferences(dorm, preferences);
			if (score > 0) {
				results.add(new DormSearchResult(dorm, score));
			}
		}

		results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
		return results.subList(0, Math.min(3, results.size()));
	}

	/**
	* Calculates a match score between a dorm and a user's structured preferences.
	*
	* @param dorm the dorm to evaluate
	* @param preferences the user's preferences
	* @return a numerical score
	*/
	private int calculateMatchScoreFromPreferences(Dorm dorm, JsonObject preferences) {
		int score = 0;

		if (preferences.has("roomType")) {
			String preferredRoom = preferences.get("roomType").getAsString();
			if (dorm.getRoomTypes().stream().anyMatch(type -> type.equalsIgnoreCase(preferredRoom))) {
				score += 10;
			}
		}

		if (preferences.has("community")) {
			String preferredCommunity = preferences.get("community").getAsString();
			if (dorm.getCommunities().stream().anyMatch(c -> c.equalsIgnoreCase(preferredCommunity))) {
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

		if (preferences.has("accessibility") && dorm.getAccessibility() > 0) {
			boolean needsAccessibility = preferences.get("accessibility").getAsBoolean();
			if (needsAccessibility) {
				score += dorm.getAccessibility();
			}
		}

		return score;
	}

	
	@Override
	public List<DormSearchResult> getInfo(JsonObject info) {
		return List.of();
	}
}
