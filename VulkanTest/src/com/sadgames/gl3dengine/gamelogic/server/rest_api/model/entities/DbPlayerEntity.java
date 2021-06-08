package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.AbstractController;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.DBPlayerController;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DbPlayerEntity extends BasicNamedDbEntity {

    private static final long serialVersionUID = 1709751985689672630L;

    public int color;

    public DbPlayerEntity() {}

    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String actionURL() {
        return RestConst.URL_PLAYER;
    }

    @Override
    public AbstractController getController() {
        return new DBPlayerController();
    }
}
