package com.mordekai.poggtech.domain;

import android.util.Log;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.repository.UserRepository;


public class UserManager {

    private final UserRepository firebaseRepo;
    private final UserRepository mysqlRepo;

    public UserManager(UserRepository firebaseRepo, UserRepository mysqlRepo) {
        this.firebaseRepo = firebaseRepo;
        this.mysqlRepo = mysqlRepo;
    }

    public void loginUser(String email, String password, RepositoryCallback<User> callback) {
        firebaseRepo.loginUser(email, password, new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String result) {
                getUser(result, new RepositoryCallback<User>() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d("Sucesso", "Usuário encontrado: " + user.getFireUid());
                        callback.onSuccess(user);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        callback.onFailure(t);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void createUser(User user, String password, RepositoryCallback<User> callback) {
        firebaseRepo.registerUser(user, password, new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String uid) {
                Log.d("UserManager", "Usuário criado com sucesso no Firebase: " + uid);
                user.setFireUid(uid);

                mysqlRepo.registerUser(user, password, new RepositoryCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("Sucesso", "Usuário criado com sucesso no XAMPP: " + result);

                        loginUser(user.getEmail(), password, new RepositoryCallback<User>() {
                            @Override
                            public void onSuccess(User result) {
                                callback.onSuccess(result);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                callback.onFailure(t);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        callback.onFailure(t);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void googleLogin(String idToken, RepositoryCallback<User> callback) {
        firebaseRepo.googleLogin(idToken, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {
                // Verifica se o usuário já existe no XAMPP
                mysqlRepo.getUser(user.getFireUid(), new RepositoryCallback<User>() {
                    @Override
                    public void onSuccess(User existingUser) {
                        // Se o usuário já existe no XAMPP, retorna os dados armazenados
                        Log.d("Sucesso", "Usuário encontrado no XAMPP: " + existingUser.getFireUid());
                        callback.onSuccess(existingUser);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Se o usuário não existe no XAMPP, cria um novo registro
                        Log.d("Info", "Usuário não encontrado no XAMPP. Criando novo...");

                        mysqlRepo.registerUser(user, "senha_padrao", new RepositoryCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                Log.d("Sucesso", "Usuário registrado no XAMPP!");
                                callback.onSuccess(user);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.e("Erro", "Falha ao registrar usuário no XAMPP", t);
                                callback.onFailure(t);
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }


    public void logoutUser() {
        firebaseRepo.logoutUser();
    }

    public void getUser(String firebaseUid, RepositoryCallback<User> callback) {
        mysqlRepo.getUser(firebaseUid, callback);
    }
}

