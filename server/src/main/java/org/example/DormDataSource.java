package org.example;

import java.util.List;

public interface DormDataSource {
  List<Dorm> getAllDorms();
  List<DormSearchResult> searchDorms(String query);
}