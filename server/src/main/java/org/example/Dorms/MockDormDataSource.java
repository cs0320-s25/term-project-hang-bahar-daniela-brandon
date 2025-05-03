package org.example.Dorms;

// MockDormDataSource.java
import java.util.*;

public class MockDormDataSource implements DormDataSource {
  private List<Dorm> dorms;

  public MockDormDataSource() {
    initializeMockData();
  }

  private void initializeMockData() {
    dorms = new ArrayList<>();

    // Goddard dorm
    dorms.add(new Dorm(
        "Goddard",
        new HashSet<>(Arrays.asList("single", "double")),
        new HashSet<>(Arrays.asList("shared", "private")),
        new HashSet<>(Arrays.asList("Sharpe Refectory", "Main Green")),
        new HashSet<>(Arrays.asList("sorority housing")),
        false,
        Arrays.asList(
            "this dorm is really good and clean and nice",
            "can be loud with parties",
            "highly recommend"
        )
    ));

    // Hegeman dorm
    dorms.add(new Dorm(
        "Hegeman",
        new HashSet<>(Arrays.asList("double", "3-persons suite")),
        new HashSet<>(Collections.singletonList("shared")),
        new HashSet<>(Arrays.asList("Sharpe Refectory", "Sciences Library")),
        new HashSet<>(Arrays.asList("quiet housing")),
        false,
        Arrays.asList(
            "quiet housing",
            "loud thayer"
        )
    ));

    // Young Orchard dorm
    dorms.add(new Dorm(
        "Barbour",
        new HashSet<>(Arrays.asList("4-persons suite")),
        new HashSet<>(Collections.singletonList("private")),
        new HashSet<>(Arrays.asList("TF Green Hall", "Orwig Music Library")),
        new HashSet<>(Arrays.asList("religious housing")),
        true,
        Arrays.asList(
            "so far away from everything, but suites come with kitchens and private bathroom"
        )
    ));
  }

  @Override
  public List<Dorm> getAllDorms() {
    return new ArrayList<>(dorms);
  }

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
