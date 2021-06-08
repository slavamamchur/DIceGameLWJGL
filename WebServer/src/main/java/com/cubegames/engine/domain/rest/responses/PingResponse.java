package com.cubegames.engine.domain.rest.responses;

public class PingResponse {

  private String name;

  public PingResponse() {
    name = "CubeGames";
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    //this.name = name;
  }

}
