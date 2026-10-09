package com.sman20.berlinclock.domain;

public record SingleMinutesRow(String lamps) {
  private static final String COLOURS = "YYYY";
  static final int LENGTH = COLOURS.length();

  public SingleMinutesRow {
    if (lamps == null || lamps.length() != LENGTH) {
      throw new InvalidTimeException("Single Minutes Row must contain " + LENGTH + " lamps.");
    }
    boolean off = false;
    for (int i = 0; i < LENGTH; i++) {
      if (lamps.charAt(i) == 'O') {
        off = true;
      } else if (off || lamps.charAt(i) != COLOURS.charAt(i)) {
        throw new InvalidTimeException("Row must contain prescribed colours followed by O lamps.");
      }
    }
  }

  static SingleMinutesRow from(DigitalTime time) {
    int count = time.minutes() % 5;
    return new SingleMinutesRow(COLOURS.substring(0, count) + "O".repeat(LENGTH - count));
  }

  int minutes() {
    int firstOff = lamps.indexOf('O');
    return 1 * (firstOff < 0 ? LENGTH : firstOff);
  }
}
