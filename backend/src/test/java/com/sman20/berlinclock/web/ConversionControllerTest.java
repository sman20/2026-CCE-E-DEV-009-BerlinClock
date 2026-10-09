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
  void convertsMilestone() throws Exception {
    mvc.perform(get("/api/berlin-clock-time").param("digitalTime", "12:32:00"))
        .andExpect(status().isOk())
        .andExpect(content().json(
                "{\"singleMinutesRow\":\"YYOO\",\"fiveMinutesRow\":\"YYRYYROOOOO\"}",
                JsonCompareMode.STRICT));
  }

  @Test
  void missingParameterHasConsistentProblemType() throws Exception {
    mvc.perform(get("/api/berlin-clock-time"))
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
}
