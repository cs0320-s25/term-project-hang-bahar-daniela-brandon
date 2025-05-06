package org.example;

import java.util.List;
import java.util.Set;

public class DormData {

  private String dormName;
  private Set<String> roomTypes;
  private Set<String> bathrooms;  // Currently unused
  private Set<String> proximity;
  private Set<String> communities;
  private Integer accessibilityCode;
  private List<String> reviews;  // Currently unused

  // Default constructor for Firebase serialization
  public DormData() {}

  // Constructor
  public DormData(String dormName, Set<String> roomTypes, Set<String> bathrooms,
      Set<String> proximity, Set<String> communities, Integer accessibilityCode,
      List<String> reviews) {
    this.dormName = dormName;
    this.roomTypes = roomTypes;
    this.bathrooms = bathrooms;
    this.proximity = proximity;
    this.communities = communities;
    this.accessibilityCode = accessibilityCode;
    this.reviews = reviews;
  }

  // Getters and Setters
  public String getDormName() {
    return dormName;
  }

  public void setDormName(String dormName) {
    this.dormName = dormName;
  }

  public Set<String> getRoomTypes() {
    return roomTypes;
  }

  public void setRoomTypes(Set<String> roomTypes) {
    this.roomTypes = roomTypes;
  }

  public Set<String> getBathrooms() {
    return bathrooms;
  }

  public void setBathrooms(Set<String> bathrooms) {
    this.bathrooms = bathrooms;
  }

  public Set<String> getProximity() {
    return proximity;
  }

  public void setProximity(Set<String> proximity) {
    this.proximity = proximity;
  }

  public Set<String> getCommunities() {
    return communities;
  }

  public void setCommunities(Set<String> communities) {
    this.communities = communities;
  }

  public Integer getAccessibilityCode() {
    return accessibilityCode;
  }

  public void setAccessibilityCode(Integer accessibilityCode) {
    this.accessibilityCode = accessibilityCode;
  }

  public List<String> getReviews() {
    return reviews;
  }

  public void setReviews(List<String> reviews) {
    this.reviews = reviews;
  }
}
