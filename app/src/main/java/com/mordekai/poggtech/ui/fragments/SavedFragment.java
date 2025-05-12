package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.SavedProductAdapter;
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

public class SavedFragment extends Fragment {
    private SharedPrefHelper sharedPrefHelper;
    private User user;
    private SavedProductAdapter savedProductAdapter;
    private RecyclerView rvItemsSaved;
    private ApiProduct apiProduct;
    private ProductManager productManager;
    private CartManager cartManager;
    private List<Product> productList;
    private ShimmerFrameLayout shimmerLayout;
    private TextView textNoFavProducts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoading = true;
    private boolean isEmpty = true;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        initComponents(view);

        // Recycler View
        productList = new ArrayList<>();
        savedProductAdapter = new SavedProductAdapter(productList, user.getUserId());
        rvItemsSaved.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvItemsSaved.setNestedScrollingEnabled(false);
        rvItemsSaved.setAdapter(savedProductAdapter);

        // Iniciar API e gerenciador de produtos
        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);
        cartManager = new CartManager(apiProduct);

        swipeRefreshLayout.setOnRefreshListener(this::fetchFavProducts);

        // Carregar produtos
        fetchFavProducts();

        return view;
    }

    private void fetchFavProducts() {
        cartManager.fetchCartProducts(user.getUserId(), 1, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                rvItemsSaved.postDelayed(() -> {
                    shimmerLayout.setVisibility(View.GONE);
                    shimmerLayout.startShimmer();
                    swipeRefreshLayout.setRefreshing(false);
                    isLoading = false;

                    productList.clear();

                    if (products.isEmpty()) {
                        isEmpty = true;
                        rvItemsSaved.setVisibility(View.GONE);
                        textNoFavProducts.setVisibility(View.VISIBLE);
                        Log.d("API_RESPONSE", "Nenhum produto encontrado");
                    } else {
                        isEmpty = false;
                        productList.addAll(products);
                        savedProductAdapter.notifyDataSetChanged();

                        rvItemsSaved.setVisibility(View.VISIBLE);
                        textNoFavProducts.setVisibility(View.GONE);
                        Log.d("API_RESPONSE", "Item 0: " + productList.get(0).getTitle());
                    }
                }, 600);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos", t);
                rvItemsSaved.postDelayed(() -> {
                    shimmerLayout.setVisibility(View.GONE);
                    shimmerLayout.startShimmer();
                    swipeRefreshLayout.setRefreshing(false);
                    isLoading = false;
                    isEmpty = true;

                    rvItemsSaved.setVisibility(View.GONE);
                    textNoFavProducts.setVisibility(View.VISIBLE);
                }, 600);
            }
        });
    }

    private void initComponents(View view) {
        // Componentes
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        rvItemsSaved = view.findViewById(R.id.rvItemsSaved);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        textNoFavProducts = view.findViewById(R.id.textNoSaveProducts);

        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
    }
}
