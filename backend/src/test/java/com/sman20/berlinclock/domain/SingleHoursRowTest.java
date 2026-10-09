package com.sman20.berlinclock.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class SingleHoursRowTest {

  @ParameterizedTest
  @CsvSource({"00:00:00,OOOO", "23:59:59,RRRO", "02:04:00,RROO", "08:23:00,RRRO", "14:35:00,RRRR"})
  void storyExamples(String text, String expected) {
    assertEquals(expected, SingleHoursRow.from(DigitalTime.parse(text)).lamps());
  }

  @ParameterizedTest
  @CsvSource({"OOOO,0", "ROOO,1", "RROO,2", "RRRO,3", "RRRR,4"})
  void validPrefixesContributeHours(String lamps, int hours) {
    assertEquals(hours, new SingleHoursRow(lamps).hours());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "RRR", "RRRRR", "RORO", "OROO", "YYYY", "rrro", "RRR ", "RRR0"})
  void rejectsInvalidRow(String lamps) {
    assertThrows(InvalidTimeException.class, () -> new SingleHoursRow(lamps));
  }
}
