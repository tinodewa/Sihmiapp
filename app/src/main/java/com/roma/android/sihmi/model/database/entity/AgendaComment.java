package com.roma.android.sihmi.model.database.entity;

public class AgendaComment {
    String id_agenda,id_user, username, message;
    long time;

    public AgendaComment(String id_agenda, String id_user, String username, String message, long time) {
        this.id_agenda = id_agenda;
        this.id_user = id_user;
        this.username = username;
        this.message = message;
        this.time = time;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_agenda() {
        return id_agenda;
    }

    public void setId_agenda(String id_agenda) {
        this.id_agenda = id_agenda;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
