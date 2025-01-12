package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private SharedPrefHelper sharedPrefHelper;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        rvConsolas = view.findViewById(R.id.rvConsolas);
        rvJogos = view.findViewById(R.id.rvJogos);
        rvAcessory = view.findViewById(R.id.rvAcessory);

        consolasAdapter = new ProductAdapter(new ArrayList<>());
        jogosAdapter = new ProductAdapter(new ArrayList<>());
        acessoryAdapter = new ProductAdapter(new ArrayList<>());

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
        productAdapter = new ProductAdapter(productList);
        rvProducts.setAdapter(productAdapter);
        fetchAllProducts();
        fetchCategoryProducts();

        return view;
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
                Toast.makeText(getContext(), "Erro ao buscar categorias", Toast.LENGTH_SHORT).show();
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
                    Log.d("API_RESPONSE", "Item 0: " + productList.get(0).getTitle());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos", t);
            }
        });
    }

    private void fetchCategoryProducts() {
        productManager.fetchProductsByCategory("Consolas", new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                consolasAdapter = new ProductAdapter(products);
                rvConsolas.setAdapter(consolasAdapter);
                consolasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos da categoria", t);
            }
        });

        productManager.fetchProductsByCategory("Jogos", new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                jogosAdapter = new ProductAdapter(products);
                rvJogos.setAdapter(jogosAdapter);
                jogosAdapter.notifyDataSetChanged();
                }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos da categoria", t);
            }
        });

        productManager.fetchProductsByCategory("Acessórios", new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                acessoryAdapter = new ProductAdapter(products);
                rvAcessory.setAdapter(acessoryAdapter);
                acessoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos da categoria", t);
            }
        });

    }

    private void resetPassword() {

    }
}
