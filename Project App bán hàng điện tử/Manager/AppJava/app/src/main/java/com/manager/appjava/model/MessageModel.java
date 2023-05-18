package com.manager.appjava.model;

public class MessageModel {
    boolean success;
    String message;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSucces() {
        return success;
    }

    public void setSucces(boolean succes) {
        this.success = succes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
