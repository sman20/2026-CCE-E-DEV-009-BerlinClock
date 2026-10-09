package com.sman20.berlinclock.domain;

public record SecondsLamp(String lamps) {
  static final int LENGTH = 1;

  public SecondsLamp {
    if (!"Y".equals(lamps) && !"O".equals(lamps)) {
      throw new InvalidTimeException("Seconds Lamp must be Y or O.");
    }
  }

  static SecondsLamp from(DigitalTime time) {
    return new SecondsLamp(time.seconds() % 2 == 0 ? "Y" : "O");
  }

  public boolean isOn() {
    return lamps.equals("Y");
  }
}
