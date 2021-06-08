package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.gl3dengine.gamelogic.client.GameConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.AbstractController;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.GameInstanceController;
import com.sadgames.sysutils.common.LuaUtils;
import com.cubegames.engine.domain.entities.players.InstancePlayer;

import org.luaj.vm2.LuaTable;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameInstanceEntity extends BasicNamedDbEntity {

    private static final long serialVersionUID = -8604963480687767704L;

    private GameEntity game;
    public List<InstancePlayer> players;
    protected int currentPlayer;
    protected int stepsToGo;
    public GameConst.GameState state;
    public long startedDate;
    public long lastUsedDate;

    public GameInstanceEntity() {}

    public List<InstancePlayer> getPlayers() {
        return players;
    }
    public void setPlayers(List<InstancePlayer> players) {
        this.players = players;
    }

    public GameEntity getGame() {
        return game;
    }
    public void setGame(GameEntity game) {
        this.game = game;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }
    @SuppressWarnings("unused") public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getStepsToGo() {
        return stepsToGo;
    }
    @SuppressWarnings("unused") public void setStepsToGo(int stepsToGo) {
        this.stepsToGo = stepsToGo;
    }

    public GameConst.GameState getState() {
        return state;
    }
    public void setState(GameConst.GameState state) {
        this.state = state;
    }
    @SuppressWarnings("unused") public void setStateLua(int state) {
        this.state = GameConst.GameState.values()[state];
    }

    @SuppressWarnings("unused") public long getStartedDate() {
        return startedDate;
    }
    @SuppressWarnings("unused") public void setStartedDate(long startedDate) {
        this.startedDate = startedDate;
    }

    public long getLastUsedDate() {
        return lastUsedDate;
    }
    @SuppressWarnings("unused") public void setLastUsedDate(long lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    @Override
    public String actionURL() {
        return RestConst.URL_GAME_INSTANCE;
    }

    @Override
    public AbstractController getController() {
        return new GameInstanceController();
    }

    public List<InstancePlayer> createPlayersList() {
        return new ArrayList<>(players);
    }
    @SuppressWarnings("unused") public LuaTable createPlayersListLua() {
        return LuaUtils.javaList2LuaTable(new ArrayList<>(players));
    }
}
