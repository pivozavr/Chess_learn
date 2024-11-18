package com.example.chess3;

public class User {
    private String email;
    private String password;
    private String status;
    private String username;
    private int lvl;

    // Default constructor
    public User() {
        this.email = "";
        this.password = "";
        this.status = "";
        this.username = "";
        this.lvl = 0;
    }

    // Parameterized constructor
    public User(String email, String password, String username, int lvl) {
        this.password = password;
        this.email = email;
        this.username = username;
        this.status = "offline";
        this.lvl = lvl;
    }

    // Getters and Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }
}
