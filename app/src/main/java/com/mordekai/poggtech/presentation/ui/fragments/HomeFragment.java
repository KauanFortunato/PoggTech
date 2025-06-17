package com.mordekai.poggtech.presentation.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.ProductLoader;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment
        implements ProductAdapter.OnProductClickListener,
        ProductAdapter.OnSavedChangedListener,
        ProductContinueAdapter.OnProductContinueClickListener,
        CategoryAdapter.OnCategoryClickListerner {

    private SharedPrefHelper sharedPrefHelper;
    private LinearLayout containerProductsHome, containerCategorias;
    private LinearLayout popularContainer, forYouContainer, maybeYouLikeContainer, promoProductsContainer;
    private RecyclerView rvForYou, rvPopular, rvContinueBuySkeleton, rvContinueBuy, rvMaybeYouLike, rvCategories;
    private ProductAdapter forYouAdapter, popularAdapter, accessoryAdapter, maybeYouLikeAdapter, promoAdapter;
    private CategoryAdapter categoryAdapter;
    private ProductContinueAdapter productContinueAdapter;
    private ApiService apiService;
    private ApiProduct apiProduct;
    private ApiInteraction apiInteraction;
    private List<Product> productList = new ArrayList<>();
    private List<Product> productForYouList = new ArrayList<>();
    private ProductManager productManager;
    private InteractionManager interactionManager;
    private User user;
    private int loadingCount = 0;
    private List<Integer> favoriteIds = new ArrayList<>();
    private ShimmerFrameLayout shimmerCategories, shimmerForYouSkeleton;
    HorizontalScrollView horizontalScrollViewCategories;
    View fakeScrollbar, fakeScrollbarTrack;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final Set<Integer> loadedProductIds = new HashSet<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        Log.d("HomeFragment", "Usuário: " + user.getUserId());

        if (user == null) {
            Log.e("HomeFragment", "ERRO: Usuário está null! Verifica se o login foi completado.");
            return inflater.inflate(R.layout.fragment_home, container, false); // ou um layout de erro
        }

        startComponentes(view);

        hideContainers();

        setupSkeletonLoader();

        containerProductsHome.setVisibility(View.VISIBLE);

        popularAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.item_product_match_parent, this, this);
        accessoryAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.item_product_match_parent, this, this);
        maybeYouLikeAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.item_product, this, this);
        productContinueAdapter = new ProductContinueAdapter(new ArrayList<>(), user.getUserId(), this, this);
        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), getContext(), this, R.layout.item_category);
        promoAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.item_product, this, this);

        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        rvPopular.setLayoutManager(new GridLayoutManager(getContext(),2));
        rvPopular.setAdapter(popularAdapter);

        rvMaybeYouLike.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvMaybeYouLike.setNestedScrollingEnabled(false);

        rvMaybeYouLike.setAdapter(maybeYouLikeAdapter);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        rvForYou = view.findViewById(R.id.rvForYou);
        rvForYou.setLayoutManager(new GridLayoutManager(getContext(),2));
        rvForYou.setNestedScrollingEnabled(false);

        RecyclerView rvPromoProducts = view.findViewById(R.id.rvPromoProducts);
        rvPromoProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPromoProducts.setAdapter(promoAdapter);

        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);
        forYouAdapter = new ProductAdapter(productForYouList, user.getUserId(), R.layout.item_product_match_parent, this, this);
        rvForYou.setAdapter(forYouAdapter);

        apiInteraction = RetrofitClient.getRetrofitInstance().create(ApiInteraction.class);
        interactionManager = new InteractionManager(apiInteraction);

        loadHomeData(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isVisible()) return;

        HeaderFragment.HeaderListener listener = (HeaderFragment.HeaderListener) getActivity();
        if (listener != null) {
            listener.hideBackButton();
        }

        View root = getActivity().findViewById(android.R.id.content);
        if (root != null) {
            root.post(() -> {
                View headerContainer = getActivity().findViewById(R.id.headerContainer);
                if (headerContainer != null) {
                    headerContainer.setVisibility(View.VISIBLE);
                }

            });
        }
    }

    private void getForYou() {
        productManager.getProductsFavCategories(user.getUserId(), 12, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                List<Product> filteredResult = filterNewProducts(result);
                List<Product> limited = limitDisplay(filteredResult, 6);
                productForYouList.clear();
                productForYouList.addAll(limited);

                productManager.fetchUserFavOrCart(user.getUserId(), 1, new RepositoryCallback<List<Integer>>() {
                    @Override
                    public void onSuccess(List<Integer> favorites) {
                        favoriteIds.clear();
                        favoriteIds.addAll(favorites);

                        updateAllAdapters();
                        forYouAdapter.updateProducts(limited);
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

    private void getContinueBuy(View view) {
        new android.os.Handler().postDelayed(() -> {
            productManager.getContinueBuy(user.getUserId(), new RepositoryCallback<List<Product>>() {
                @Override
                public void onSuccess(List<Product> result) {
                    rvContinueBuySkeleton.setVisibility(View.GONE);

                    if(result.size() >= 3) {
                        rvContinueBuy.setVisibility(View.VISIBLE);

                        productContinueAdapter.updateProducts(result);
                        rvContinueBuy.setAdapter(productContinueAdapter);
                    } else {
                        view.findViewById(R.id.containerContinueBuy).setVisibility(View.GONE);
                    }

                    checkIfLoadingFinished();
                }

                @Override
                public void onFailure(Throwable t) {
                    if (getContext() != null) {
                        Log.e("API_RESPONSE", "Erro ao buscar produtos recomendados", t);
                    }
                    view.findViewById(R.id.containerContinueBuy).setVisibility(View.GONE);
                    checkIfLoadingFinished();
                }
            });
        }, 200);
    }

    private void getPopular() {
        productManager.getPopularProducts(false, 12, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                List<Product> filteredResult = filterNewProducts(result);
                List<Product> limited = limitDisplay(filteredResult, 4);

                popularAdapter.updateProducts(limited);
                checkIfLoadingFinished();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos", t);
                checkIfLoadingFinished();
            }
        });
    }

    private void maybeYouLike() {
        productManager.getProductsFromFavCategory(user.getUserId(), new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                if (result == null || result.isEmpty()) {
                    checkIfLoadingFinished();
                    return;
                }
                List<Product> filteredResult = filterNewProducts(result);
                List<Product> limited = limitDisplay(filteredResult, 6);

                maybeYouLikeAdapter.updateProducts(limited);
                checkIfLoadingFinished();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos recomendados", t);
                checkIfLoadingFinished();
            }
        });
    }

    private void getCategories() {
        productManager.getAllCategories(new RepositoryCallback<List<Category>>() {

            @Override
            public void onSuccess(List<Category> result) {
                categoryAdapter.updateCategories(result);

                checkIfLoadingFinished();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar categorias", t);
                checkIfLoadingFinished();
            }
        });
    }

    private void getPromotionProducts() {
        productManager.getPromotionProducts(20, 50, 0, 1, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                promoAdapter.updateProducts(result);
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
        forYouAdapter.setSavedIds(favoriteIds);
        popularAdapter.setSavedIds(favoriteIds);
        accessoryAdapter.setSavedIds(favoriteIds);
        maybeYouLikeAdapter.setSavedIds(favoriteIds);
        promoAdapter.setSavedIds(favoriteIds);

        forYouAdapter.notifyDataSetChanged();
        popularAdapter.notifyDataSetChanged();
        accessoryAdapter.notifyDataSetChanged();
        productContinueAdapter.notifyDataSetChanged();
        maybeYouLikeAdapter.notifyDataSetChanged();
        promoAdapter.notifyDataSetChanged();
    }

    private void checkIfLoadingFinished() {
        loadingCount++;
        Log.d("DEBUG", "loadingCount: " + loadingCount);

        if (loadingCount >= 5) {
            containerProductsHome.setVisibility(View.VISIBLE);

            shimmerForYouSkeleton.stopShimmer();
            shimmerCategories.stopShimmer();
            shimmerCategories.setVisibility(View.GONE);
            shimmerForYouSkeleton.setVisibility(View.GONE);
            containerCategorias.setVisibility(View.VISIBLE);
            showContainers();

            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void startComponentes(View view) {
        rvPopular = view.findViewById(R.id.rvPopular);
        rvContinueBuy = view.findViewById(R.id.rvContinueBuy);
        rvMaybeYouLike = view.findViewById(R.id.rvMaybeYouLike);
        rvCategories = view.findViewById(R.id.rvCategories);

        popularContainer = view.findViewById(R.id.popularContainer);
        forYouContainer = view.findViewById(R.id.forYouContainer);
        maybeYouLikeContainer = view.findViewById(R.id.maybeYouLikeContainer);

        rvContinueBuySkeleton = view.findViewById(R.id.rvContinueBuySkeleton);
        containerProductsHome = view.findViewById(R.id.containerProductsHome);
        containerCategorias = view.findViewById(R.id.containerCategorias);
        promoProductsContainer = view.findViewById(R.id.promoProductsContainer);
        shimmerForYouSkeleton = view.findViewById(R.id.forYouSkeleton);
        shimmerCategories = view.findViewById(R.id.shimmerCategorias);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        shimmerCategories.startShimmer();
        shimmerForYouSkeleton.startShimmer();

        shimmerForYouSkeleton.startShimmer();
        rvContinueBuy.setVisibility(View.GONE);
        containerCategorias.setVisibility(View.GONE);

        horizontalScrollViewCategories = view.findViewById(R.id.horizontalScrollViewCategories);
        fakeScrollbar = view.findViewById(R.id.fakeScrollbar);
        fakeScrollbarTrack = view.findViewById(R.id.fakeScrollbarTrack);
        TextView seeMorePromo = view.findViewById(R.id.seeMorePromo);
        TextView seeMorePop = view.findViewById(R.id.seeMorePop);

        seeMorePromo.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("listType", "promotion");

            NavOptions navOptions = new NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build();

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
            navController.navigate(R.id.productListFragment, bundle, navOptions);
        });

        seeMorePop.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("listType", "popular");

            NavOptions navOptions = new NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build();

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
            navController.navigate(R.id.productListFragment, bundle, navOptions);
        });

        HeaderFragment.HeaderListener listener = (HeaderFragment.HeaderListener) getActivity();

        if (listener != null) {
            listener.hideBackButton();
        }

        horizontalScrollViewCategories.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollX = horizontalScrollViewCategories.getScrollX();
            int contentWidth = horizontalScrollViewCategories.getChildAt(0).getWidth();
            int visibleWidth = horizontalScrollViewCategories.getWidth();

            int maxScrollX = contentWidth - visibleWidth;
            int trackWidth = fakeScrollbarTrack.getWidth();
            int thumbWidth = fakeScrollbar.getWidth();

            int maxThumbTranslation = trackWidth - thumbWidth;

            if (maxScrollX > 0 && maxThumbTranslation > 0) {
                float proportion = (float) scrollX / maxScrollX;
                float translationX = proportion * maxThumbTranslation;

                fakeScrollbar.setTranslationX(
                        fakeScrollbarTrack.getX() + translationX
                );
            } else {
                fakeScrollbar.setTranslationX(fakeScrollbarTrack.getX());
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> loadHomeData(view));

    }

    private void loadHomeData(View view) {
        loadedProductIds.clear();
        loadingCount = 0;

        showSkeletons();
        getForYou();
        getContinueBuy(view);
        maybeYouLike();
        getPopular();
        getCategories();
        getPromotionProducts();
    }

    private void setupSkeletonLoader() {
        List<Product> fakeList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fakeList.add(new Product());
        }

        ProductContinueAdapter skeletonAdapter = new ProductContinueAdapter(fakeList, user.getUserId(), this, this);
        rvContinueBuySkeleton.setAdapter(skeletonAdapter);
    }

    private void showSkeletons() {
        loadedProductIds.clear();

        shimmerForYouSkeleton.setVisibility(View.VISIBLE);
        shimmerCategories.setVisibility(View.VISIBLE);
        shimmerForYouSkeleton.startShimmer();
        shimmerCategories.startShimmer();

        rvContinueBuy.setVisibility(View.GONE);
        containerCategorias.setVisibility(View.GONE);

        hideContainers();
    }

    private void hideContainers() {
        popularContainer.setVisibility(View.GONE);
        forYouContainer.setVisibility(View.GONE);
        maybeYouLikeContainer.setVisibility(View.GONE);
        promoProductsContainer.setVisibility(View.GONE);
    }

    private void showContainers() {
        popularContainer.setVisibility(View.VISIBLE);
        forYouContainer.setVisibility(View.VISIBLE);
        maybeYouLikeContainer.setVisibility(View.VISIBLE);
        promoProductsContainer.setVisibility(View.VISIBLE);

    }

    private List<Product> limitDisplay(List<Product> list, int maxCount) {
        return list.size() > maxCount ? list.subList(0, maxCount) : list;
    }


    private List<Product> filterNewProducts(List<Product> products) {
        List<Product> uniqueProducts = new ArrayList<>();
        for (Product product : products) {
            if (!loadedProductIds.contains(product.getProduct_id())) {
                loadedProductIds.add(product.getProduct_id());
                uniqueProducts.add(product);
            }
        }
        return uniqueProducts;
    }

    @Override
    public void onProductClick(Product product) {
        Bundle bundle = new Bundle();
        bundle.putInt("productId", product.getProduct_id());

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

        NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
        navController.navigate(R.id.action_home_to_productDetailsFragment, bundle);
    }

    @Override
    public void onCategoryClick(Category category) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("category", category);
        Fragment fragment = new CategoryFragment();
        fragment.setArguments(bundle);

        NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
        navController.navigate(R.id.action_home_to_categoryFragment, bundle);
    }

    @Override
    public void onSaveChanged() {
        updateAllAdapters();
    }

}
