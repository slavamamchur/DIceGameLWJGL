package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthTokenEntity extends BasicEntity {

    private static final long serialVersionUID = 2059404078619968113L;

    private String id;

    @SuppressWarnings("unused")
    public AuthTokenEntity() {}
    public AuthTokenEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

}
