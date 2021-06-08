package com.cubegames.engine.domain.rest.requests;

import com.cubegames.engine.domain.entities.players.InstancePlayer;

import java.util.List;

public class StartNewGameRequest {

  private String gameId;
  private String name;
  private List<InstancePlayer> players;
  private List<String> playersId;

  public List<InstancePlayer> getPlayers() {
    return players;
  }

  public void setPlayers(List<InstancePlayer> players) {
    this.players = players;
  }

  public String getGameId() {
    return gameId;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getPlayersId() {
    return playersId;
  }

  public void setPlayersId(List<String> playersId) {
    this.playersId = playersId;
  }
}
