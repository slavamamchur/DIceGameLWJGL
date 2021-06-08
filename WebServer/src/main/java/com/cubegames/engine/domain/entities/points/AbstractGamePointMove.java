package com.cubegames.engine.domain.entities.points;

public class AbstractGamePointMove extends AbstractGamePoint {

  protected int additionalMoves;

  public int getAdditionalMoves() {
    return additionalMoves;
  }

  public void setAdditionalMoves(int additionalMoves) {
    this.additionalMoves = additionalMoves;
  }
}
