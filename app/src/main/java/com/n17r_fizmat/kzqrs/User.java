package com.n17r_fizmat.kzqrs;

/**
 * Created by Alisher on 8/10/2016.
 */
public class User {
    private String username;
    private String avatar;
    private String userId;

    public User(String u, String a, String id) {
        this.username = u;
        this.avatar = a;
        this.userId = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
