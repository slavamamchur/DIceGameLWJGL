package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.AbstractController;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.GameMapController;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameMapEntity extends BasicNamedDbEntity {

    private static final long serialVersionUID = -1298194783869611909L;

    @JsonProperty(required = false)
    public long createdDate;
    public long lastUsedDate;
    @JsonProperty(required = false)
    private byte[] binaryData;
    @JsonProperty(required = false)
    private byte[] binaryDataRelief;

    public GameMapEntity() {}

    @SuppressWarnings("unused") public GameMapEntity(String id) {
        this.id = id;
        this.createdDate = System.currentTimeMillis();
    }

    public long getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }
    public byte[] getBinaryData() {
        return binaryData;
    }
    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }
    public byte[] getBinaryDataRelief() {
        return binaryDataRelief;
    }
    public void setBinaryDataRelief(byte[] binaryDataRelief) {
        this.binaryDataRelief = binaryDataRelief;
    }
    public long getLastUsedDate() {
        return lastUsedDate;
    }
    public void setLastUsedDate(long lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    @Override
    public String actionURL() {
        return RestConst.URL_GAME_MAP;
    }

    @Override
    public AbstractController getController() {
        return new GameMapController();
    }
}
