package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.CategoryAdapter;
import com.mordekai.poggtech.data.adapter.ProductAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Category;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiInteraction;
import com.mordekai.poggtech.data.remote.ApiService;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.InteractionManager;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private SharedPrefHelper sharedPrefHelper;
    private LinearLayout containerProductsHome;
    private TextView tudoCategorie;
    private ProgressBar progressBar;
    private RecyclerView rvProducts, rvConsolas, rvJogos, rvAcessory, rvContinueBuySkeleton, rvContinueBuy;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter, consolasAdapter, jogosAdapter, acessoryAdapter;
    private ApiService apiService;
    private ApiProduct apiProduct;
    private ApiInteraction apiInteraction;
    private List<Product> productList = new ArrayList<>();
    private List<Product> productForYouList = new ArrayList<>();
    private ProductManager productManager;
    private InteractionManager interactionManager;
    private List<Category> categoryList = new ArrayList<>();
    private User user;
    private int loadingCount = 0;
    private List<Integer> favoriteIds = new ArrayList<>();
    private ShimmerFrameLayout shimmerContinueBuy;

    @Override
    public void onProductClick(Product product) {
        Bundle bundle = new Bundle();
        bundle.putInt("productId", product.getProduct_id());

        ProductDetailsFragment fragment = new ProductDetailsFragment();
        fragment.setArguments(bundle);

        interactionManager.userInteraction(product.getProduct_id(), user.getUserId(), "view",new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("API_RESPONSE", "Interaction result: " + result);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Error in userInteraction", t);
            }
        });

        getParentFragmentManager().beginTransaction()
                .replace(R.id.containerFrame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        startComponentes(view);

//        setupSkeletonLoader();

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

        categoryAdapter = new CategoryAdapter(categoryList);
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        rvProducts = view.findViewById(R.id.rvForYou);
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvProducts.setNestedScrollingEnabled(false);

        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);
        productAdapter = new ProductAdapter(productList, user.getUserId(), this);
        rvProducts.setAdapter(productAdapter);

        apiInteraction = RetrofitClient.getRetrofitInstance().create(ApiInteraction.class);
        interactionManager = new InteractionManager(apiInteraction);

        fetchAllProducts();
        return view;
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
        tudoCategorie = view.findViewById(R.id.tudoCategorie);
        rvContinueBuySkeleton = view.findViewById(R.id.rvContinueBuySkeleton);
        rvContinueBuy = view.findViewById(R.id.rvContinueBuy);
        shimmerContinueBuy = view.findViewById(R.id.shimmerContinueBuy);

        shimmerContinueBuy.startShimmer();
        shimmerContinueBuy.setVisibility(View.VISIBLE);
        rvContinueBuy.setVisibility(View.GONE);

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.btnBackHeader).setVisibility(View.GONE);


        tudoCategorie.setTypeface(tudoCategorie.getTypeface(), tudoCategorie.getTypeface().BOLD);
        tudoCategorie.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    }

    private void setupSkeletonLoader() {
        List<Product> fakeList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fakeList.add(new Product());
        }

        ProductAdapter skeletonAdapter = new ProductAdapter(fakeList, user.getUserId(), this);
        rvContinueBuySkeleton.setAdapter(skeletonAdapter);
    }

}
