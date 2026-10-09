package com.sman20.berlinclock.domain;

public record BerlinClockTime(SecondsLamp secondsLamp, FiveHoursRow fiveHoursRow,
    SingleHoursRow singleHoursRow, FiveMinutesRow fiveMinutesRow, SingleMinutesRow singleMinutesRow) {

  public String lamps() {
    return "";
  }

  public int hours() {
    return 0;
  }
  public int minutes() {
    return 0;
  }
}
