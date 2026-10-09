package com.sman20.berlinclock.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sman20.berlinclock.ConverterConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConversionController.class)
@Import(ConverterConfiguration.class)
class ConversionControllerTest {
  @Autowired MockMvc mvc;

  @Test
  void convertsEntireClock() throws Exception {
    mvc.perform(get("/api/berlin-clock-time").param("digitalTime", "12:32:00"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"berlinClockTime\":\"YRROORROOYYRYYROOOOOYYOO\"}", JsonCompareMode.STRICT));
  }

  @ParameterizedTest
  @ValueSource(strings = {"/api/berlin-clock-time", "/api/digital-time"})
  void missingParameterHasConsistentProblemType(String path) throws Exception {
    mvc.perform(get(path))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
        .andExpect(jsonPath("$.type").value("urn:berlin-clock:invalid-input"))
        .andExpect(jsonPath("$.title").value("Bad Request"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail").isNotEmpty());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "",
        "24:00:00",
        "23:59:60",
        "12:32",
        "12:32:00.123",
        " 12:32:00",
        "12:32:00\n",
        "12:32:00\r\n",
        "１２:３２:００",
        "12: 2:00"
      })
  void rejectsMalformedTimeWithProblemDetail(String input) throws Exception {
    mvc.perform(get("/api/berlin-clock-time").param("digitalTime", input))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
        .andExpect(jsonPath("$.type").value("urn:berlin-clock:invalid-input"))
        .andExpect(jsonPath("$.title").value("Bad Request"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail").isNotEmpty());
  }

  @Test
  void reversesReadingWithExactFinalResponse() throws Exception {
    mvc.perform(get("/api/digital-time").param("berlinClockTime", "ORRRRRRROYYRYYRYYRYYYYYY"))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"digitalTime\":\"23:59:01\"}", JsonCompareMode.STRICT));
  }

  @ParameterizedTest
  @ValueSource(
          strings = {"",
              "yooooooooooooooooooooooo",
              "YRRRRRRRROOOOOOOOOOOOOOO",
              "YROROOOOOOOOOOOOOOOOOOOO",
              "YOOOOOOOOYYYOOOOOOOOOOOO",
              "YOOOOOOOOOOOOOOOOOOOOOOO\n",
              " YOOOOOOOOOOOOOOOOOOOOOOO"
          })
  void malformedReverseInputUsesProblemDetail(String input) throws Exception {
    mvc.perform(get("/api/digital-time").param("berlinClockTime", input))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
            .andExpect(jsonPath("$.type").value("urn:berlin-clock:invalid-input"))
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").isNotEmpty());
  }
}
