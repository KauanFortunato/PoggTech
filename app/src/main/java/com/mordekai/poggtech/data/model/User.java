package com.mordekai.poggtech.data.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("user_id")
    private int user_id;
    @SerializedName("firebase_uid")
    private String firebase_uid;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("phone")
    private String phone;
    private String created_at;
    private String error;
    private String token;
    private boolean isGoogle;

    public User() {
    }

    public User(String name, String lastName, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters e Setters
    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getFireUid() {
        return firebase_uid;
    }

    public void setFireUid(String firebase_uid) {
        this.firebase_uid = firebase_uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public boolean getIsGoogle() { return isGoogle; }

    public void setIsGoogle(boolean isGoogle) { this.isGoogle = isGoogle; }
}
