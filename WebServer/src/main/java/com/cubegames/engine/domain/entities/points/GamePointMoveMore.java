package com.cubegames.engine.domain.entities.points;

/**
 * New additional moving
 */
public class GamePointMoveMore extends AbstractGamePointMove {

  public GamePointMoveMore() {
    this.additionalMoves = 1;  // active player should make additional moving
    this.type = PointType.MOVE_MORE;
  }
}
