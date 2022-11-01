package com.example.quizapplication.Models;

import java.util.Date;

public class User {

    private String UID;
    private String email;
    private String username;
    private String country;
    private Boolean isAdmin;
    public User() {

    }

    public User(String UID, String email, String username, String country, Boolean isAdmin) {
        this.UID = UID;
        this.email = email;
        this.username = username;
        this.country = country;
        this.isAdmin = isAdmin;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
