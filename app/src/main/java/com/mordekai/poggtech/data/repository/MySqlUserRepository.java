package com.mordekai.poggtech.data.repository;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MySqlUserRepository implements UserRepository {
    private final ApiService apiService;

    public MySqlUserRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void registerUser(User user, String password, RepositoryCallback<String> callback) {
        apiService.insertUser(user.GetFireUid(), user.getName(), user.getLastName(), user.getEmail())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess("Usuário criado");
                        } else {
                            callback.onFailure(new Exception("Erro ao salvar no XAMPP: " + response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public void getUser(String firebaseUid, RepositoryCallback<User> callback) {
        Call<User> call = apiService.getUser(firebaseUid);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    if (user.getError() != null) {
                        callback.onFailure(new Exception(user.getError()));
                    } else {
                        callback.onSuccess(user);
                    }
                } else {
                    callback.onFailure(new Exception("Erro ao buscar usuário no Servidor"));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    @Override
    public void loginUser(String email, String password, RepositoryCallback<String> callback) {
        throw new UnsupportedOperationException();
    }
}
