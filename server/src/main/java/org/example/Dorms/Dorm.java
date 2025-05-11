package org.example.Dorms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class Dorm {
	private String name;
	private Set<String> roomTypes;
	private Set<String> bathrooms;
	private Set<String> proximity;
	private Set<String> communities;
	private Integer accessibility;
	private String yearBuilt;
	private String description;
	private List<Map<String, Object>> posts;

	public Dorm(String name, Set<String> roomTypes, Set<String> bathrooms,
           Set<String> proximity, Set<String> communities, Integer accessibility,
           String yearBuilt, String description,
           List<Map<String, Object>> posts) {
		this.name = name;
		this.roomTypes = roomTypes;
		this.bathrooms = bathrooms;
		this.proximity = proximity;
		this.communities = communities;
		this.accessibility = accessibility;
		this.yearBuilt = yearBuilt;
		this.description = description;
		this.posts = posts;
	}

	// Getters
	public String getName() {
		return name;
	}

	public Set<String> getRoomTypes() {
		return roomTypes;
	}

	public Set<String> getBathrooms() {
		return bathrooms;
	}

	public Set<String> getProximity() {
		return proximity;
	}

	public Set<String> getCommunities() {
		return communities;
	}

	public boolean isAccessible() {
		return accessibility > 2;
	}

	public Integer getAccessibility() {
		return accessibility;
	}

	public String getYearBuilt() {
		return yearBuilt;
	}

	public String getDescription() {
		return description;
	}

	public List<Map<String, Object>> getPosts() {
		return posts;
	}

	public List<String> getReviews() {
		List<String> reviews =new ArrayList<>();
		for (Map<String, Object> post : posts) {
			reviews.add((String) post.get("content"));
		}
		return reviews;

	}
}