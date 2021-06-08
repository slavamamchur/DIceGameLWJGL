package com.cubegames.engine.services;

import com.cubegames.engine.dao.AbstractDao;
import com.cubegames.engine.dao.DbPlayerDao;
import com.cubegames.engine.domain.entities.DbPlayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DbPlayerService extends AbstractServiceDb<DbPlayer> {

  @Autowired
  protected DbPlayerDao dao;

  @Override
  public Class<DbPlayer> getDomain() {
    return DbPlayer.class;
  }

  @Override
  public AbstractDao<DbPlayer> getDao() {
    return dao;
  }

}
