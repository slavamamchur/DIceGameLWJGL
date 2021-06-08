package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.params;


import com.cubegames.engine.domain.entities.players.InstancePlayer;

import java.io.Serializable;
import java.util.List;

public class StartNewGameRequestParam implements Serializable {

    private static final long serialVersionUID = 5756812446346174496L;

    private String gameId;
    private String name;
    private List<InstancePlayer> players;
    private List<String> playersId;

    public StartNewGameRequestParam(){}

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
