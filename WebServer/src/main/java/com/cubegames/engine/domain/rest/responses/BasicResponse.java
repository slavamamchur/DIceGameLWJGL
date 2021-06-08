package com.cubegames.engine.domain.rest.responses;

public class BasicResponse {

  private String error;
  private int errorCode;
  private boolean auth;

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public boolean isAuth() {
    return auth;
  }

  public void setAuth(boolean auth) {
    this.auth = auth;
  }
}
