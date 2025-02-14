package com.mordekai.poggtech.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.CategoryAdapter;
import com.mordekai.poggtech.data.adapter.ProductAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Category;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiService;
import com.mordekai.poggtech.data.remote.ProductApi;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private SharedPrefHelper sharedPrefHelper;
    private LinearLayout containerProductsHome;
    private ProgressBar progressBar;
    private RecyclerView rvCategories, rvProducts, rvConsolas, rvJogos, rvAcessory;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter, consolasAdapter, jogosAdapter, acessoryAdapter;
    private FirebaseAuth auth;
    private ApiService apiService;
    private ProductApi productApi;
    private List<Product> productList = new ArrayList<>();
    private ProductManager productManager;
    private List<Category> categoryList = new ArrayList<>();
    private User user;
    private int loadingCount = 0;
    private List<Integer> favoriteIds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        startComponentes(view);
        progressBar.setVisibility(View.VISIBLE);
        containerProductsHome.setVisibility(View.GONE);

        consolasAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), this);
        jogosAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), this);
        acessoryAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), this);

        rvConsolas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvJogos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvAcessory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        rvConsolas.setAdapter(consolasAdapter);
        rvJogos.setAdapter(jogosAdapter);
        rvAcessory.setAdapter(acessoryAdapter);

        rvCategories = view.findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        categoryAdapter = new CategoryAdapter(categoryList);
        rvCategories.setAdapter(categoryAdapter);
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        fetchCategories();

        rvProducts = view.findViewById(R.id.rvForYou);
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvProducts.setNestedScrollingEnabled(false);

        productApi = RetrofitClient.getRetrofitInstance().create(ProductApi.class);
        productManager = new ProductManager(productApi);
        productAdapter = new ProductAdapter(productList, user.getUserId(), this);
        rvProducts.setAdapter(productAdapter);
        applyRecyclerViewAnimation(rvProducts);

        fetchAllProducts();
        return view;
    }

    private void applyRecyclerViewAnimation(RecyclerView recyclerView) {
        Context context = requireContext();
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide);
        recyclerView.setLayoutAnimation(animationController);
    }

    private void fetchCategories() {
        apiService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();

                    Log.d("API_RESPONSE", "Categorias recebidas: " + response.body().toString());
                } else {
                    Toast.makeText(getContext(), "Erro ao carregar categorias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Erro ao buscar categorias", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                    productAdapter.notifyDataSetChanged();

                    rvProducts.scheduleLayoutAnimation();

                    Log.d("API_RESPONSE", "Item 0: " + productList.get(0).getTitle());

                    Map<String, List<Product>> categorizedProducts = new HashMap<>();

                    for (Product product : productList) {
                        String category = product.getCategory();
                        if (!categorizedProducts.containsKey(category)) {
                            categorizedProducts.put(category, new ArrayList<>());
                        }
                        categorizedProducts.get(category).add(product);
                    }

                    // Pede os favoritos do utilizador
                    productManager.fetchUserFavOrCart(user.getUserId(), 1, new RepositoryCallback<List<Integer>>() {
                        @Override
                        public void onSuccess(List<Integer> favorites) {
                            favoriteIds.clear();
                            favoriteIds.addAll(favorites);

                            // Garante que todos os adaptadores usam a mesma lista de favoritos
                            updateAllAdapters();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            if (getContext() != null) {
                                Log.e("API_RESPONSE", "Erro ao buscar produtos favoritos", t);
                            }
                        }
                    });

                    // Atualiza os adapters das RecyclerViews de categorias
                    if (categorizedProducts.containsKey("Consolas")) {
                        consolasAdapter.updateProducts(categorizedProducts.get("Consolas"));
                    }
                    if (categorizedProducts.containsKey("Jogos")) {
                        jogosAdapter.updateProducts(categorizedProducts.get("Jogos"));
                    }
                    if (categorizedProducts.containsKey("Acessórios")) {
                        acessoryAdapter.updateProducts(categorizedProducts.get("Acessórios"));
                    }

                    checkIfLoadingFinished();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (getContext() != null) {
                    Log.e("API_RESPONSE", "Erro ao buscar produtos", t);
                    checkIfLoadingFinished();
                }
            }
        });
    }

    public void updateAllAdapters() {
        productAdapter.setFavoriteIds(favoriteIds);
        consolasAdapter.setFavoriteIds(favoriteIds);
        jogosAdapter.setFavoriteIds(favoriteIds);
        acessoryAdapter.setFavoriteIds(favoriteIds);
    }

    private void checkIfLoadingFinished() {
        loadingCount++;

        if (loadingCount >= 1) {
            progressBar.setVisibility(View.GONE);
            containerProductsHome.setVisibility(View.VISIBLE);
        }
    }

    private void startComponentes(View view) {
        rvConsolas = view.findViewById(R.id.rvConsolas);
        rvJogos = view.findViewById(R.id.rvJogos);
        rvAcessory = view.findViewById(R.id.rvAcessory);
        containerProductsHome = view.findViewById(R.id.containerProductsHome);
        progressBar = view.findViewById(R.id.progressBar);
    }
}
