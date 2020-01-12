import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static spark.Spark.awaitInitialization;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import common.GlobalSettings;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;
import spark.utils.IOUtils;

public class TransferControllerTest {

  static GlobalSettings globalSettings;

  @BeforeClass
  public static void beforeClass() {
    globalSettings = mock(GlobalSettings.class);
    when(globalSettings.isTestDataBaseInUse()).thenReturn(true);
    TransferController.main(new String[0]);
    awaitInitialization();
  }

  @AfterClass
  public static void afterClass() {
    Spark.stop();
  }

  @Test
  public void mainPath_shouldReturnOk() {
    TestResponse res = request("GET", "/");
    assertEquals(200, res.status);
  }

  @Test
  public void getAccounts_shouldReturnAllAccounts() {
    TestResponse res = request("GET", "/accounts");
    List result = res.asList();
    assertEquals(200, res.status);
    assertEquals(4, result.size());
    assertEquals("{id=3.0, iban=accountIban3, balance=300.0}", result.get(2).toString());
    assertEquals("{id=4.0, iban=accountIban4, balance=400.0}", result.get(3).toString());
  }

  @Test
  public void getAccountById_shouldReturnAllAccounts() {
    TestResponse res = request("GET", "/accounts/4");
    LinkedTreeMap account = res.asMap().get("value");
    assertEquals(200, res.status);
    assertEquals(4.0, account.get("id"));
    assertEquals("accountIban4", account.get("iban"));
    assertEquals(400.0, account.get("balance"));
  }

  @Test
  public void getAccountById_withInvalidNumber_shouldReturnAllAccounts() {
    assertThrows(AssertionError.class, () -> request("GET", "/accounts/9"));
  }

  @Test
  public void getAccountHistory_shouldReturnAllRecords() {
    TestResponse res = request("GET", "/accounts/1/history");
    List result = res.asList();
    assertEquals(200, res.status);
    assertEquals("{id=1.0, accountId=1.0, date=Jan 1, 2020 12:00:01 AM, amount=20.0, origin=test}",
        result.get(0).toString());
    assertEquals("{id=2.0, accountId=1.0, date=Jan 2, 2020 12:00:01 AM, amount=-10.0, origin=test}",
        result.get(1).toString());
  }

  @Test
  public void sendTransfer_shouldMoveMoney() {
    String jsonInputString = "{\"senderAccount\":accountIban1,\"receiverAccount\":\"accountIban2\",\"amount\":1.00,\"requstOriginApplication\":\"test\"}";

    TestResponse res = request("POST", "/transfer", jsonInputString);

    assertEquals(200, res.status);
    assertEquals(99.0, request("GET", "/accounts/1").asMap().get("value").get("balance"));
  }

  private TestResponse request(String method, String path) {
    return request(method, path, null);
  }

  private TestResponse request(String method, String path, String jsonInputString) {
    try {
      URL url = new URL("http://localhost:4567" + path);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod(method);
      connection.setRequestProperty("Content-Type", "application/json; utf-8");
      connection.setDoOutput(true);
      connection.connect();

      if (jsonInputString != null) {
        try (OutputStream os = connection.getOutputStream()) {
          byte[] input = jsonInputString.getBytes("utf-8");
          os.write(input, 0, input.length);
        }
      }
      String body = IOUtils.toString(connection.getInputStream());
      return new TestResponse(connection.getResponseCode(), body);
    } catch (IOException e) {
      e.printStackTrace();
      fail("Sending request failed: " + e.getMessage());
      return null;
    }
  }

  private static class TestResponse {

    final String body;
    final int status;

    TestResponse(int status, String body) {
      this.status = status;
      this.body = body;
    }

    List asList() {
      return new Gson().fromJson(body, List.class);
    }

    Map<String, LinkedTreeMap> asMap() {
      return new Gson().fromJson(body, Map.class);
    }
  }
}
