package com.sman20.berlinclock.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class FiveMinutesRowTest {
  @ParameterizedTest
  @CsvSource({
    "00:00:00,OOOOOOOOOOO",
    "23:59:59,YYRYYRYYRYY",
    "12:04:00,OOOOOOOOOOO",
    "12:23:00,YYRYOOOOOOO",
    "12:35:00,YYRYYRYOOOO"
  })
  void storyExamples(String text, String expected) {
    assertEquals(expected, FiveMinutesRow.from(DigitalTime.parse(text)).lamps());
  }

  @ParameterizedTest
  @CsvSource({
    "OOOOOOOOOOO,0",
    "YOOOOOOOOOO,5",
    "YYOOOOOOOOO,10",
    "YYROOOOOOOO,15",
    "YYRYOOOOOOO,20",
    "YYRYYOOOOOO,25",
    "YYRYYROOOOO,30",
    "YYRYYRYOOOO,35",
    "YYRYYRYYOOO,40",
    "YYRYYRYYROO,45",
    "YYRYYRYYRYO,50",
    "YYRYYRYYRYY,55"
  })
  void validPrefixesContributeMinutes(String lamps, int minutes) {
    assertEquals(minutes, new FiveMinutesRow(lamps).minutes());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(
      strings = {
        "",
        "OOOOOOOOOO",
        "OOOOOOOOOOOO",
        "YYYOOOOOOOO",
        "YYRYYYOOOOO",
        "YYRYYRYYYOO",
        "YOOYOOOOOOO",
        "yyroooooooo",
        "YYROOOOOOO "
      })
  void rejectsInvalidRow(String lamps) {
    assertThrows(InvalidTimeException.class, () -> new FiveMinutesRow(lamps));
  }
}
