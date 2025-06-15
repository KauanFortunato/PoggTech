package com.mordekai.poggtech.domain;


import android.util.Log;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Category;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ApiProduct;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
                .enqueue(new Callback<ApiResponse<Product>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Product product = response.body().getData();
                            callback.onSuccess(product);
                        } else {
                            Log.e("API_ERROR", "Código: " + response.code() + ", Erro: " + response.errorBody());
                            callback.onFailure(new Exception("Erro ao buscar produto"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
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

    public void getProductsByCategory(String category, Boolean all, RepositoryCallback<List<Product>> callback) {
        apiProduct.getProductsByCategory(category, all)
                .enqueue(new Callback<ApiResponse<List<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Product> products = response.body().getData();
                            callback.onSuccess(products);
                        } else {
                            callback.onFailure(new Exception("Erro ao buscar produtos"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                        callback.onFailure(new Exception("Erro ao buscar produtos da categoria " + category + ": " + t.getMessage()));
                    }
                });
    }

    public void fetchUserFavOrCart(int userId, int tipo, RepositoryCallback<List<Integer>> callback) {
        apiProduct.getUserFavOrCart(userId, tipo)
                .enqueue(new Callback<ApiResponse<List<Integer>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Integer>>> call, Response<ApiResponse<List<Integer>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Integer>> apiResponse = response.body();
                            if (apiResponse.getData() != null) {
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

    public void getPopularProducts(Boolean all, int quantity, RepositoryCallback<List<Product>> callback) {
        apiProduct.getPopularProducts(all, quantity)
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

    public void getContinueBuy(int userId, RepositoryCallback<List<Product>> callback) {
        apiProduct.getContinueBuy(userId).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<Product> products = response.body().getData();
                        callback.onSuccess(products);
                    } else {
                        callback.onFailure(new Exception("Erro ao buscar produtos recomendados"));
                    }
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

    public void getProductsFavCategories(int userId, int quantity, RepositoryCallback<List<Product>> callback) {
        apiProduct.getProductsFavCategories(userId, quantity).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<Product> products = apiResponse.getData();
                        callback.onSuccess(products);
                    } else {
                        callback.onFailure(new Exception("Erro ao buscar produtos"));
                    }
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

    public void getProductsFromFavCategory(int userId, RepositoryCallback<List<Product>> callback) {
        apiProduct.getProductsFromFavCategory(userId).enqueue(new Callback<ApiResponse<List<Product>>>() {

            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<Product> products = apiResponse.getData();
                        callback.onSuccess(products);
                    } else {
                        callback.onFailure(new Exception("Erro ao buscar produtos"));
                    }
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

    public void searchProducts(String search, RepositoryCallback<List<Product>> callback) {
        String sanitizedQuery = search.replace("/", " ");

        apiProduct.searchProducts(sanitizedQuery).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onFailure(new Exception("Erro ao buscar produtos"));
                    }
                } else {
                    callback.onFailure(new Exception("Erro ao buscar produtos"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar produtos"));
            }
        });
    }

    public void getSuggestions(String query, RepositoryCallback<List<String>> callback) {
        String sanitizedQuery = query.replace("/", " ");

        Log.d("Suggestions", "Query: " + sanitizedQuery);

        Call<ApiResponse<List<String>>> call = apiProduct.getSuggestions(sanitizedQuery);
        call.enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<String>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onFailure(new Exception("Erro ao buscar sugestões"));
                    }
                } else {
                    callback.onFailure(new Exception("Erro ao buscar sugestões"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar sugestões"));
            }
        });
    }

    public void getAllCategories(RepositoryCallback<List<Category>> callback) {
        apiProduct.getAllCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Category>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onFailure(new Exception("Erro ao buscar categorias"));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar categorias"));
            }
        });
    }

    public void uploadProduct(RequestBody title, RequestBody description, RequestBody location,
                              RequestBody price, RequestBody userId, RequestBody category,
                              List<MultipartBody.Part> images, RepositoryCallback<Void> callback) {

        apiProduct.uploadProduct(title, description, location, price, userId, category, images)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            callback.onFailure(new Exception("Erro no servidor"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    public void updateProduct(int product_id,
                              RequestBody title, RequestBody description, RequestBody location, RequestBody price, RequestBody category,
                              List<RequestBody> existingImages,
                              List<MultipartBody.Part> images, RepositoryCallback<Void> callback) {

        apiProduct.updateProduct(product_id, title, description, location, price, category, existingImages, images)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            callback.onFailure(new Exception("Erro no servidor"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    public void reduceQuantity(int product_id, int quantity, RepositoryCallback<String> callback) {
        apiProduct.reduceQuantity(product_id, quantity).enqueue(new Callback<ApiResponse<Void>>() {

            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMessage());
                } else {
                    callback.onFailure(new Exception("Erro ao reduzir a quantidade"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao reduzir a quantidade"));
            }
        });
    }

    public void getProductImages(int product_id, RepositoryCallback<List<String>> callback) {
        apiProduct.getProductImages(product_id).enqueue(new Callback<ApiResponse<List<String>>>() {

            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<String>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onFailure(new Exception("Erro ao buscar imagens do produto"));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar imagens do produto"));
            }
        });
    }

    public void deleteProduct(int product_id, RepositoryCallback<String> callback) {
        apiProduct.deleteProduct(product_id).enqueue(new Callback<ApiResponse<Void>>() {

            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        callback.onSuccess("Produto deletado com sucesso");
                    } else {
                        callback.onFailure(new Exception("Erro ao deletar produto"));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao deletar produto"));
            }
        });
    }

    public void getMyProducts(int user_id, RepositoryCallback<List<Product>> callback) {
        apiProduct.getMyProducts(user_id).enqueue(new Callback<ApiResponse<List<Product>>>() {

            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onFailure(new Exception("Erro ao buscar produtos"));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar produtos"));
            }
        });
    }
}
