package com.mordekai.poggtech.presentation.viewmodel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.domain.CartManager;

import java.util.List;

import okhttp3.ResponseBody;

public class CartViewModel extends ViewModel {

    private final CartManager cartManager;

    // LiveData para os produtos do carrinho
    private final MutableLiveData<List<Product>> cartProducts = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CartViewModel(CartManager cartManager) {
        this.cartManager = cartManager;
    }

    public LiveData<List<Product>> getCartProducts() {
        return cartProducts;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchCartProducts(int userId, int tipo) {
        isLoading.setValue(true);
        cartManager.fetchCartProducts(userId, tipo, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                isLoading.postValue(false);
                cartProducts.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void addToCart(int product_id, int user_id, int quantity) {
        isLoading.setValue(true);
        cartManager.addToCart(product_id, user_id, quantity, new RepositoryCallback<ApiResponse<Void>>() {
            @Override
            public void onSuccess(ApiResponse<Void> data) {
                isLoading.postValue(false);
                // Se quiser atualizar produtos automaticamente, chame fetchCartProducts de novo
            }

            @Override
            public void onFailure(Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void removeOneFromCart(int product_id, int user_id, int tipo) {
        isLoading.setValue(true);
        cartManager.removeOneFromCart(product_id, user_id, tipo, new RepositoryCallback<ApiResponse<Void>>() {
            @Override
            public void onSuccess(ApiResponse<Void> data) {
                isLoading.postValue(false);
                // Atualizar lista se necessário
            }

            @Override
            public void onFailure(Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void removeFromCart(int product_id, int user_id, int tipo) {
        isLoading.setValue(true);
        cartManager.removeFromCart(product_id, user_id, tipo, new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                isLoading.postValue(false);
                // Atualizar lista se necessário
            }

            @Override
            public void onFailure(Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void verifyProductOnCart(int productId, int userId, int tipo, MutableLiveData<Boolean> result) {
        cartManager.verifyProductOnCart(productId, userId, tipo, new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean exists) {
                result.postValue(exists);
            }

            @Override
            public void onFailure(Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}
