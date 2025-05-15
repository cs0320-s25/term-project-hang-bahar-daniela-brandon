package org.example.Handlers;


import java.util.List;

import org.example.Dorms.DormDataSource;
import org.example.Dorms.DormSearchResult;

import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

/**
 * Handles HTTP GET requests to search for dormitories based on a query parameter.
 * This class implements the Spark Route interface, allowing it to be
 * used as a route handler within a Spark Java web application. When a GET
 * request is made to the associated endpoint with a 'query' parameter, this
 * handler processes the input and returns a list of matching dormitories in
 * JSON format.
 * If the 'query' parameter is missing or empty, the handler responds with a
 * 400 Bad Request status and an error message. If no matching dormitories are
 * found, it responds with a 404 Not Found status and an appropriate message.
 */


public class SearchHandler implements Route {
  private DormDataSource dataSource;
  private Gson gson = new Gson();
	
	/**
	* Constructs a new SearchHandler with the specified data source.
	*
	* @param dataSource the data source used to retrieve and search dormitory data
	*/
  public SearchHandler(DormDataSource dataSource) {
    this.dataSource = dataSource;
  }

    /**
     * Handles the HTTP GET request by retrieving the 'query' parameter, searching
     * for matching dormitories based on this query, and returning the results as
     * a JSON-formatted string.
     *
     * @param req the HTTP request containing the 'query' parameter
     * @param res the HTTP response
     * @return a JSON-formatted string containing matching dormitories or an error message
     * @throws Exception if an error occurs while processing the request
     */

  @Override
  public Object handle(Request req, Response res) throws Exception {
    String query = req.queryParams("query");
    if (query == null || query.isEmpty()) {
      res.status(400);
      return "Missing query parameter";
    }
	List<DormSearchResult> dorms = dataSource.searchDorms(query);

	if (dorms.isEmpty()) {
	  res.status(404);
	  return "No dorms found for the given query:" + query;
	}
	else {
		return gson.toJson(dataSource.searchDorms(query));
	}
  }
}
