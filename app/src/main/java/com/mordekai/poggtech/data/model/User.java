package com.mordekai.poggtech.data.model;

public class User {
    private int user_id;
    private String firebase_uid;
    private String name;
    private String last_name;
    private String email;
    private String phone;
    private String created_at;
    private String error;

    public User(String name, String lastName, String email) {
        this.name = name;
        this.last_name = lastName;
        this.email = email;
    }

    // Getters e Setters
    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String GetFireUid() {
        return firebase_uid;
    }

    public void SetFireUid(String firebase_uid) {
        this.firebase_uid = firebase_uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
