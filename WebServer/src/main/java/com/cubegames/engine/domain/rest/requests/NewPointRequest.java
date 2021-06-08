package com.cubegames.engine.domain.rest.requests;

import com.cubegames.engine.domain.entities.points.PointType;

public class NewPointRequest {

  private String gameId;
  private int xPos;
  private int yPos;
  private PointType type;
  private int nextIndex;
  private int flyIndex;

  public String getGameId() {
    return gameId;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }

  public int getxPos() {
    return xPos;
  }

  public void setxPos(int xPos) {
    this.xPos = xPos;
  }

  public int getyPos() {
    return yPos;
  }

  public void setyPos(int yPos) {
    this.yPos = yPos;
  }

  public PointType getType() {
    return type;
  }

  public void setType(PointType type) {
    this.type = type;
  }

  public int getNextIndex() {
    return nextIndex;
  }

  public void setNextIndex(int nextIndex) {
    this.nextIndex = nextIndex;
  }

  public int getFlyIndex() {
    return flyIndex;
  }

  public void setFlyIndex(int flyIndex) {
    this.flyIndex = flyIndex;
  }

}
