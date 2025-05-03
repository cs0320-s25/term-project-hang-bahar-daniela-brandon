package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class AccessibilityFetcher {
	public static Map<String, Integer> fetchAccessibility() {
		Map<String, Integer> accessibilityMap = new HashMap<>();
		try {
			String endpoint = "https://services1.arcgis.com/HMLBxPKXzqtpFXfq/arcgis/rest/services/Active_Buildings/FeatureServer/0/query";
			String params = "?f=json&geometry=-7949450.94165924%2C5134122.315860363%2C-7947004.956754116%2C5136568.300765488&maxRecordCountFactor=4&resultOffset=0&resultRecordCount=8000&where=1%3D1&orderByFields=OBJECTID&outFields=Accessibility_Code%2COBJECTID%2CProperty_Name&quantizationParameters=%7B%22extent%22%3A%7B%22xmin%22%3A-7949450.94165924%2C%22ymin%22%3A5134122.315860363%2C%22xmax%22%3A-7947004.956754116%2C%22ymax%22%3A5136568.300765488%7D%2C%22mode%22%3A%22view%22%2C%22originPosition%22%3A%22upperLeft%22%2C%22tolerance%22%3A4.77731426782227%7D&resultType=tile&spatialRel=esriSpatialRelIntersects&geometryType=esriGeometryEnvelope&defaultSR=102100";
			URL url = new URL(endpoint + params);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			JSONObject json = new JSONObject(response.toString());
			JSONArray features = json.getJSONArray("features");

			for (int i = 0; i < features.length(); i++) {
				JSONObject attributes = features.getJSONObject(i).getJSONObject("attributes");
				String name = attributes.optString("Property_Name", null);

				if (name != null && !attributes.isNull("Accessibility_Code") && nameMapping.containsKey(name)) {
					int code = attributes.getInt("Accessibility_Code");
					accessibilityMap.put(nameMapping.get(name), code);
				}
			}
			System.out.println("printing accessibility map");
			// Output the result
			// accessibilityMap.forEach((name, code) -> {
			// System.out.println(name + " -> " + code);
			// });

		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessibilityMap;
	}

	static Map<String, String> nameMapping = Map.ofEntries(
			Map.entry("Buxton House: Wriston Quad", "buxtonhouse"),
			Map.entry("Hope College", "hopecollege"),
			Map.entry("Barbour Hall", "barbourhall"),
			Map.entry("Diman House: Wriston Quad", "dimanhouse"),
			Map.entry("Young Orchard Ave 002", "youngorchard2"),
			Map.entry("Chapin House: Wriston Quad", "chapinhouse"),
			Map.entry("Sears House: Wriston Quad", "searshouse"),
			Map.entry("Chen Family Hall", "chenfamilyhall"),
			Map.entry("Hegeman Hall", "hegemanhall"),
			Map.entry("Sternlicht Commons", "sternlichtcommons"),
			Map.entry("Graduate Center A", "gradcentera"),
			Map.entry("Harkness House: Wriston Quad", "harknesshouse"),
			Map.entry("Graduate Center B", "gradcenterb"),
			Map.entry("Slater Hall", "slaterhall"),
			Map.entry("Graduate Center C", "gradcenterc"),
			Map.entry("Graduate Center D", "gradcenterd"),
			Map.entry("Wayland House: Wriston Quad", "waylandhouse"),
			Map.entry("Marcy House: Wriston Quad", "marcyhouse"),
			Map.entry("Vartan Gregorian Quad A", "gregorianquada"),
			Map.entry("Vartan Gregorian Quad B", "gregorianquadb"),
			Map.entry("Minden Hall", "mindenhall"),
			Map.entry("Goddard House: Wriston Quad", "goddardhouse"),
			Map.entry("Olney House: Wriston Quad", "olneyhouse"),
			Map.entry("Caswell Hall", "caswellhall"),
			Map.entry("Young Orchard Ave 004", "youngorchard4"),
			Map.entry("Littlefield Hall", "littlefieldhall"),
			Map.entry("Young Orchard Ave 010", "youngorchard10"),
			Map.entry("William and Ami Danoff Hall", "danoffhall"),
			Map.entry("Machado (Antonio) House", "machadohouse"),
			Map.entry("King House", "kinghouse"),
			Map.entry("Perkins Hall", "perkinshall"));

}
