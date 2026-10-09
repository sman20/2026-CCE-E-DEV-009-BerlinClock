package com.sman20.berlinclock.domain;

public record SingleHoursRow(String lamps) {
  private static final LampPattern LAMP_PATTERN = new LampPattern("RRRR");
  static final int LENGTH = LAMP_PATTERN.length();

  public SingleHoursRow {
    LAMP_PATTERN.requireValid(lamps, "Single Hours Row");
  }

  static SingleHoursRow from(DigitalTime time) {
    return new SingleHoursRow(LAMP_PATTERN.illuminate(time.hours() % 5));
  }

  int hours() {
    return 1 * LAMP_PATTERN.illuminatedCount(lamps);
  }
}
