package com.cubegames.engine.domain.entities.points;

/**
 * Finish of the game - last point
 */
public class GamePointFinish extends AbstractGamePointMove {

  public GamePointFinish() {
    this.additionalMoves = -1;  // active player should skip his moving
    this.type = PointType.FINISH;
  }

}
