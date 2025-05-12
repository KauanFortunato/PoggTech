package com.mordekai.poggtech.data.repository;

import android.util.Log;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.GoogleAuthProvider;
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

    @Override
    public void googleLogin(String idToken, RepositoryCallback<User> callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        if (firebaseUser != null) {
                            firebaseUser.reload().addOnCompleteListener(reloadTask -> {
                                // Agora os dados do usuário estão atualizados
                                FirebaseUser updatedUser = FirebaseAuth.getInstance().getCurrentUser();

                                if (updatedUser != null) {
                                    // Criar um objeto User com os dados do Firebase
                                    User user = new User();
                                    user.SetFireUid(updatedUser.getUid());
                                    user.setEmail(updatedUser.getEmail());
                                    user.setName(updatedUser.getDisplayName());

                                    Log.d("Auth", "Usuário atualizado! Nome: " + user.getName() + ", Email: " + user.getEmail());

                                    callback.onSuccess(user);
                                } else {
                                    callback.onFailure(new Exception("Erro: FirebaseUser é nulo após reload"));
                                }
                            });
                        } else {
                            callback.onFailure(new Exception("Erro: FirebaseUser é nulo"));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }



    @Override
    public void logoutUser() {
        firebaseAuth.signOut();
        Log.d("Sucesso", "Usuário deslogado com sucesso!");
    }

    public void getUser(String firebaseUid, RepositoryCallback<User> callback) {
        throw new UnsupportedOperationException();
    }
}
