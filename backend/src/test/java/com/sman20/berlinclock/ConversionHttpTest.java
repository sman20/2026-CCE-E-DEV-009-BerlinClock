package com.sman20.berlinclock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConversionHttpTest {
  @LocalServerPort int port;

  private HttpResponse<String> get(String path) throws Exception {
    return HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
            HttpResponse.BodyHandlers.ofString());
  }

  @Test
  void actualServerConvertsMilestone() throws Exception {
    var response = get("/api/berlin-clock-time?digitalTime=12%3A32%3A00");
    assertEquals(200, response.statusCode());
    assertEquals(
            "{\"singleMinutesRow\":\"YYOO\",\"fiveMinutesRow\":\"YYRYYROOOOO\"}",
            response.body());
  }

  @Test
  void actualServerRejectsMalformedInput() throws Exception {
    var response = get("/api/berlin-clock-time?digitalTime=12%3A32%3A00%0A");
    assertEquals(400, response.statusCode());
    assertTrue(
        response
            .headers()
            .firstValue("Content-Type")
            .orElse("")
            .startsWith("application/problem+json"));
    assertTrue(response.body().contains("urn:berlin-clock:invalid-input"));
  }
}
