package com.sman20.berlinclock.domain;

public record DigitalTime(int hours, int minutes, int seconds) {

  public static DigitalTime parse(String text) {
    return new DigitalTime(0, 0, 0);
  }

  public String text() {
    return "";
  }
}
