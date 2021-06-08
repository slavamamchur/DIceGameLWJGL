package com.cubegames.engine.domain.rest.responses;

import com.cubegames.engine.domain.entities.GameInstance;
import com.cubegames.engine.domain.entities.players.InstancePlayer;

public class GameInstanceResponse extends IdResponse {

  private int currentPlayer;
  private int stepsToGo;
  private GameInstance.State state;
  private long lastUsedDate;
  private boolean usersAreFinished;


  public GameInstanceResponse() {
    // constructor for serializer
  }

  public GameInstanceResponse(GameInstance instance) {
    super(instance.getId());
    this.currentPlayer = instance.getCurrentPlayer();
    this.stepsToGo = instance.getStepsToGo();
    this.state = instance.getState();
    this.lastUsedDate = instance.getLastUsedDate();

    this.usersAreFinished = true;
    for (InstancePlayer player : instance.getPlayers()) {
      if (!player.isFinished()) {
        this.usersAreFinished = false;
        break;
      }
    }
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

  public GameInstance.State getState() {
    return state;
  }

  public void setState(GameInstance.State state) {
    this.state = state;
  }

  public long getLastUsedDate() {
    return lastUsedDate;
  }

  public void setLastUsedDate(long lastUsedDate) {
    this.lastUsedDate = lastUsedDate;
  }

  public boolean isUsersAreFinished() {
    return usersAreFinished;
  }

  public void setUsersAreFinished(boolean usersAreFinished) {
    this.usersAreFinished = usersAreFinished;
  }
}
