package org.example.Dorms;

import java.util.List;
import java.util.Set;

public class Dorm {
  private String name;
  private Set<String> roomTypes;
  private Set<String> bathrooms;
  private Set<String> proximity;
  private Set<String> communities;
  private boolean accessibility;
  private List<String> reviews;

  public Dorm(String name, Set<String> roomTypes, Set<String> bathrooms,
      Set<String> proximity, Set<String> communities, boolean accessibility, List<String> reviews) {
    this.name = name;
    this.roomTypes = roomTypes;
    this.bathrooms = bathrooms;
    this.proximity = proximity;
    this.communities = communities;
    this.accessibility = accessibility;
    this.reviews = reviews;
  }

  // Getters
  public String getName() { return name; }
  public Set<String> getRoomTypes() { return roomTypes; }
  public Set<String> getBathrooms() { return bathrooms; }
  public Set<String> getProximity() { return proximity; }
  public Set<String> getCommunities() { return communities; }
  public boolean isAccessible() { return accessibility; }
  public List<String> getReviews() { return reviews; }
}