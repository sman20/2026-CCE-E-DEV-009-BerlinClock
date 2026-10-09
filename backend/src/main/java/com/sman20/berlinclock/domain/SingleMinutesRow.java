package com.sman20.berlinclock.domain;

public record SingleMinutesRow(String lamps) {
  static final int LENGTH = 4;

  static SingleMinutesRow from(DigitalTime time) {
    return new SingleMinutesRow("O".repeat(LENGTH));
  }

  int minutes() {
    return 0;
  }
}
