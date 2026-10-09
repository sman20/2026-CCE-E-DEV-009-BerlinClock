package com.sman20.berlinclock.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class BerlinClockTimeTest {
  private static final SecondsLamp SECONDS = new SecondsLamp("O");
  private static final FiveHoursRow FIVE_HOURS = new FiveHoursRow("RRRR");
  private static final SingleHoursRow SINGLE_HOURS = new SingleHoursRow("RRRO");
  private static final FiveMinutesRow FIVE_MINUTES = new FiveMinutesRow("YYRYYRYYRYY");
  private static final SingleMinutesRow SINGLE_MINUTES = new SingleMinutesRow("YYYY");

  @Test
  void acceptsTwentyThreeHoursAndDerivesComponents() {
    var reading =
        new BerlinClockTime(SECONDS, FIVE_HOURS, SINGLE_HOURS, FIVE_MINUTES, SINGLE_MINUTES);
    assertEquals(23, reading.hours());
    assertEquals(59, reading.minutes());
    assertEquals("ORRRRRRROYYRYYRYYRYYYYYY", reading.lamps());
  }

  @Test
  void rejectsTwentyFourHours() {
    assertThrows(
        InvalidTimeException.class,
        () -> new BerlinClockTime(SECONDS, FIVE_HOURS, new SingleHoursRow("RRRR"), FIVE_MINUTES, SINGLE_MINUTES));
  }

  static Stream<Arguments> absentComponents() {
    return Stream.of(
        Arguments.of(null, FIVE_HOURS, SINGLE_HOURS, FIVE_MINUTES, SINGLE_MINUTES),
        Arguments.of(SECONDS, null, SINGLE_HOURS, FIVE_MINUTES, SINGLE_MINUTES),
        Arguments.of(SECONDS, FIVE_HOURS, null, FIVE_MINUTES, SINGLE_MINUTES),
        Arguments.of(SECONDS, FIVE_HOURS, SINGLE_HOURS, null, SINGLE_MINUTES),
        Arguments.of(SECONDS, FIVE_HOURS, SINGLE_HOURS, FIVE_MINUTES, null));
  }

  @ParameterizedTest
  @MethodSource("absentComponents")
  void missingComponentsAreProgrammingErrors(
      SecondsLamp seconds,
      FiveHoursRow fiveHours,
      SingleHoursRow singleHours,
      FiveMinutesRow fiveMinutes,
      SingleMinutesRow singleMinutes) {
    assertThrows(
        NullPointerException.class,
        () -> new BerlinClockTime(seconds, fiveHours, singleHours, fiveMinutes, singleMinutes));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(
          strings = {
                  "Y",
                  "YOOOOOOOOOOOOOOOOOOOOOO",
                  "YOOOOOOOOOOOOOOOOOOOOOOOO",
                  "yooooooooooooooooooooooo",
                  " YOOOOOOOOOOOOOOOOOOOOOOO",
                  "YOOOOOOOOOOOOOOOOOOOOOOO ",
                  "YOOOOOOOOOOOOOOOOOOOOOOO\n",
                  "YOOOOOOOOOOOOOOOOOOOOOOO\r\n",
                  "0OOOOOOOOOOOOOOOOOOOOOOO",
                  "ROOOOOOOOOOOOOOOOOOOOOOO"
          })
  void rejectsMalformedFramingAndLamp(String text) {
    assertThrows(InvalidTimeException.class, () -> BerlinClockTime.parse(text));
  }

  @ParameterizedTest
  @CsvSource({
          "RORO,OOOO,OOOOOOOOOOO,OOOO", "OOOO,RORO,OOOOOOOOOOO,OOOO",
          "OOOO,OOOO,YOYOOOOOOOO,OOOO", "OOOO,OOOO,OOOOOOOOOOO,YOYO",
          "OOOO,OOOO,YYYOOOOOOOO,OOOO", "OOOO,OOOO,YYRYYYOOOOO,OOOO",
          "RRRR,RRRR,OOOOOOOOOOO,OOOO", "YOOO,OOOO,OOOOOOOOOOO,OOOO",
          "OOOO,OOOO,OOOOOOOOOOO,ROOO"
  })
  void parsingCannotBypassRowOrCompositionInvariants(
          String fiveHours, String singleHours, String fiveMinutes, String singleMinutes) {
    assertThrows(
            InvalidTimeException.class,
            () -> BerlinClockTime.parse("Y" + fiveHours + singleHours + fiveMinutes + singleMinutes));
  }

  @ParameterizedTest
  @CsvSource({"Y,exactly 24 lamps", "YRRRRRRRROOOOOOOOOOOOOOO,at most 23 hours"})
  void expectedFailuresExplainFramingAndCombinedHours(String text, String expected) {
    assertTrue(assertThrows(InvalidTimeException.class, () -> BerlinClockTime.parse(text)).getMessage().contains(expected));
  }
}
