package org.example.Handlers;

import org.example.Dorms.DormDataSource;

import com.google.gson.Gson;

import spark.Request;
import spark.Response;
import spark.Route;

public class GetDormsHandler implements Route {

  private DormDataSource dataSource;
  private Gson gson = new Gson();

  public GetDormsHandler(DormDataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Object handle(Request req, Response res) throws Exception {
    System.out.println("GetDormsHandler entered");
    String query = req.queryParams("query");
    if (query == null || query.isEmpty()) {
      res.status(400);
      return "Missing query parameter";
    }
	

    return gson.toJson(dataSource.getAllDorms());
  }

}

