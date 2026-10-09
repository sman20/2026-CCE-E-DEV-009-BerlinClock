package com.sman20.berlinclock.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class FiveHoursRowTest {
  @ParameterizedTest
  @CsvSource({"00:00:00,OOOO", "23:59:59,RRRR", "02:04:00,OOOO", "08:23:00,ROOO", "16:35:00,RRRO"})
  void storyExamples(String text, String expected) {
    assertEquals(expected, FiveHoursRow.from(DigitalTime.parse(text)).lamps());
  }

  @ParameterizedTest
  @CsvSource({"OOOO,0", "ROOO,5", "RROO,10", "RRRO,15", "RRRR,20"})
  void validPrefixesContributeHours(String lamps, int hours) {
    assertEquals(hours, new FiveHoursRow(lamps).hours());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "RRR", "RRRRR", "RORO", "OROO", "YYYY", "rrro", "RRR "})
  void rejectsInvalidRow(String lamps) {
    assertThrows(InvalidTimeException.class, () -> new FiveHoursRow(lamps));
  }
}
