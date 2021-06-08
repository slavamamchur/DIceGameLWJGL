package com.cubegames.engine.domain.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BasicNamedDbEntity extends BasicDbEntity {

  public static final String FIELD_NAME = "name";

  private static final long serialVersionUID = -4720020904449211330L;

  @JsonProperty(required = true)
  protected String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
