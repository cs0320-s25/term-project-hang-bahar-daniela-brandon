package org.example.Handlers;


import com.google.gson.JsonObject;
import java.util.List;
import org.example.DormDataSource;
import org.example.DormSearchResult;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class MatchHandler implements Route {
  private DormDataSource dataSource;
  private Gson gson = new Gson();



  public MatchHandler(DormDataSource dataSource) {
    this.dataSource = dataSource;
    System.out.println("entered matchhandler constructor");

  }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    JsonObject preferences = gson.fromJson(req.body(), JsonObject.class);

    if (preferences == null) {
      res.status(400);
      return "Invalid JSON body";
    }
    System.out.println("about to call matchDorms");
    // Pass the preferences to some matching logic in your data source
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
