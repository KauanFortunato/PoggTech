package com.mordekai.poggtech.data.model;

public class ApiResponse {
    private Boolean success;
    private String message;

    // Getter e Setter
    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
