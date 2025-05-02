package org.example;

import static spark.Spark.after;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.example.Handlers.GetDormsHandler;
import org.example.Handlers.SearchHandler;
import spark.Filter;
import spark.Spark;

/** Top Level class for our project, utilizes spark to create and maintain our server. */
public class Server {

  public static void setUpServer() {
    System.out.println("Starting server...");
    int port = 5678;
    Spark.port(port);
    System.out.println("555");
    after(
        (Filter)
            (request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
            });
    System.out.println("666");

    DormDataSource dataSource = DormDataSourceFactory.createDataSource(DormDataSourceFactory.DataSourceType.MOCK);
    System.out.println("777");
    // fetches the data at http://localhost:3232/search?query=quiet Or /info?query=getAllDorms
    Spark.get("/search", new SearchHandler(dataSource));
    Spark.get("/info", new GetDormsHandler(dataSource));
    Spark.get("/", (req, res) -> "Hello World");
    Spark.get("/test", (req, res) -> "Test successful");
    System.out.println("888");

    Spark.notFound(
        (request, response) -> {
          response.status(404); // Not Found
          System.err.println("ERROR");
          System.out.flush();
          return "404 Not Found - The requested endpoint does not exist.";
        });

    System.out.println("999");
    Spark.init();
    System.out.println("aaa");
    Spark.awaitInitialization();
    System.out.println("bbb");

    System.out.println("Server started at http://localhost:" + port);
  }

  public static void printDormRoomTypes() {
    try {

      Map<String, Set<String>> dorm_room_types = new DormRoomTypesParser().parseDormRoomTypes("/Users/hangnguyen/Desktop/Academics/CS320/term-project-hang-bahar-daniela-brandon/dorm.csv");
      Map<String, Integer> accessibilityMap = AccessibilityFetcher.fetchAccessibility();

//      System.out.println("printing dorm accessibility");
//      for (Map.Entry<String, Integer> entry : accessibilityMap.entrySet()) {
//        System.out.println(entry.getKey() + " => " + entry.getValue());
//      }
//
//      for (Map.Entry<String, Set<String>> entry : dorm_room_types.entrySet()) {
//        System.out.println(entry.getKey() + " => " + entry.getValue());
//      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Runs Server.
   *
   * @param args none
   */
  public static void main(String[] args) throws IOException {
    setUpServer();
    printDormRoomTypes();
  }
}