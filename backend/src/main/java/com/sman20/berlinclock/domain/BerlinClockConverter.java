package com.sman20.berlinclock.domain;

public final class BerlinClockConverter {

  public BerlinClockTime toBerlinClockTime(DigitalTime time) {
    return new BerlinClockTime(
            SecondsLamp.from(time), FiveHoursRow.from(time), SingleHoursRow.from(time),
            FiveMinutesRow.from(time), SingleMinutesRow.from(time));
  }

  public DigitalTime toDigitalTime(BerlinClockTime time) {
    return new DigitalTime(0, 0, 0);
  }
}
