package com.cubegames.engine.exceptions;

public class BasicException extends RuntimeException {

  private int errorCode;

  public BasicException(String message) {
    super(message);
  }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }
}
