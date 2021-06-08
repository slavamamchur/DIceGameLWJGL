package com.cubegames.engine.dao;

import com.cubegames.engine.domain.entities.GameInstance;

import org.springframework.stereotype.Repository;

@Repository(value = "gameInstanceDao")
public class GameInstanceDao extends AbstractDao<GameInstance> {

  @Override
  public Class<GameInstance> getDomain() {
    return GameInstance.class;
  }


  @Override
  public boolean allowSharedEntities() {
    return false;
  }

}
