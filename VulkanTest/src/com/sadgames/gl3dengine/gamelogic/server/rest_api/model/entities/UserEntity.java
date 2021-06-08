package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities;

public class UserEntity extends BasicEntity {

    private static final long serialVersionUID = -360850314355350792L;

    private String userName;
    private String userPass;
    private String email;
    private String language;

    @SuppressWarnings("unused")
    public UserEntity() {}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

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

}
