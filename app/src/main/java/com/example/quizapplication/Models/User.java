package com.example.quizapplication.Models;

import java.io.Serializable;
import java.util.Date;

public class User {

    private String UID;
    private String email;
    private String username;
    private String admin;


    public User() {

    }

    public User(String UID, String email, String username, String admin) {
        this.UID = UID;
        this.email = email;
        this.username = username;
        this.admin = admin;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
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

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}


