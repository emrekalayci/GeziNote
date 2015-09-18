package com.gezinote.android.model;

/**
 * Created by Emre on 30.5.2015.
 */
public class User {

    private int gyId;
    private String email;
    private String username;
    private String fullname;
    private String created_at;

    public User() {
        super();
    }

    public User(int gyId, String email, String username, String fullname, String created_at) {
        super();
        this.gyId = gyId;
        this.email = email;
        this.username = username;
        this.fullname = fullname;
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return username;
    }

    public int getGyId() {
        return gyId;
    }

    public void setGyId(int gyId) {
        this.gyId = gyId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
