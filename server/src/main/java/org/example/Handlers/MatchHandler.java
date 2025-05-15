package org.example.Handlers;


import com.google.gson.JsonObject;
import java.util.List;

import org.example.Dorms.DormDataSource;
import org.example.Dorms.DormSearchResult;

import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

/**
 * Handles HTTP POST requests to match dormitories based on user-defined preferences.
 * This class implements the Spark Route interface, allowing it to be
 * used as a route handler within a Spark Java web application. When a POST
 * request is made to the associated endpoint with a JSON body containing user
 * preferences, this handler processes the input and returns a list of matching
 * dormitories in JSON format.
  */
public class MatchHandler implements Route {
  private DormDataSource dataSource;
  private Gson gson = new Gson();

  /**
   * Constructs a new MatchHandler with the specified data source.
   *
   * @param dataSource the data source used to retrieve and match dormitory data
   */

  public MatchHandler(DormDataSource dataSource) {
    this.dataSource = dataSource;

  }

    /**
     * Handles the HTTP POST request by parsing user preferences from the request body,
     * matching dormitories based on these preferences, and returning the results as
     * a JSON-formatted string.
     *
     * @param req the HTTP request containing user preferences in JSON format
     * @param res the HTTP response
     * @return a JSON-formatted string containing matching dormitories or an error message
     * @throws Exception if an error occurs while processing the request
     */

  @Override
  public Object handle(Request req, Response res) throws Exception {
    JsonObject preferences = gson.fromJson(req.body(), JsonObject.class);

    if (preferences == null) {
      res.status(400);
      return "Invalid JSON body";
    }
    List<DormSearchResult> matches = dataSource.matchDorms(preferences);
    System.out.println("called matchDorms");
    if (matches.isEmpty()) {
      res.status(404);
      return "No matching dorms found";
    }

    res.type("application/json");
    return gson.toJson(matches);

  }
}
