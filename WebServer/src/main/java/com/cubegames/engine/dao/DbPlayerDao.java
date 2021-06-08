package com.cubegames.engine.dao;


import com.cubegames.engine.domain.entities.DbPlayer;

import org.springframework.stereotype.Repository;

@Repository(value = "dbPlayerDao")
public class DbPlayerDao extends AbstractDao<DbPlayer> {

  @Override
  public Class<DbPlayer> getDomain() {
    return DbPlayer.class;
  }


  @Override
  public boolean allowSharedEntities() {
    return false;
  }
}
