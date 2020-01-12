package common;

import com.google.gson.Gson;
import spark.ResponseTransformer;

/**
 * Simple json response transformer.
 */
public class JsonTransformer implements ResponseTransformer {
  private Gson gson = new Gson();

  @Override
  public String render(Object model) {
    return gson.toJson(model);
  }
}