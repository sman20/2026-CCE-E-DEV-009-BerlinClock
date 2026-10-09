package com.sman20.berlinclock.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ReverseConversionTest {
  private final BerlinClockConverter converter = new BerlinClockConverter();
  private static final String[] HOURS = {"OOOO", "ROOO", "RROO", "RRRO", "RRRR"};
  private static final String[] SINGLE_MINUTES = {"OOOO", "YOOO", "YYOO", "YYYO", "YYYY"};
  private static final String[] FIVE_MINUTES = {
    "OOOOOOOOOOO", "YOOOOOOOOOO", "YYOOOOOOOOO", "YYROOOOOOOO",
    "YYRYOOOOOOO", "YYRYYOOOOOO", "YYRYYROOOOO", "YYRYYRYOOOO",
    "YYRYYRYYOOO", "YYRYYRYYROO", "YYRYYRYYRYO", "YYRYYRYYRYY"
  };

  @ParameterizedTest(name = "{0}: published {1}, representative {2}")
  @CsvSource({
    "YOOOOOOOOOOOOOOOOOOOOOOO,00:00:00,00:00:00",
    "ORRRRRRROYYRYYRYYRYYYYYY,23:59:59,23:59:01",
    "YRRROROOOYYRYYRYYRYOOOOO,16:50:06,16:50:00",
    "ORROOROOOYYRYYRYOOOOYYOO,11:37:01,11:37:01",
    "ORROORRROYYROOOOOOOOYYOO,13:17:01,13:17:01"
  })
  void reverseExamples(String text, String published, String expected) {
    assertEquals(expected, converter.toDigitalTime(BerlinClockTime.parse(text)).text());
    assertEquals(text, BerlinClockTime.parse(text).lamps());
  }

  @Test
  void allDigitalTimesHaveIndependentForwardSemanticsAndRoundTripParity() {
    int checked = 0;
    for (int hour = 0; hour < 24; hour++) {
      for (int minute = 0; minute < 60; minute++) {
        for (int second = 0; second < 60; second++) {
          var time = new DigitalTime(hour, minute, second);
          var reading = converter.toBerlinClockTime(time);
          assertEquals(second % 2 == 0 ? "Y" : "O", reading.secondsLamp().lamps());
          assertEquals(HOURS[hour / 5], reading.fiveHoursRow().lamps());
          assertEquals(HOURS[hour % 5], reading.singleHoursRow().lamps());
          assertEquals(FIVE_MINUTES[minute / 5], reading.fiveMinutesRow().lamps());
          assertEquals(SINGLE_MINUTES[minute % 5], reading.singleMinutesRow().lamps());
          assertEquals(new DigitalTime(hour, minute, second % 2), converter.toDigitalTime(reading));
          checked++;
        }
      }
    }
    assertEquals(86400, checked);
  }

  @Test
  void allIndependentlyEnumeratedReadingsDecodeCorrectlyAndRoundTrip() {
    int checked = 0;
    for (int fiveHours = 0; fiveHours < HOURS.length; fiveHours++) {
      for (int singleHours = 0; singleHours < HOURS.length; singleHours++) {
        int hour = fiveHours * 5 + singleHours;
        if (hour > 23) {
          continue;
        }
        for (int fiveMinutes = 0; fiveMinutes < FIVE_MINUTES.length; fiveMinutes++) {
          for (int singleMinutes = 0; singleMinutes < SINGLE_MINUTES.length; singleMinutes++) {
            for (int second = 0; second < 2; second++) {
              String lamps =
                  (second == 0 ? "Y" : "O")
                      + HOURS[fiveHours]
                      + HOURS[singleHours]
                      + FIVE_MINUTES[fiveMinutes]
                      + SINGLE_MINUTES[singleMinutes];
              var reading = BerlinClockTime.parse(lamps);
              var decoded = converter.toDigitalTime(reading);
              assertEquals(new DigitalTime(hour, fiveMinutes * 5 + singleMinutes, second), decoded);
              assertEquals(lamps, converter.toBerlinClockTime(decoded).lamps());
              checked++;
            }
          }
        }
      }
    }
    assertEquals(2880, checked);
  }
}
