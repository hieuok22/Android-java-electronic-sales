package com.example.appjava.model;

import java.util.List;

public class SanPhamMoiModel {
    boolean success;
    String message;
    List<SamPhamMoi> result;

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

    public List<SamPhamMoi> getResult() {
        return result;
    }

    public void setResult(List<SamPhamMoi> result) {
        this.result = result;
    }
}
