package org.example.Dorms;

import com.google.gson.JsonObject;
import java.util.List;

public interface DormDataSource {
  List<Dorm> getAllDorms();

  //function that searches through the dorms with a given query
  List<DormSearchResult> searchDorms(String query);
  List<DormSearchResult> matchDorms(JsonObject preferences);
  List<DormSearchResult> getInfo(JsonObject info);
}