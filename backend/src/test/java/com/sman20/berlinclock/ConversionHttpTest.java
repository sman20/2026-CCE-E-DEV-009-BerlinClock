package com.sman20.berlinclock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;

import com.sman20.berlinclock.domain.BerlinClockConverter;
import com.sman20.berlinclock.domain.DigitalTime;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConversionHttpTest {
  @LocalServerPort int port;
  @MockitoSpyBean BerlinClockConverter converter;

  private HttpResponse<String> get(String path) throws Exception {
    return HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
            HttpResponse.BodyHandlers.ofString());
  }

  @Test
  void actualServerConvertsEntireClock() throws Exception {
    var response = get("/api/berlin-clock-time?digitalTime=12%3A32%3A00");
    assertEquals(200, response.statusCode());
    assertEquals("{\"berlinClockTime\":\"YRROORROOYYRYYROOOOOYYOO\"}", response.body());
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

  @Test
  void programmingFailuresRemainServerErrors() throws Exception {
    doThrow(new IllegalStateException("Injected programming failure"))
            .when(converter)
            .toBerlinClockTime(new DigitalTime(0, 0, 1));
    var response = get("/api/berlin-clock-time?digitalTime=00%3A00%3A01");
    assertEquals(500, response.statusCode());
  }
}
