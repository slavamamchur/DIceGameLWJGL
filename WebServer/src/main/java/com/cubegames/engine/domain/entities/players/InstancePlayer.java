package com.cubegames.engine.domain.entities.players;

import java.io.Serializable;

public class InstancePlayer implements Serializable {

  private static final long serialVersionUID = -6815839058727920425L;

  public String name;
  public int color;
  public int currentPoint;
  public boolean finished;
  public boolean skipped;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public int getCurrentPoint() {
    return currentPoint;
  }

  public void setCurrentPoint(int currentPoint) {
    this.currentPoint = currentPoint;
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFinished(boolean finished) {
    this.finished = finished;
  }

  public boolean isSkipped() {
    return skipped;
  }

  public void setSkipped(boolean skipped) {
    this.skipped = skipped;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    InstancePlayer player = (InstancePlayer) o;

    if (color != player.color) return false;
    if (currentPoint != player.currentPoint) return false;
    if (finished != player.finished) return false;
    if (skipped != player.skipped) return false;
    return name.equals(player.name);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + color;
    result = 31 * result + currentPoint;
    result = 31 * result + (finished ? 1 : 0);
    result = 31 * result + (skipped ? 1 : 0);
    return result;
  }
}
