package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities;

public abstract class BasicDbEntity extends BasicEntity {
    protected String id;
    protected String tenantId;
    protected boolean deleted;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
