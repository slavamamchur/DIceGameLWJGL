package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.AbstractController;

public abstract class BasicNamedDbEntity extends BasicDbEntity {

    @JsonProperty(required = true)
    public String name;

    public BasicNamedDbEntity(){}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public abstract String actionURL();
    public abstract AbstractController getController();
}
