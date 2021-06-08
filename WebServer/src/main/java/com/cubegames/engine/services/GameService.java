package com.cubegames.engine.services;

import com.cubegames.engine.dao.AbstractDao;
import com.cubegames.engine.dao.GameDao;
import com.cubegames.engine.domain.entities.Game;
import com.cubegames.engine.domain.entities.points.AbstractGamePoint;
import com.cubegames.engine.domain.entities.points.PointType;
import com.cubegames.engine.exceptions.ValidationBasicException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService extends AbstractServiceDb<Game> {

  public static final int MIN_POINTS_COUNT = 2;

  @Autowired
  protected GameDao dao;

  @Override
  public Class getDomain() {
    return Game.class;
  }

  @Override
  public AbstractDao getDao() {
    return dao;
  }


  @Override
  public void validateOnCreate(Game game) throws ValidationBasicException {
    super.validateOnCreate(game);

    game.setCreatedDate(System.currentTimeMillis());
  }


  public void validate(Game game) {
    List<AbstractGamePoint> points = game.getGamePoints();

    if (points == null) {
      throw new ValidationBasicException("Game must have Points");
    }

    if (points.size() < MIN_POINTS_COUNT) {
      throw new ValidationBasicException("Game must have at least 2 Points");
    }

    // 1st point must be START
    AbstractGamePoint point0 = points.get(0);
    if (point0.getType() != PointType.START) {
      throw new ValidationBasicException("First Point must be START");
    }

    // At least 1 FINISH must be
    boolean wasFinish = false;
    for (AbstractGamePoint point : points) {
      if (point.getType() == PointType.FINISH) {
        wasFinish = true;
        break;
      }
    }

    if (!wasFinish) {
      throw new ValidationBasicException("Game must have FINISH point");
    }
  }
}
