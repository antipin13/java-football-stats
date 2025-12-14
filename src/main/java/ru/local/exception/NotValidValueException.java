package ru.local.exception;

public class NotValidValueException extends RuntimeException {
  public NotValidValueException(String message) {
    super(message);
  }
}
