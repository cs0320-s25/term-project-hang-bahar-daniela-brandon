package org.example.Dorms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is a proximity assigner. It maps list of dorms to a certain location on campus such as Main Green
 * 
 */

public class ProximityAssigner {

  private static final Map<String, List<String>> PROXIMITY_MAP = new HashMap<>();

  static {
    PROXIMITY_MAP.put("Main Green", List.of("caswellhall", "hopecollege", "hegemanhall", "slaterhall", "littlefieldhall" ));
    PROXIMITY_MAP.put("Wriston Quad", List.of("buxtonhouse", "dimanhouse", "chapinhouse", "searshouse", "harknesshouse", "waylandhouse", "marcyhouse", "goddardhouse", "olneyhouse"));
    PROXIMITY_MAP.put("North Campus", List.of("sternlichtcommons", "mindenhall", "machadohouse"));
    PROXIMITY_MAP.put("South Campus", List.of("barbourhall", "youngorchard2", "chenfamilyhall", "gradcentera", "gradcenterb", "gradcenterc", "gradcenterd", "gregorianquada", "gregorianquadb", "youngorchard4", "youngorchard5", "youngorchard10", "danoffhall", "kinghouse", "perkinshall"));
  }

  
  /**
   * this helper method returns proximity of the specified dorm from the list of "benchmark points" specified above
   * @param: name of the dorm of interest
   * @return: proximity from the specified dorm
   */

  public static Set<String> getProximity(String dormName) {
    Set<String> proximities = new HashSet<>();
    for (Map.Entry<String, List<String>> entry : PROXIMITY_MAP.entrySet()) {
      if (entry.getValue().contains(dormName)) {
        proximities.add(entry.getKey());
      }
    }
    return proximities;
  }

}
