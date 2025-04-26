package org.example.Handlers;


import java.util.List;
import org.example.DormDataSource;
import org.example.DormSearchResult;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class SearchHandler implements Route {
  private DormDataSource dataSource;
  private Gson gson = new Gson();

  public SearchHandler(DormDataSource dataSource) {
    this.dataSource = dataSource;
  }

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
