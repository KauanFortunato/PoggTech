package com.mordekai.poggtech.domain;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ProductApi;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartManager {
    private final ProductApi productApi;

    public CartManager(ProductApi productApi) {
        this.productApi = productApi;
    }

    public void addToCart(int product_id, int user_id, int tipo, RepositoryCallback<ResponseBody> callback) {
        Call<ResponseBody> call = productApi.addToCart(product_id, user_id, tipo);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Erro ao adicionar ao carrinho: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(new Exception("Falha na conexão: " + t.getMessage()));
            }
        });
    }


    public void fetchCartProducts(int userId, int tipo, RepositoryCallback<List<Product>> callback) {
        productApi.getCartProducts(userId, tipo)
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
                        callback.onFailure(new Exception("Erro ao buscar produtos: " + t.getMessage()));
                    }
                });
    }
}
