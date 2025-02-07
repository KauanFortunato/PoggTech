package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.CartProductAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ProductApi;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.ProductManager;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartFragment extends Fragment {

    CartProductAdapter cartProductAdapter;
    RecyclerView rvItemsCart;
    private ProductApi productApi;
    private ProductManager productManager;
    private List<Product> productList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        rvItemsCart = view.findViewById(R.id.rvItemsCart);

        cartProductAdapter = new CartProductAdapter(new ArrayList<>());

        rvItemsCart.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvItemsCart.setNestedScrollingEnabled(false);

        productApi = RetrofitClient.getRetrofitInstance().create(ProductApi.class);
        productManager = new ProductManager(productApi);
        cartProductAdapter = new CartProductAdapter(productList);
        rvItemsCart.setAdapter(cartProductAdapter);
        fetchAllProducts();
        return view;
    }

    private void fetchCartProducts () {

    }

    private void fetchAllProducts() {
        productManager.fetchAllProduct(new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                if (result.isEmpty()) {
                    Log.d("API_RESPONSE", "Nenhum produto encontrado");
                } else {
                    productList.clear();
                    productList.addAll(result);
                    cartProductAdapter.notifyDataSetChanged();
                    Log.d("API_RESPONSE", "Item 0: " + productList.get(0).getTitle());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos", t);
            }
        });
    }
}
