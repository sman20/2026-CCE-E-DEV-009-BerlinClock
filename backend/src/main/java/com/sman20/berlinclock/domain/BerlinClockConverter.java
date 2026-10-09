package com.sman20.berlinclock.domain;

public final class BerlinClockConverter {

  public SingleMinutesRow toSingleMinutesRow(DigitalTime time) {
    return SingleMinutesRow.from(time);
  }
}
