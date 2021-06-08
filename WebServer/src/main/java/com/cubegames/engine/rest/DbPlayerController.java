package com.cubegames.engine.rest;

import com.cubegames.engine.domain.entities.DbPlayer;
import com.cubegames.engine.services.AbstractServiceDb;
import com.cubegames.engine.services.DbPlayerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = RestConst.URL_PLAYER, produces = MediaType.APPLICATION_JSON_VALUE)
public class DbPlayerController extends AbstractController<DbPlayer> {

  @Autowired
  private DbPlayerService service;

  @Override
  protected AbstractServiceDb<DbPlayer> getService() {
    return service;
  }

}
