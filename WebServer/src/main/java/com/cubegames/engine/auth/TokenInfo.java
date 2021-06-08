package com.cubegames.engine.auth;

public class TokenInfo {

  private String userId;
  private long expireDate;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public long getExpireDate() {
    return expireDate;
  }

  public void setExpireDate(long expireDate) {
    this.expireDate = expireDate;
  }
}
