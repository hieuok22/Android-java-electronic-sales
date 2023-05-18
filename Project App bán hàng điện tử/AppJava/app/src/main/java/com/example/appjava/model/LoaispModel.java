package com.example.appjava.model;

import java.util.List;

public class LoaispModel {
    boolean success;
    String message;
    List<LoaiSp> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<LoaiSp> getResut() {
        return result;
    }

    public void setResut(List<LoaiSp> resut) {
        this.result = resut;
    }
}
