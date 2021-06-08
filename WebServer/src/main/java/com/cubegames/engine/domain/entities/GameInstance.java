package com.cubegames.engine.domain.entities;

import com.cubegames.engine.domain.entities.players.InstancePlayer;

import java.util.List;


public class GameInstance extends BasicSizedNamedDbEntity {

  public static final String FIELD_LAST_USED_DATE = "lastUsedDate";

  private Game game;  // exactly Game, not Game's Id
  private List<InstancePlayer> players;
  private int currentPlayer;
  private int stepsToGo;
  private State state;
  private long startedDate;
  private long lastUsedDate;

  @Override
  public int calculateEntitySize() {
    return calculateCollectionSize(players);
  }


  public List<InstancePlayer> getPlayers() {
    return players;
  }

  public void setPlayers(List<InstancePlayer> players) {
    this.players = players;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public int getCurrentPlayer() {
    return currentPlayer;
  }

  public void setCurrentPlayer(int currentPlayer) {
    this.currentPlayer = currentPlayer;
  }

  public int getStepsToGo() {
    return stepsToGo;
  }

  public void setStepsToGo(int stepsToGo) {
    this.stepsToGo = stepsToGo;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public long getStartedDate() {
    return startedDate;
  }

  public void setStartedDate(long startedDate) {
    this.startedDate = startedDate;
  }

  public long getLastUsedDate() {
    return lastUsedDate;
  }

  public void setLastUsedDate(long lastUsedDate) {
    this.lastUsedDate = lastUsedDate;
  }

  public enum State { WAIT, MOVING, FINISHED }
}
