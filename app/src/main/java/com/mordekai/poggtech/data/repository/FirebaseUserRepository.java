package com.mordekai.poggtech.data.repository;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.User;


public class FirebaseUserRepository implements UserRepository {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public void registerUser(User user, String password, RepositoryCallback<String> callback) {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            user.SetFireUid(firebaseUser.getUid());
                            callback.onSuccess(user.GetFireUid());
                        } else {
                            callback.onFailure(new Exception("Erro: FirebaseUser é nulo"));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    @Override
    public void loginUser(String email, String password, RepositoryCallback<String> callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            callback.onSuccess(firebaseUser.getUid());
                        } else {
                            callback.onFailure(new Exception("Erro: FirebaseUser é nulo"));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void getUser(String firebaseUid, RepositoryCallback<User> callback) {
        throw new UnsupportedOperationException();
    }
}
