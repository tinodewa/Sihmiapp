package com.roma.android.sihmi.model.database.entity;

public class Account {
    private int id_account;
    private String username, password, image;

    public Account(int id_account, String username, String password, String image) {
        this.id_account = id_account;
        this.username = username;
        this.password = password;
        this.image = image;
    }

    public int getId_account() {
        return id_account;
    }

    public void setId_account(int id_account) {
        this.id_account = id_account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
