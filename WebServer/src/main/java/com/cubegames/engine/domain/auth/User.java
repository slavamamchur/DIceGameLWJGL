package com.cubegames.engine.domain.auth;

import com.cubegames.engine.consts.CollectionConsts;
import com.cubegames.engine.domain.entities.BasicNamedDbEntity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = CollectionConsts.COLLECTION_USER)
public class User extends BasicNamedDbEntity {

  private String password;
  //private String tenantId;
  private String email;
  private String language;
  private UserRole userRole;
  private long lastLoginDate;
  private long createdDate;
  private boolean donated;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

//  public String getTenantId() {
//    return tenantId;
//  }
//
//  public void setTenantId(String tenantId) {
//    this.tenantId = tenantId;
//  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public UserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }

  public long getLastLoginDate() {
    return lastLoginDate;
  }

  public void setLastLoginDate(long lastLoginDate) {
    this.lastLoginDate = lastLoginDate;
  }

  public long getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(long createdDate) {
    this.createdDate = createdDate;
  }

  public boolean isDonated() {
    return donated;
  }

  public void setDonated(boolean donated) {
    this.donated = donated;
  }
}
