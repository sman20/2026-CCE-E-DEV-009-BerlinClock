package com.sman20.berlinclock.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class SingleMinutesRowTest {

  @ParameterizedTest
  @CsvSource({"00:00:00,OOOO", "23:59:59,YYYY", "12:32:00,YYOO", "12:34:00,YYYY", "12:35:00,OOOO"})
  void storyExamples(String text, String expected) {
    assertEquals(expected, SingleMinutesRow.from(DigitalTime.parse(text)).lamps());
  }

  @ParameterizedTest
  @CsvSource({"OOOO,0", "YOOO,1", "YYOO,2", "YYYO,3", "YYYY,4"})
  void validPrefixesContributeMinutes(String lamps, int minutes) {
    assertEquals(minutes, new SingleMinutesRow(lamps).minutes());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "YYY", "YYYYY", "RORO", "YOYO", "yooo", "YOO ", "YYY0"})
  void rejectsInvalidRow(String lamps) {
    assertThrows(InvalidTimeException.class, () -> new SingleMinutesRow(lamps));
  }
}
