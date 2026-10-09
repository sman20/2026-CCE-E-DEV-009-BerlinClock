package com.sman20.berlinclock.domain;

public record SingleHoursRow(String lamps) {
  static final int LENGTH = 4;

  static SingleHoursRow from(DigitalTime time) {
    return new SingleHoursRow("O".repeat(LENGTH));
  }

  int hours() {
    return 0;
  }
}
