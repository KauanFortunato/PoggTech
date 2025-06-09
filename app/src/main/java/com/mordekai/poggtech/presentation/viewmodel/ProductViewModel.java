package com.mordekai.poggtech.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Category;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.domain.ProductManager;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProductViewModel extends ViewModel {

    private final ProductManager productManager;

    // LiveData para resultados
    private final MutableLiveData<Product> productLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> allProductsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> productsByCategoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> productsSavedLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> popularProductsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> recommendedProductsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> productsFavCategoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> productsFromFavCategoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> searchedProductsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> suggestionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> productImagesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> myProductsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();

    // LiveData para status e erros
    private final MutableLiveData<String> statusLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public ProductViewModel(ProductManager productManager) {
        this.productManager = productManager;
    }

    // Getters para os LiveData
    public LiveData<Product> getProductLiveData() {
        return productLiveData;
    }

    public LiveData<List<Product>> getAllProductsLiveData() {
        return allProductsLiveData;
    }

    public LiveData<List<Product>> getProductsByCategoryLiveData() {
        return productsByCategoryLiveData;
    }

    public LiveData<List<Integer>> getProductsSavedLiveData() {
        return productsSavedLiveData;
    }

    public LiveData<List<Product>> getPopularProductsLiveData() {
        return popularProductsLiveData;
    }

    public LiveData<List<Product>> getRecommendedProductsLiveData() {
        return recommendedProductsLiveData;
    }

    public LiveData<List<Product>> getProductsFavCategoriesLiveData() {
        return productsFavCategoriesLiveData;
    }

    public LiveData<List<Product>> getProductsFromFavCategoryLiveData() {
        return productsFromFavCategoryLiveData;
    }

    public LiveData<List<Product>> getSearchedProductsLiveData() {
        return searchedProductsLiveData;
    }

    public LiveData<List<String>> getSuggestionsLiveData() {
        return suggestionsLiveData;
    }

    public LiveData<List<String>> getProductImagesLiveData() {
        return productImagesLiveData;
    }

    public LiveData<List<Product>> getMyProductsLiveData() {
        return myProductsLiveData;
    }

    public LiveData<List<Category>> getCategoriesLiveData() {
        return categoriesLiveData;
    }

    public LiveData<String> getStatusLiveData() {
        return statusLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // Chamadas para ProductManager

    public void fetchProductById(int product_id) {
        productManager.fetchProductById(product_id, new RepositoryCallback<Product>() {
            @Override
            public void onSuccess(Product data) {
                productLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void fetchAllProduct() {
        productManager.fetchAllProduct(new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                allProductsLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void getProductsByCategory(String category, Boolean all) {
        productManager.getProductsByCategory(category, all, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                productsByCategoryLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void verifProductsSaved(int userId, int tipo) {
        productManager.verifProductsSaved(userId, tipo, new RepositoryCallback<List<Integer>>() {
            @Override
            public void onSuccess(List<Integer> data) {
                productsSavedLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void getPopularProducts(Boolean all, int quantity) {
        productManager.getPopularProducts(all, quantity, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                popularProductsLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void getRecommendedProducts(int userId) {
        productManager.getRecommendedProducts(userId, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                recommendedProductsLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void getProductsFavCategories(int userId, int quantity) {
        productManager.getProductsFavCategories(userId, quantity, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                productsFavCategoriesLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void getProductsFromFavCategory(int userId) {
        productManager.getProductsFromFavCategory(userId, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                productsFromFavCategoryLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void searchProducts(String search) {
        productManager.searchProducts(search, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                searchedProductsLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void getSuggestions(String query) {
        productManager.getSuggestions(query, new RepositoryCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                suggestionsLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void uploadProduct(RequestBody title, RequestBody description, RequestBody location,
                              RequestBody price, RequestBody userId, RequestBody category,
                              List<MultipartBody.Part> images) {
        productManager.uploadProduct(title, description, location, price, userId, category, images, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                statusLiveData.postValue("Produto criado com sucesso");
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void getProductImages(int product_id) {
        productManager.getProductImages(product_id, new RepositoryCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                productImagesLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void deleteProduct(int product_id) {
        productManager.deleteProduct(product_id, new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String data) {
                statusLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void getMyProducts(int user_id) {
        productManager.getMyProducts(user_id, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                myProductsLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }

    public void getAllCategories() {
        productManager.getAllCategories(new RepositoryCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> data) {
                categoriesLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }
}

