package com.sman20.berlinclock.domain;

public record FiveMinutesRow(String lamps) {
  static final int LENGTH = 11;

  static FiveMinutesRow from(DigitalTime time) {
    return new FiveMinutesRow("O".repeat(LENGTH));
  }

  int minutes() {
    return 0;
  }
}
