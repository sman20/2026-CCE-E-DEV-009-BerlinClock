package com.sman20.berlinclock.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class SecondsLampTest {

  @ParameterizedTest
  @CsvSource({"00:00:00,Y", "23:59:59,O"})
  void storyExamples(String text, String expected) {
    assertEquals(expected, SecondsLamp.from(DigitalTime.parse(text)).lamps());
  }

  @ParameterizedTest
  @CsvSource({"Y,true", "O,false"})
  void validStatesReportWhetherOn(String lamps, boolean on) {
    assertEquals(on, new SecondsLamp(lamps).isOn());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "YY", "R", "y", " Y", "Y ", "0"})
  void rejectsInvalidRow(String lamps) {
    assertThrows(InvalidTimeException.class, () -> new SecondsLamp(lamps));
  }
}
