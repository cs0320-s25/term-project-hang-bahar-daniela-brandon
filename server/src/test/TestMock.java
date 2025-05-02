//package org.example;
//
//import java.util.List;
//
////TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
//// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
//// Main.java
//public class TestMock {
//  public static void main(String[] args) {
//    // Create mock data source
//    DormDataSource dataSource = DormDataSourceFactory.createDataSource(
//        DormDataSourceFactory.DataSourceType.MOCK
//    );
//
//    // Example search
//    String searchQuery = "quiet";
//    List<DormSearchResult> results = dataSource.searchDorms(searchQuery);
//
//    // Display results
//    System.out.println("Search results for: " + searchQuery);
//    for (DormSearchResult result : results) {
//      System.out.printf("%s (Score: %d)%n",
//          result.getDorm().getName(),
//          result.getScore());
//    }
//  }
//}