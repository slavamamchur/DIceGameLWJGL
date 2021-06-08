package com.cubegames.engine.domain.rest;

import com.cubegames.engine.consts.EntitySize;

public class SearchRequest {

  private String name;
  private EntitySize size;
  private int players;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public EntitySize getSize() {
    return size;
  }

  public void setSize(EntitySize size) {
    this.size = size;
  }

  public int getPlayers() {
    return players;
  }

  public void setPlayers(int players) {
    this.players = players;
  }

}
