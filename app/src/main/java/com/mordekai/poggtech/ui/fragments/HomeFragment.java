package com.mordekai.poggtech.ui.fragments;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.CategoryAdapter;
import com.mordekai.poggtech.data.adapter.ProductAdapter;
import com.mordekai.poggtech.data.adapter.ProductContinueAdapter;
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
import java.util.List;

public class HomeFragment extends Fragment
        implements ProductAdapter.OnProductClickListener,
                    ProductAdapter.OnFavoritesChangedListener,
                    ProductContinueAdapter.OnProductContinueClickListener {

    private SharedPrefHelper sharedPrefHelper;
    private LinearLayout containerProductsHome;
    private TextView tudoCategorie;
    private ProgressBar progressBar;
    private RecyclerView rvForYou, rvConsolas, rvPopular, rvAccessory, rvContinueBuySkeleton, rvContinueBuy;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter forYouAdapter, consolasAdapter, popularAdapter, accessoryAdapter;
    private ProductContinueAdapter productContinueAdapter;
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
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                )
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

        setupSkeletonLoader();

        progressBar.setVisibility(View.VISIBLE);
        containerProductsHome.setVisibility(View.GONE);

        consolasAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.card_product_match_parent,this, this);
        popularAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.card_product_match_parent, this, this);
        accessoryAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.card_product_match_parent, this, this);
        productContinueAdapter = new ProductContinueAdapter(new ArrayList<>(), user.getUserId(), this, this);

        rvConsolas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopular.setLayoutManager(new GridLayoutManager(getContext(),2));
        rvAccessory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        rvConsolas.setAdapter(consolasAdapter);
        rvPopular.setAdapter(popularAdapter);
        rvAccessory.setAdapter(accessoryAdapter);

        categoryAdapter = new CategoryAdapter(categoryList);
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        rvForYou = view.findViewById(R.id.rvForYou);
        rvForYou.setLayoutManager(new GridLayoutManager(getContext(),2));
        rvForYou.setNestedScrollingEnabled(false);

        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);
        forYouAdapter = new ProductAdapter(productList, user.getUserId(), R.layout.card_product_match_parent, this, this);
        rvForYou.setAdapter(forYouAdapter);

        apiInteraction = RetrofitClient.getRetrofitInstance().create(ApiInteraction.class);
        interactionManager = new InteractionManager(apiInteraction);
        
        getContinueBuy();
        getPopular();
        getForYou();

        return view;
    }

    private void getContinueBuy() {
        new android.os.Handler().postDelayed(() -> {
            productManager.getRecommendedProducts(user.getUserId(), new RepositoryCallback<List<Product>>() {
                @Override
                public void onSuccess(List<Product> result) {
                    rvContinueBuySkeleton.setVisibility(View.GONE);

                    if(result.size() >= 3) {
                        rvContinueBuy.setVisibility(View.VISIBLE);

                        productContinueAdapter.updateProducts(result);
                        rvContinueBuy.setAdapter(productContinueAdapter);
                    }

                    checkIfLoadingFinished();
                }

                @Override
                public void onFailure(Throwable t) {
                    if (getContext() != null) {
                        Log.e("API_RESPONSE", "Erro ao buscar produtos recomendados", t);
                        checkIfLoadingFinished();
                    }
                }
            });
        }, 400);
    }

    private void getForYou() {
        productManager.getProductsFavCategory(user.getUserId(), 4, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                productForYouList.clear();
                productForYouList.addAll(result);

                // Buscar os favoritos do utilizador antes de atualizar os adapters
                productManager.fetchUserFavOrCart(user.getUserId(), 1, new RepositoryCallback<List<Integer>>() {
                    @Override
                    public void onSuccess(List<Integer> favorites) {
                        favoriteIds.clear();
                        favoriteIds.addAll(favorites);

                        updateAllAdapters();
                        forYouAdapter.updateProducts(productForYouList);
                        checkIfLoadingFinished();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("API_RESPONSE", "Erro ao buscar produtos favoritos", t);
                        checkIfLoadingFinished();
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos", t);
                checkIfLoadingFinished();
            }
        });
    }

    private void getPopular() {
        productManager.getPopularProducts(new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                popularAdapter.updateProducts(result);
                checkIfLoadingFinished();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos", t);
                checkIfLoadingFinished();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAllAdapters() {
        forYouAdapter.setFavoriteIds(favoriteIds);
        consolasAdapter.setFavoriteIds(favoriteIds);
        popularAdapter.setFavoriteIds(favoriteIds);
        accessoryAdapter.setFavoriteIds(favoriteIds);

        forYouAdapter.notifyDataSetChanged();
        consolasAdapter.notifyDataSetChanged();
        popularAdapter.notifyDataSetChanged();
        accessoryAdapter.notifyDataSetChanged();
        productContinueAdapter.notifyDataSetChanged();
    }

    private void checkIfLoadingFinished() {
        loadingCount++;

        if (loadingCount >= 3) {
            progressBar.setVisibility(View.GONE);
            containerProductsHome.setVisibility(View.VISIBLE);
        }
    }

    private void startComponentes(View view) {
        rvConsolas = view.findViewById(R.id.rvConsolas);
        rvPopular = view.findViewById(R.id.rvPopular);
        rvAccessory = view.findViewById(R.id.rvAcessory);
        rvContinueBuy = view.findViewById(R.id.rvContinueBuy);

        rvContinueBuySkeleton = view.findViewById(R.id.rvContinueBuySkeleton);
        containerProductsHome = view.findViewById(R.id.containerProductsHome);
        progressBar = view.findViewById(R.id.progressBar);
        tudoCategorie = view.findViewById(R.id.tudoCategorie);
        shimmerContinueBuy = view.findViewById(R.id.shimmerContinueBuy);
        shimmerContinueBuy.startShimmer();
        shimmerContinueBuy.setVisibility(View.VISIBLE);
        rvContinueBuy.setVisibility(View.GONE);

        HeaderFragment.HeaderListener listener = (HeaderFragment.HeaderListener) getActivity();

        if (listener != null) {
            listener.hideBackButton();
        }

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);

        tudoCategorie.setTypeface(tudoCategorie.getTypeface(), tudoCategorie.getTypeface().BOLD);
        tudoCategorie.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    }

    private void setupSkeletonLoader() {
        List<Product> fakeList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fakeList.add(new Product());
        }

        ProductContinueAdapter skeletonAdapter = new ProductContinueAdapter(fakeList, user.getUserId(), this, this);
        rvContinueBuySkeleton.setAdapter(skeletonAdapter);
    }

    @Override
    public void onFavoritesChanged() {
        updateAllAdapters();
    }

}
