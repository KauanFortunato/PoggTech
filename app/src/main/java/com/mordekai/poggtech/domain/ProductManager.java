package com.mordekai.poggtech.domain;


import android.util.Log;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ApiProduct;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductManager {
    private final ApiProduct apiProduct;

    public ProductManager(ApiProduct apiProduct) {
        this.apiProduct = apiProduct;
    }

    public void fetchProductById(int product_id, RepositoryCallback<Product> callback) {
        apiProduct.getProductById(product_id)
                .enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            Product product = response.body();
                            callback.onSuccess(product);
                        } else {
                            Log.e("API_ERROR", "Código: " + response.code() + ", Erro: " + response.errorBody());
                            callback.onFailure(new Exception("Erro ao buscar produto"));
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Log.e("API_ERROR", "Erro na requisição: " + t.getMessage(), t);
                        callback.onFailure(new Exception("Erro ao buscar produto: " + t.getMessage()));
                    }

                });
    }

    public void fetchAllProduct(RepositoryCallback<List<Product>> callback) {
        apiProduct.getAllProducts()
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Product> products = response.body();
                            callback.onSuccess(products);
                        } else {
                            callback.onFailure(new Exception("Erro ao buscar produtos"));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {
                        callback.onFailure(new Exception("Erro ao buscar produtos"));
                    }
                });
    }

    public void fetchProductsByCategory(String category, RepositoryCallback<List<Product>> callback) {
        apiProduct.getProductsByCategory(category)
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            List<Product> products = response.body();
                            callback.onSuccess(products);
                        } else {
                            callback.onFailure(new Exception("Erro ao buscar produtos"));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {
                        callback.onFailure(new Exception("Erro ao buscar produtos da categoria " + category + ": " + t.getMessage()));
                    }
                });
    }

    public void fetchUserFavOrCart(int userId, int tipo, RepositoryCallback<List<Integer>> callback) {
        apiProduct.getUserFavOrCart(userId, tipo)
                .enqueue(new Callback<ApiResponse<List<Integer>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Integer>>> call, Response<ApiResponse<List<Integer>>> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Integer>> apiResponse = response.body();
                            if(apiResponse.getData() != null) {
                                List<Integer> products = apiResponse.getData();
                                callback.onSuccess(products);
                            } else {
                                callback.onSuccess(new ArrayList<>());
                            }
                        } else {
                            callback.onFailure(new Exception("Erro ao buscar produtos"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Integer>>> call, Throwable t) {
                        callback.onFailure(new Exception("Erro ao buscar produtos: " + t.getMessage()));
                    }
                });
    }

    public void getPopularProducts(RepositoryCallback<List<Product>> callback) {
        apiProduct.getPopularProducts()
                .enqueue(new Callback<ApiResponse<List<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Product> products = response.body().getData();
                            callback.onSuccess(products);
                        } else {
                            callback.onFailure(new Exception("Erro ao buscar produtos populares"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                        callback.onFailure(new Exception("Erro ao buscar produtos"));
                    }
                });
    }

    public void getRecommendedProducts(int userId, RepositoryCallback<List<Product>> callback) {
        apiProduct.getRecommendedProducts(userId).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getData();
                    callback.onSuccess(products);
                } else {
                    callback.onFailure(new Exception("Erro ao buscar produtos recomendados"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar produtos"));
            }
        });
    }

    public void getProductsFavCategory(int userId, int quantity, RepositoryCallback<List<Product>> callback) {
        apiProduct.getProductsFavCategory(userId, quantity).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getData();
                    callback.onSuccess(products);
                } else {
                    callback.onFailure(new Exception("Erro ao buscar produtos pela categoria"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar produtos"));
            }
        });
    }
}
