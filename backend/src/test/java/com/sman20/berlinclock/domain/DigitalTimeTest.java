package com.sman20.berlinclock.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class DigitalTimeTest {

  @ParameterizedTest
  @CsvSource({"00:00:00,0,0,0", "23:59:59,23,59,59", "12:32:00,12,32,0"})
  void parsesExactTime(String text, int h, int m, int s) {
    assertEquals(new DigitalTime(h, m, s), DigitalTime.parse(text));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(
      strings = {
        "0:00:00",
        "24:00:00",
        "23:59:60",
        "12:60:00",
        "-1:00:00",
        " 12:32:00",
        "12:32:00 ",
        "12:32:00\n",
        "12:32:00\r\n",
        "12:32:00x",
        "１２:３２:００",
        "12-32-00",
        "12: 2:00",
        "12:32",
        "12:32:00.123"
      })
  void rejectsMalformedInput(String text) {
    assertThrows(InvalidTimeException.class, () -> DigitalTime.parse(text));
  }

  @ParameterizedTest
  @CsvSource({"-1,0,0", "24,0,0", "0,-1,0", "0,60,0", "0,0,-1", "0,0,60"})
  void constructorProtectsRanges(int h, int m, int s) {
    assertThrows(InvalidTimeException.class, () -> new DigitalTime(h, m, s));
  }

  @ParameterizedTest
  @CsvSource({"0,0,0,00:00:00", "3,4,5,03:04:05", "23,59,59,23:59:59"})
  void formatsZeroPaddedAscii(int h, int m, int s, String expected) {
    assertEquals(expected, new DigitalTime(h, m, s).text());
  }
}
