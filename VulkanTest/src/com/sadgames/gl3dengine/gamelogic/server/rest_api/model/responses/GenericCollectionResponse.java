package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericCollectionResponse<T> extends BasicResponse {
    private Collection<T> collection;

    public Collection<T> getCollection() {
        return collection;
    }

    public void setCollection(Collection<T> collection) {
        this.collection = collection;
    }
}
