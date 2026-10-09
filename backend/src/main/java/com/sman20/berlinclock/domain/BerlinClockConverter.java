package com.sman20.berlinclock.domain;

public final class BerlinClockConverter {

  public SingleMinutesRow toSingleMinutesRow(DigitalTime time) {
    return SingleMinutesRow.from(time);
  }

  public FiveMinutesRow toFiveMinutesRow(DigitalTime time) {
    return FiveMinutesRow.from(time);
  }

  public SingleHoursRow toSingleHoursRow(DigitalTime time) {
    return SingleHoursRow.from(time);
  }
}
