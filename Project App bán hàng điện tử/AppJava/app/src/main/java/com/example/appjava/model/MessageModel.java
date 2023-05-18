package com.example.appjava.model;

public class MessageModel {
    boolean success;
    String message;
    String name;
    String iddonhang;

    public String getIddonhang() {
        return iddonhang;
    }

    public void setIddonhang(String iddonhang) {
        this.iddonhang = iddonhang;
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
