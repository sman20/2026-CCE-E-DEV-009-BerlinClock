package com.sman20.berlinclock.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BerlinClockConverterTest {

  @ParameterizedTest
  @CsvSource({
    "00:00:00,YOOOOOOOOOOOOOOOOOOOOOOO",
    "23:59:59,ORRRRRRROYYRYYRYYRYYYYYY",
    "16:50:06,YRRROROOOYYRYYRYYRYOOOOO",
    "11:37:01,ORROOROOOYYRYYRYOOOOYYOO"
  })
  void entireClockExamples(String text, String expected) {
    assertEquals(expected,
            new BerlinClockConverter().toBerlinClockTime(DigitalTime.parse(text)).lamps());
  }
}
