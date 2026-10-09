package com.sman20.berlinclock.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
        () ->
            new BerlinClockTime(
                SECONDS, FIVE_HOURS, new SingleHoursRow("RRRR"), FIVE_MINUTES, SINGLE_MINUTES));
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
}
