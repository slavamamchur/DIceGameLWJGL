package com.cubegames.engine.domain.rest.responses;

import java.util.Collection;

public class CollectionResponse<T> extends BasicResponse {

  private Collection<T> collection;

  public Collection<T> getCollection() {
    return collection;
  }

  public void setCollection(Collection<T> collection) {
    this.collection = collection;
  }
}
