package com.sman20.berlinclock.domain;

final class LampPattern {
  private final String colors;

  LampPattern(String colors) {
    this.colors = colors;
  }

  int length() {
    return colors.length();
  }

  void requireValid(String lamps, String row) {
    if (lamps == null || lamps.length() != length()) {
      throw new InvalidTimeException(row + " must contain " + length() + " lamps.");
    }
    boolean off = false;
    for (int i = 0; i < length(); i++) {
      if (lamps.charAt(i) == 'O') {
        off = true;
      } else if (off || lamps.charAt(i) != colors.charAt(i)) {
        throw new InvalidTimeException(row + " must contain prescribed colors followed by O lamps.");
      }
    }
  }

  String illuminate(int count) {
    return colors.substring(0, count) + "O".repeat(length() - count);
  }

  int illuminatedCount(String validatedLamps) {
    int firstOff = validatedLamps.indexOf('O');
    return firstOff < 0 ? length() : firstOff;
  }
}
