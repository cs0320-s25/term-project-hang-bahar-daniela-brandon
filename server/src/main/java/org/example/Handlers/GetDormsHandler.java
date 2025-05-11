package org.example.Handlers;

import com.google.gson.JsonObject;
import java.util.List;
import org.example.Dorms.DormDataSource;

import com.google.gson.Gson;

import org.example.Dorms.DormSearchResult;
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

    return gson.toJson(dataSource.getAllDorms());
  }

}

