package org.example.Dorms;

import java.util.List;
import java.util.Set;

public class Dorm {
  private String name;
  private Set<String> roomTypes;
  private Set<String> bathrooms;
  private Set<String> proximity;
  private Set<String> communities;
  private Integer accessibility;
  private List<String> reviews;
  private String yearBuilt;
  private String description;

  public Dorm(String name, Set<String> roomTypes, Set<String> bathrooms,
      Set<String> proximity, Set<String> communities, Integer accessibility, List<String> reviews, String yearBuilt, String description) {
    this.name = name;
    this.roomTypes = roomTypes;
    this.bathrooms = bathrooms;
    this.proximity = proximity;
    this.communities = communities;
    this.accessibility = accessibility;
    this.reviews = reviews;
    this.yearBuilt = yearBuilt;
    this.description = description;
  }

  // Getters
  public String getName() { return name; }
  public Set<String> getRoomTypes() { return roomTypes; }
  public Set<String> getBathrooms() { return bathrooms; }
  public Set<String> getProximity() { return proximity; }
  public Set<String> getCommunities() { return communities; }
  public boolean isAccessible() { return accessibility > 2; }
  public List<String> getReviews() { return reviews; }
  public Integer getAccessibility() { return accessibility; }
  public String getYearBuilt() { return yearBuilt; }
  public String getDescription() { return description; }


}