package com.example.chess3;

public class User {
    public String email;
    public String password;
    public String status;
    private String username;
    private int lvl;

    public User() {
        String email ="";
        String password ="";
        String status ="";
        String lastMessage="";
        String username = "";
        int lvl=0;
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String password, String username, int lvl) {
        this.password = password;
        this.email = email;
        this.username = username;
        this.status="offline";
        this.lvl=lvl;
    }

}
