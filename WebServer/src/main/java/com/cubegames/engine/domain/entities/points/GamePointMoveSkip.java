package com.cubegames.engine.domain.entities.points;

/**
 * Skip a move
 */

public class GamePointMoveSkip extends AbstractGamePointMove {

  public GamePointMoveSkip() {
    this.additionalMoves = -1;  // active player should skip his moving
    this.type = PointType.MOVE_SKIP;
  }
}
