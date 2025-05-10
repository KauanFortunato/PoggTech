package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.CartProductAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private SharedPrefHelper sharedPrefHelper;
    private User user;
    private CartProductAdapter cartProductAdapter;
    private RecyclerView rvItemsCart;
    private ApiProduct apiProduct;
    private ProductManager productManager;
    private CartManager cartManager;
    private List<Product> productList;
    private TextView textNoCartProducts;
    private boolean isLoading = true;
    private boolean isEmpty = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        initComponents(view);

        // Recycler View
        productList = new ArrayList<>();
        cartProductAdapter = new CartProductAdapter(productList, user.getUserId());
        rvItemsCart.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvItemsCart.setNestedScrollingEnabled(false);
        rvItemsCart.setAdapter(cartProductAdapter);

        // Iniciar API e gerenciador de produtos
        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);
        cartManager = new CartManager(apiProduct);

        swipeRefreshLayout.setOnRefreshListener(this::fetchCartProducts);

        // Carregar produtos
        fetchCartProducts();

        return view;
    }

    // Tipo 0 = Carrinho
    private void fetchCartProducts () {
        cartManager.fetchCartProducts(user.getUserId(), 0, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                rvItemsCart.postDelayed(() -> {
                    shimmerLayout.setVisibility(View.GONE);
                    shimmerLayout.stopShimmer();
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
                }, 600);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos", t);
                rvItemsCart.postDelayed(() -> {
                    shimmerLayout.setVisibility(View.GONE);
                    shimmerLayout.stopShimmer();
                    swipeRefreshLayout.setRefreshing(false);
                    isLoading = false;
                    isEmpty = true;

                    rvItemsCart.setVisibility(View.GONE);
                    textNoCartProducts.setVisibility(View.VISIBLE);
                }, 600);
            }
        });
    }

    private void initComponents(View view) {
        // Componentes
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        rvItemsCart = view.findViewById(R.id.rvItemsCart);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        textNoCartProducts = view.findViewById(R.id.textNoCartProducts);

        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
    }
}
