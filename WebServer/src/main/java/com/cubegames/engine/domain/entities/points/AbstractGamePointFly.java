package com.cubegames.engine.domain.entities.points;

public class AbstractGamePointFly extends AbstractGamePoint {

  protected int flyToPoint;

  public int getFlyToPoint() {
    return flyToPoint;
  }

  public void setFlyToPoint(int flyToPoint) {
    this.flyToPoint = flyToPoint;
  }
}
