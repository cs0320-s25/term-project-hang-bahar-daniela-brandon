package org.example;

import java.util.List;

public interface DormDataSource {
  List<Dorm> getAllDorms();

  //function that searches through the dorms with a given query
  List<DormSearchResult> searchDorms(String query);
}