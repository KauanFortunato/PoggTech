package com.mordekai.poggtech.data.repository;

import android.util.Log;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
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
        apiService.insertUser(user)
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
        Call<ApiResponse<User>> call = apiService.getUser(firebaseUid);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();

                    if(apiResponse.isSuccess()) {
                        User user = apiResponse.getData();
                        Log.d("Sucesso", "Usuário encontrado no XAMPP: " + user.getName());

                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(new Exception(apiResponse.getMessage()));
                    }

                } else {
                    callback.onFailure(new Exception("Erro ao buscar usuário no Servidor"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    @Override
    public void logoutUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loginUser(String email, String password, RepositoryCallback<String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void googleLogin(String idToken, RepositoryCallback<User> callback) {
        throw new UnsupportedOperationException();
    }
}
