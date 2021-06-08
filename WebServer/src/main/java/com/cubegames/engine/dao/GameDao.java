package com.cubegames.engine.dao;

import com.cubegames.engine.domain.entities.Game;

import org.springframework.stereotype.Repository;

@Repository(value = "gameDao")
public class GameDao extends AbstractDao<Game> {

  @Override
  public Class getDomain() {
    return Game.class;
  }

}
