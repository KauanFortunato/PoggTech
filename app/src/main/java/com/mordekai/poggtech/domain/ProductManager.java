package com.mordekai.poggtech.domain;


import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ProductApi;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductManager {
    private final ProductApi productApi;

    public ProductManager(ProductApi productApi) {
        this.productApi = productApi;
    }

    public void fetchProductById(int product_id, RepositoryCallback<Product> callback) {
        productApi.getProductById(product_id)
                .enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            Product product = response.body();
                            callback.onSuccess(product);
                        } else {
                            callback.onFailure(new Exception("Erro ao buscar produto"));
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        callback.onFailure(new Exception("Erro ao buscar produto"));
                    }
                });
    }

    public void fetchAllProduct(RepositoryCallback<List<Product>> callback) {
        productApi.getAllProducts()
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
        productApi.getProductsByCategory(category)
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
}
