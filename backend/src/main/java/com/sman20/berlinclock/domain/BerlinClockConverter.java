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

  public FiveHoursRow toFiveHoursRow(DigitalTime time) {
    return FiveHoursRow.from(time);
  }

  public SecondsLamp toSecondsLamp(DigitalTime time) {
    return SecondsLamp.from(time);
  }

  public BerlinClockTime toBerlinClockTime(DigitalTime time) {
    return new BerlinClockTime(SecondsLamp.from(time), FiveHoursRow.from(time),
            SingleHoursRow.from(time), FiveMinutesRow.from(time), SingleMinutesRow.from(time));
  }
}
