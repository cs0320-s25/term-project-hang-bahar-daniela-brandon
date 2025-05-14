package org.example.Dorms;

import com.google.gson.JsonObject;
import java.util.List;

/**
 * This is an interface for dorm data source and is implemented by MockDormDatasource and FirebseDormDatasource
 * It has the shared funcionalities of those classes such as search or getinfo through the database with the provided query
 */
public interface DormDataSource {
  List<Dorm> getAllDorms();

  //function that searches through the dorms with a given query
  List<DormSearchResult> searchDorms(String query);
  List<DormSearchResult> matchDorms(JsonObject preferences);
  List<DormSearchResult> getInfo(JsonObject info);
}
