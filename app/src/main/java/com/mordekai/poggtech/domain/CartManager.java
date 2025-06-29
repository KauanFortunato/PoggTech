package com.mordekai.poggtech.domain;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ApiProduct;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartManager {
    private final ApiProduct apiProduct;

    public CartManager(ApiProduct apiProduct) {
        this.apiProduct = apiProduct;
    }

    public void addToCart(int product_id, int user_id, int quantity, RepositoryCallback<ApiResponse<Void>> callback) {
        Call<ApiResponse<Void>> call = apiProduct.addToCart(product_id, user_id, quantity);
        call.enqueue(new Callback<ApiResponse<Void>>() {

            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Não há mais produtos disponíveis"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onFailure(new Exception("Falha na conexão: " + t.getMessage()));
            }
        });
    }

    public void saveProduct(int product_id, int user_id, RepositoryCallback<ApiResponse<Void>> callback) {
        Call<ApiResponse<Void>> call = apiProduct.saveProduct(product_id, user_id);
        call.enqueue(new Callback<ApiResponse<Void>>() {

            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Erro ao adicionar aos favoritos/carrinho: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onFailure(new Exception("Falha na conexão: " + t.getMessage()));
            }
        });
    }

    public void removeOneFromCart(int product_id, int user_id, int tipo, RepositoryCallback<ApiResponse<Void>> callback) {
        Call<ApiResponse<Void>> call = apiProduct.removeOneFromCart(product_id, user_id, tipo);
        call.enqueue(new Callback<ApiResponse<Void>>() {

            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Erro ao remover 1 produto: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onFailure(new Exception("Falha na conexão: " + t.getMessage()));
            }
        });
    }

    public void removeFromCart(int product_id, int user_id, int tipo, RepositoryCallback<ResponseBody> callback) {
        Call<ResponseBody> call = apiProduct.removeFromCart(product_id, user_id, tipo);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Erro ao remover dos favoritos/carrinho: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(new Exception("Falha na conexão: " + t.getMessage()));
            }
        });
    }

    public void fetchCartProducts(int userId, int tipo, RepositoryCallback<List<Product>> callback) {
        apiProduct.getCartProducts(userId, tipo).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData()); // Retorna os produtos corretamente
                    } else {
                        callback.onSuccess(new ArrayList<>()); // Se não houver produtos, retorna lista vazia
                    }
                } else {
                    callback.onFailure(new Exception("Erro na resposta da API: Código " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar produtos: " + t.getMessage()));
            }
        });
    }

    public void verifyProductOnCart(int productId, int userId, int tipo, RepositoryCallback<Boolean> callback) {
        apiProduct.verifyProductOnCart(userId, productId, tipo).enqueue(new Callback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Integer>> call, Response<ApiResponse<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Integer> apiResponse = response.body();

                    if(apiResponse.getData() != null) {
                        int tipoProdutoApi = (int) Float.parseFloat(String.format("%d", apiResponse.getData()));

                        if (tipo == tipoProdutoApi) {
                            callback.onSuccess(true);
                        } else {
                            callback.onSuccess(false);
                        }
                    } else {
                        callback.onSuccess(false);
                    }
                } else {
                    callback.onSuccess(false);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao verificar se o produto está nos favoritos: " + t.getMessage()));
            }
        });
    }

}
