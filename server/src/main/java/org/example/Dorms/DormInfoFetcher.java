package org.example.Dorms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class DormInfoFetcher {
	public static Map<String, Map<String, String>> fetchDormInfo() {
		Map<String, Map<String, String>> dormInfoMap = new HashMap<>();
		try {
			URL url = new URL(
					"https://services1.arcgis.com/HMLBxPKXzqtpFXfq/arcgis/rest/services/Active_Buildings/FeatureServer/0/query?f=json&geometry=-7949450.94165924%2C5134122.315860363%2C-7947004.956754116%2C5136568.300765488&maxRecordCountFactor=4&resultOffset=0&resultRecordCount=8000&where=1%3D1&orderByFields=OBJECTID&outFields=Accessibility_Code%2COBJECTID%2CProperty_Name%2CLong_Description%2CYear_of_Construction&quantizationParameters=%7B%22extent%22%3A%7B%22xmin%22%3A-7949450.94165924%2C%22ymin%22%3A5134122.315860363%2C%22xmax%22%3A-7947004.956754116%2C%22ymax%22%3A5136568.300765488%7D%2C%22mode%22%3A%22view%22%2C%22originPosition%22%3A%22upperLeft%22%2C%22tolerance%22%3A4.77731426782227%7D&resultType=tile&spatialRel=esriSpatialRelIntersects&geometryType=esriGeometryEnvelope&defaultSR=102100");
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

				if (name != null && !attributes.isNull("Long_Description") && !attributes.isNull("Year_of_Construction")
						&& AccessibilityFetcher.getNameMapping().containsKey(name)) {
					Map<String, String> info = new HashMap<>();
					String desc = attributes.getString("Long_Description");
					int year = attributes.getInt("Year_of_Construction");
					info.put("description", desc);
					info.put("built", String.valueOf(year));
					dormInfoMap.put(AccessibilityFetcher.getNameMapping().get(name), info);
				}
			}
			System.out.println("printing dorm info map");
			// Output the result
			// accessibilityMap.forEach((name, code) -> {
			// System.out.println(name + " -> " + code);
			// });

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dormInfoMap;
	}
}