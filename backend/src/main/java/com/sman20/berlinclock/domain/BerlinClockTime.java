package com.sman20.berlinclock.domain;

import java.util.Objects;

public record BerlinClockTime(
        SecondsLamp secondsLamp,
        FiveHoursRow fiveHoursRow,
        SingleHoursRow singleHoursRow,
        FiveMinutesRow fiveMinutesRow,
        SingleMinutesRow singleMinutesRow) {

  public BerlinClockTime {
    Objects.requireNonNull(secondsLamp, "Seconds Lamp");
    Objects.requireNonNull(fiveHoursRow, "Five Hours Row");
    Objects.requireNonNull(singleHoursRow, "Single Hours Row");
    Objects.requireNonNull(fiveMinutesRow, "Five Minutes Row");
    Objects.requireNonNull(singleMinutesRow, "Single Minutes Row");
    if (fiveHoursRow.hours() + singleHoursRow.hours() > 23) {
      throw new InvalidTimeException("Berlin Clock Time must represent at most 23 hours.");
    }
  }

  public String lamps() {
    return secondsLamp.lamps() + fiveHoursRow.lamps() + singleHoursRow.lamps() + fiveMinutesRow.lamps() + singleMinutesRow.lamps();
  }

  public int hours() {
    return fiveHoursRow.hours() + singleHoursRow.hours();
  }

  public int minutes() {
    return fiveMinutesRow.minutes() + singleMinutesRow.minutes();
  }

  public static BerlinClockTime parse(String text) {
    return new BerlinClockTime(new SecondsLamp("Y"), new FiveHoursRow("OOOO"), new SingleHoursRow("OOOO"),
            new FiveMinutesRow("OOOOOOOOOOO"), new SingleMinutesRow("OOOO"));
  }
}
