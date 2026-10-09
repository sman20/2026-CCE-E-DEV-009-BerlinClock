package com.sman20.berlinclock.domain;

public final class InvalidTimeException extends IllegalArgumentException {

  public InvalidTimeException(String detail) {
    super(detail);
  }
}
