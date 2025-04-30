package org.example;


import static spark.Spark.after;

import java.io.IOException;
import org.example.Handlers.GetDormsHandler;
import org.example.Handlers.MatchHandler;
import org.example.Handlers.SearchHandler;
import spark.Filter;
import spark.Spark;

/** Top Level class for our project, utilizes spark to create and maintain our server. */
public class Server {

  public static void setUpServer() {
    int port = 3232;
    Spark.port(port);

    after(
        (Filter)
            (request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
            });

    DormDataSource dataSource = DormDataSourceFactory.createDataSource(DormDataSourceFactory.DataSourceType.MOCK);

    // fetches the data at http://localhost:3232/search?query=quiet Or /info?query=getAllDorms
    Spark.get("/search", new SearchHandler(dataSource));
    Spark.get("/info", new GetDormsHandler(dataSource));
    Spark.post("/match", new MatchHandler(dataSource));
    System.out.println("called in server");


    Spark.notFound(
        (request, response) -> {
          response.status(404); // Not Found
          System.out.println("ERROR");
          return "404 Not Found - The requested endpoint does not exist.";
        });
    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }

  /**
   * Runs Server.
   *
   * @param args none
   */
  public static void main(String[] args) {
    setUpServer();
  }
}