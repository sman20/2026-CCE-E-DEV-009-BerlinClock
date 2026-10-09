package com.sman20.berlinclock.domain;

public record FiveMinutesRow(String lamps) {
  private static final LampPattern LAMP_PATTERN = new LampPattern("YYRYYRYYRYY");
  static final int LENGTH = LAMP_PATTERN.length();

  public FiveMinutesRow {
    LAMP_PATTERN.requireValid(lamps, "Five Minutes Row");
  }

  static FiveMinutesRow from(DigitalTime time) {
    return new FiveMinutesRow(LAMP_PATTERN.illuminate(time.minutes() / 5));
  }

  int minutes() {
    return 5 * LAMP_PATTERN.illuminatedCount(lamps);
  }
}
