package com.cubegames.engine.domain.entities;

import java.util.Collection;

public abstract class BasicSizedNamedDbEntity extends BasicNamedDbEntity {

  public static final String FIELD_ENTITY_SIZE = "entitySize";

  private int entitySize;

  public int getEntitySize() {
    return entitySize;
  }

  public void setEntitySize(int entitySize) {
    this.entitySize = entitySize;
  }

  /**
   * Override this method to calculate size of entity
   * @return size of entity (points count, for example)
   */
  public abstract int calculateEntitySize();

  protected int calculateCollectionSize(Collection collection) {
    if (collection != null) {
      return collection.size();
    }
    return 0;
  }

}
