package com.cubegames.engine.domain.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BasicDbEntity extends BasicEntity {

  public static final String FIELD_ID = "id";
  public static final String FIELD_UNDERSCORE_ID = "_id";
  public static final String FIELD_TENANT_ID = "tenantId";
  public static final String FIELD_DELETED = "deleted";

  private static final long serialVersionUID = 1673528234199904905L;

  @JsonProperty(required = false)
  protected String id;

  protected String tenantId;
  //@JsonProperty(required = false)
  protected boolean deleted;


  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}
