package org.example.Handlers;

import com.google.gson.JsonObject;
import java.util.List;
import org.example.Dorms.DormDataSource;

import com.google.gson.Gson;

import org.example.Dorms.DormSearchResult;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handles HTTP GET requests to retrieve all dormitory data.
 * This class implements the Spark Route interface, allowing it to be
 * used as a route handler within a Spark Java web application. When a GET
 * request is made to the associated endpoint, this handler retrieves all dorm
 * information from the provided DormDataSource and returns it as a
 * JSON-formatted string.
 */

public class GetDormsHandler implements Route {

  private DormDataSource dataSource;
  private Gson gson = new Gson();

  /**
  * Constructs a new GetDormsHandler with the specified data source.
  *
  * @param dataSource the data source from which dorm information is retrieved
  */

  public GetDormsHandler(DormDataSource dataSource) {
    this.dataSource = dataSource;
  }

/**
     * Handles the HTTP request by retrieving all dormitory data and returning it
     * as a JSON-formatted string.
     *
     * @param req the HTTP request
     * @param res the HTTP response
     * @return a JSON-formatted string containing all dormitory data
     * @throws Exception if an error occurs while retrieving the data
     */

  @Override
  public Object handle(Request req, Response res) throws Exception {

    return gson.toJson(dataSource.getAllDorms());
  }

}

