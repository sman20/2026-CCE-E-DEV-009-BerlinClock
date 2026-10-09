package com.sman20.berlinclock.domain;

public record SingleMinutesRow(String lamps) {
  private static final LampPattern LAMP_PATTERN = new LampPattern("YYYY");
  static final int LENGTH = LAMP_PATTERN.length();

  public SingleMinutesRow {
    LAMP_PATTERN.requireValid(lamps, "Single Minutes Row");
  }

  static SingleMinutesRow from(DigitalTime time) {
    return new SingleMinutesRow(LAMP_PATTERN.illuminate(time.minutes() % 5));
  }

  int minutes() {
    return 1 * LAMP_PATTERN.illuminatedCount(lamps);
  }
}
