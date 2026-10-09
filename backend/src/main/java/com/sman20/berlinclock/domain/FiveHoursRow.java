package com.sman20.berlinclock.domain;

public record FiveHoursRow(String lamps) {
  private static final LampPattern LAMP_PATTERN = new LampPattern("RRRR");
  static final int LENGTH = LAMP_PATTERN.length();

  public FiveHoursRow {
    LAMP_PATTERN.requireValid(lamps, "Five Hours Row");
  }

  static FiveHoursRow from(DigitalTime time) {
    return new FiveHoursRow(LAMP_PATTERN.illuminate(time.hours() / 5));
  }

  int hours() {
    return 5 * LAMP_PATTERN.illuminatedCount(lamps);
  }
}
