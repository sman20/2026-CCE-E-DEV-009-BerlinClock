package com.sman20.berlinclock.domain;

public record FiveHoursRow(String lamps) {
  static final int LENGTH = 4;
  static FiveHoursRow from(DigitalTime time) { return new FiveHoursRow("O".repeat(LENGTH)); }
  int hours() { return 0; }
}
