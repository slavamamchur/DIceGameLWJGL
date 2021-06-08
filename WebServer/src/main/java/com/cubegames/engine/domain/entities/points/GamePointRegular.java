package com.cubegames.engine.domain.entities.points;

/**
 * Regular point
 */
public class GamePointRegular extends AbstractGamePointMove {

  public GamePointRegular() {
    this.additionalMoves = 0;  // active player should not do additional moving
    this.type = PointType.REGULAR;
  }

}
