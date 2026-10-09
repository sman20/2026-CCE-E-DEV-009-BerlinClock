package com.sman20.berlinclock.domain;

import java.util.Locale;
import java.util.regex.Pattern;

public record DigitalTime(int hours, int minutes, int seconds) {
  private static final Pattern DIGITAL_TIME_PATTERN = Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2}");

  public DigitalTime {
    if (hours < 0 || hours > 23) {
      throw new InvalidTimeException("Hours must be between 00 and 23.");
    }
    if (minutes < 0 || minutes > 59) {
      throw new InvalidTimeException("Minutes must be between 00 and 59.");
    }
    if (seconds < 0 || seconds > 59) {
      throw new InvalidTimeException("Seconds must be between 00 and 59.");
    }
  }

  public static DigitalTime parse(String text) {
    if (text == null || !DIGITAL_TIME_PATTERN.matcher(text).matches()) {
      throw new InvalidTimeException(
              "Digital time must be exactly HH:mm:ss with two ASCII digits per part.");
    }
    return new DigitalTime(Integer.parseInt(text.substring(0, 2)),
            Integer.parseInt(text.substring(3, 5)), Integer.parseInt(text.substring(6, 8)));
  }

  public String text() {
    return String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds);
  }
}
