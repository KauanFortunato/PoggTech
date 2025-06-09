package com.mordekai.poggtech.data.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    private Boolean success;
    private String message;
    @SerializedName("data")
    private T data;

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

    public T getData() { return data; }
}
