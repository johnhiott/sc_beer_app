package com.johnhiott.sundayfunday;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class ApiRequest {

  private static final String URL = "http://johnhiott.com/sundayfunday/get_locations.php";

  OkHttpClient client = new OkHttpClient();

  String run() throws IOException {

    Request request = new Request.Builder()
          .url(URL)
          .build();

    Response response = client.newCall(request).execute();
    return response.body().string();
  }
}