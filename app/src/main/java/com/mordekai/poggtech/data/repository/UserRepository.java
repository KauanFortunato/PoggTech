package com.mordekai.poggtech.data.repository;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.User;

public interface UserRepository {
    void registerUser(User user, String password, RepositoryCallback<String> callback);
    void loginUser(String email, String password, RepositoryCallback<String> callback);
    void googleLogin(String idToken, RepositoryCallback<User> callback);
    void logoutUser();
    void getUser(String firebaseUid, RepositoryCallback<User> callback);
}
