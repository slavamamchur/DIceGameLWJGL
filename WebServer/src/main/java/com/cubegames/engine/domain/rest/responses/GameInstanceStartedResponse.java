package com.cubegames.engine.domain.rest.responses;

import com.cubegames.engine.domain.entities.GameInstance;

public class GameInstanceStartedResponse extends BasicResponse {

  private GameInstance instance;

  public GameInstanceStartedResponse() {
    // for serializer
  }


  public GameInstanceStartedResponse(GameInstance instance) {
    this.instance = instance;
  }


  public GameInstance getInstance() {
    return instance;
  }

  public void setInstance(GameInstance instance) {
    this.instance = instance;
  }

}
