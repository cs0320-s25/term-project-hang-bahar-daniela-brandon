package org.example.Dorms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommunityHouseAssigner {
  private static final Map<String, List<String>> COMMUNITY_MAP = new HashMap<>();

  static {
    COMMUNITY_MAP.put("Civic Engagement", List.of("chapinhouse"));
    COMMUNITY_MAP.put("Interfaith", List.of("danoffhall"));
    COMMUNITY_MAP.put("greek housing, fraternity - Delta Phi", List.of("dimanhouse"));
    COMMUNITY_MAP.put("greek housing - Delta Gamma / Alpha Delta Phi", List.of("goddardhouse"));
  }

  public static Set<String> getCommunity(String dormName) {
    Set<String> community = new HashSet<>();
    for (Map.Entry<String, List<String>> entry : COMMUNITY_MAP.entrySet()) {
      if (entry.getValue().contains(dormName)) {
        community.add(entry.getKey());
      }
    }
    return community;
  }

}
