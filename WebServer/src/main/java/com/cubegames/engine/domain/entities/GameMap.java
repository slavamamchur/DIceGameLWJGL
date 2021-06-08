package com.cubegames.engine.domain.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class GameMap extends BasicNamedDbEntity implements Serializable {

  private static final long serialVersionUID = -9005418836642830363L;

  public static final String FIELD_LAST_USED_DATE = "lastUsedDate";

  @JsonProperty(required = false)
  private long createdDate;

  @JsonProperty(required = false)
  private long lastUsedDate;

  @JsonProperty(required = false)
  private byte[] binaryData;

  @JsonProperty(required = false)
  private byte[] binaryDataSmall;

  @JsonProperty(required = false)
  private byte[] binaryDataRelief;


  public long getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(long createdDate) {
    this.createdDate = createdDate;
  }

  public long getLastUsedDate() {
    return lastUsedDate;
  }

  public void setLastUsedDate(long lastUsedDate) {
    this.lastUsedDate = lastUsedDate;
  }

  public byte[] getBinaryData() {
    return binaryData;
  }

  public byte[] getBinaryDataSmall() {
    return binaryDataSmall;
  }

  public void setBinaryData(byte[] binaryData) {
    this.binaryData = binaryData;
  }

  public void setBinaryDataSmall(byte[] binaryDataSmall) {
    this.binaryDataSmall = binaryDataSmall;
  }

  public byte[] getBinaryDataRelief() {
    return binaryDataRelief;
  }

  public void setBinaryDataRelief(byte[] binaryDataRelief) {
    this.binaryDataRelief = binaryDataRelief;
  }
}
