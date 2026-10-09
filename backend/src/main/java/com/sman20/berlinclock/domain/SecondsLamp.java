package com.sman20.berlinclock.domain;

public record SecondsLamp(String lamps) {
  static final int LENGTH = 1;

  static SecondsLamp from(DigitalTime time) {
    return new SecondsLamp("O".repeat(LENGTH));
  }

  public boolean isOn() {
    return false;
  }
}
