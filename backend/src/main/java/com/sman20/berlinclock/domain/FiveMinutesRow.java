package com.sman20.berlinclock.domain;

public record FiveMinutesRow(String lamps) {
  private static final String COLORS = "YYRYYRYYRYY";
  static final int LENGTH = COLORS.length();

  public FiveMinutesRow {
    if (lamps == null || lamps.length() != LENGTH) {
      throw new InvalidTimeException("Five Minutes Row must contain " + LENGTH + " lamps.");
    }
    boolean off = false;
    for (int i = 0; i < LENGTH; i++) {
      if (lamps.charAt(i) == 'O') {
        off = true;
      } else if (off || lamps.charAt(i) != COLORS.charAt(i)) {
        throw new InvalidTimeException("Row must contain prescribed colours followed by O lamps.");
      }
    }
  }

  static FiveMinutesRow from(DigitalTime time) {
    int count = time.minutes() / 5;
    return new FiveMinutesRow(COLORS.substring(0, count) + "O".repeat(LENGTH - count));
  }

  int minutes() {
    int firstOff = lamps.indexOf('O');
    return 5 * (firstOff < 0 ? LENGTH : firstOff);
  }
}
