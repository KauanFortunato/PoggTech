package com.mordekai.poggtech.data.remote.request;

public class UpdateEmailRequest {
    private int user_id;
    private String email;

    public UpdateEmailRequest(int user_id, String email) {
        this.user_id = user_id;
        this.email = email;
    }
}
