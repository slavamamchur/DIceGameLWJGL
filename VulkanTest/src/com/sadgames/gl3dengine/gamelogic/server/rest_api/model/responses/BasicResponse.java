package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;

public class BasicResponse extends BasicEntity {
    private String error;
    private int errorCode;
    private boolean auth;

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }

    public int getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isAuth() {
        return auth;
    }
    public void setAuth(boolean auth) {
        this.auth = auth;
    }

}
