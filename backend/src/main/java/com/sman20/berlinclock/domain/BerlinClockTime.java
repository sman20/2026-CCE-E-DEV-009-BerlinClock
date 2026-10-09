package com.sman20.berlinclock.domain;

import java.util.Objects;

public record BerlinClockTime(
        SecondsLamp secondsLamp,
        FiveHoursRow fiveHoursRow,
        SingleHoursRow singleHoursRow,
        FiveMinutesRow fiveMinutesRow,
        SingleMinutesRow singleMinutesRow) {
  private static final int FIVE_HOURS_START = SecondsLamp.LENGTH;
  private static final int SINGLE_HOURS_START = FIVE_HOURS_START + FiveHoursRow.LENGTH;
  private static final int FIVE_MINUTES_START = SINGLE_HOURS_START + SingleHoursRow.LENGTH;
  private static final int SINGLE_MINUTES_START = FIVE_MINUTES_START + FiveMinutesRow.LENGTH;
  private static final int LENGTH = SINGLE_MINUTES_START + SingleMinutesRow.LENGTH;

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
    return secondsLamp.lamps() + fiveHoursRow.lamps() + singleHoursRow.lamps()
            + fiveMinutesRow.lamps() + singleMinutesRow.lamps();
  }

  public int hours() {
    return fiveHoursRow.hours() + singleHoursRow.hours();
  }

  public int minutes() {
    return fiveMinutesRow.minutes() + singleMinutesRow.minutes();
  }

  public static BerlinClockTime parse(String text) {
    if (text == null || text.length() != LENGTH) {
      throw new InvalidTimeException("Berlin Clock Time must contain exactly " + LENGTH + " lamps.");
    }
    return new BerlinClockTime(
            new SecondsLamp(text.substring(0, FIVE_HOURS_START)),
            new FiveHoursRow(text.substring(FIVE_HOURS_START, SINGLE_HOURS_START)),
            new SingleHoursRow(text.substring(SINGLE_HOURS_START, FIVE_MINUTES_START)),
            new FiveMinutesRow(text.substring(FIVE_MINUTES_START, SINGLE_MINUTES_START)),
            new SingleMinutesRow(text.substring(SINGLE_MINUTES_START, LENGTH)));
  }
}
