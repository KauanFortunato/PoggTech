package com.mordekai.poggtech.data.repository;

import android.util.Log;

import com.google.firebase.auth.AuthCredential;
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
                            user.setFireUid(firebaseUser.getUid());
                            callback.onSuccess(user.getFireUid());
                        } else {
                            callback.onFailure(new Exception("Erro: FirebaseUser é nulo"));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    @Override
    public void deleteUser(String firebaseUid, RepositoryCallback<Void> callback) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("DeleteUser", "Conta deletada com sucesso.");
                            callback.onSuccess(null);
                        } else {
                            Log.e("DeleteUser", "Erro ao deletar conta", task.getException());
                            callback.onFailure(new Exception("Erro ao deletar na Firebase: " + task.getException()));
                        }
                    });
        } else {
            callback.onFailure(new Exception("Erro: FirebaseUser é nulo"));
        }
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
                                // Agora os dados do utilizador estão atualizados
                                FirebaseUser updatedUser = FirebaseAuth.getInstance().getCurrentUser();

                                if (updatedUser != null) {
                                    // Criar um objeto User com os dados do Firebase
                                    User user = new User();
                                    user.setFireUid(updatedUser.getUid());
                                    user.setEmail(updatedUser.getEmail());
                                    user.setName(updatedUser.getDisplayName());

                                    Log.d("Auth", "Utilizador atualizado! Nome: " + user.getName() + ", Email: " + user.getEmail());

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
        Log.d("Sucesso", "Utilizador deslogado com sucesso!");
    }

    public void getUser(String firebaseUid, RepositoryCallback<User> callback) {
        throw new UnsupportedOperationException();
    }
}
