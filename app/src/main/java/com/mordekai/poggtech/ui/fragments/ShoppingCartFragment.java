package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.CartProductAdapter;
import com.mordekai.poggtech.data.adapter.ProductAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Category;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ProductApi;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingCartFragment extends Fragment {

    private SharedPrefHelper sharedPrefHelper;
    private User user;
    private CartProductAdapter cartProductAdapter;
    private RecyclerView rvItemsCart;
    private ProductApi productApi;
    private ProductManager productManager;
    private CartManager cartManager;
    private List<Product> productList;
    private ProgressBar progressBar;
    private TextView textNoCartProducts;
    private boolean isLoading = true;
    private boolean isEmpty = false;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        initComponents(view);

        // Recycler View
        productList = new ArrayList<>();
        cartProductAdapter = new CartProductAdapter(productList);
        rvItemsCart.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvItemsCart.setNestedScrollingEnabled(false);
        rvItemsCart.setAdapter(cartProductAdapter);

        // Iniciar API e gerenciador de produtos
        productApi = RetrofitClient.getRetrofitInstance().create(ProductApi.class);
        productManager = new ProductManager(productApi);
        cartManager = new CartManager(productApi);

        swipeRefreshLayout.setOnRefreshListener(this::fetchCartProducts);

        // Carregar produtos
        progressBar.setVisibility(View.VISIBLE);
        fetchCartProducts();

        return view;
    }

    // Tipo 0 = Carrinho
    private void fetchCartProducts () {
        cartManager.fetchCartProducts(user.getUserId(), 0, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;

                productList.clear();

                if (products.isEmpty()) {
                    isEmpty = true;
                    rvItemsCart.setVisibility(View.GONE);
                    textNoCartProducts.setVisibility(View.VISIBLE);

                    Log.d("API_RESPONSE", "Nenhum produto encontrado");
                } else {
                    isEmpty = false;
                    productList.addAll(products);
                    cartProductAdapter.notifyDataSetChanged();

                    rvItemsCart.setVisibility(View.VISIBLE);
                    textNoCartProducts.setVisibility(View.GONE);
                    Log.d("API_RESPONSE", "Item 0: " + productList.get(0).getTitle());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos", t);

                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
            }
        });
    }

    private void initComponents(View view) {
        // Componentes
        progressBar = view.findViewById(R.id.progressBarItems);
        rvItemsCart = view.findViewById(R.id.rvItemsCart);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        textNoCartProducts = view.findViewById(R.id.textNoCartProducts);
    }
}
