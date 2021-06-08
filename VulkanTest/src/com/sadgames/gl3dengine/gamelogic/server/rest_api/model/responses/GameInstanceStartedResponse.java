package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameInstanceStartedResponse extends BasicEntity{

    private GameInstanceEntity instance;
    private String error;

    public GameInstanceStartedResponse() {
        // for serializer
    }

    public GameInstanceStartedResponse(GameInstanceEntity instance) {
        this.instance = instance;
    }


    public GameInstanceEntity getInstance() {
        return instance;
    }
    public void setInstance(GameInstanceEntity instance) {
        this.instance = instance;
    }

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}

