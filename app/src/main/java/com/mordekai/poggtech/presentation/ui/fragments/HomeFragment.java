package com.mordekai.poggtech.presentation.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
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
import com.mordekai.poggtech.presentation.viewmodel.ProductViewModel;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.ProductLoader;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import androidx.lifecycle.ViewModelProvider;


import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment
        implements ProductAdapter.OnProductClickListener,
        ProductAdapter.OnSavedChangedListener,
        ProductContinueAdapter.OnProductContinueClickListener,
        CategoryAdapter.OnCategoryClickListerner {

    private SharedPrefHelper sharedPrefHelper;
    private LinearLayout containerProductsHome, containerCategorias;
    private LinearLayout popularContainer, forYouContainer, maybeYouLikeContainer;
    private RecyclerView rvForYou, rvPopular, rvContinueBuySkeleton, rvContinueBuy, rvMaybeYouLike, rvCategories;
    private ProductAdapter forYouAdapter, popularAdapter, accessoryAdapter, maybeYouLikeAdapter;
    private CategoryAdapter categoryAdapter;
    private ProductContinueAdapter productContinueAdapter;
    private ApiInteraction apiInteraction;
    private List<Product> products = new ArrayList<>();
    private List<Product> productForYouList = new ArrayList<>();
    private ProductManager productManager;
    private ProductViewModel productViewModel;
    private InteractionManager interactionManager;
    private User user;
    private int loadingCount = 0;
    private List<Integer> favoriteIds = new ArrayList<>();
    private ShimmerFrameLayout shimmerCategories, shimmerForYouSkeleton;
    HorizontalScrollView horizontalScrollViewCategories;
    View fakeScrollbar, fakeScrollbarTrack;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        if (user == null) {
            Log.e("HomeFragment", "ERRO: Usuário está null! Verifica se o login foi completado.");
            return inflater.inflate(R.layout.fragment_home, container, false); // ou um layout de erro
        }

        productManager = new ProductManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));

        productViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ProductViewModel(productManager);
            }
        }).get(ProductViewModel.class);


        startComponentes(view);
        ((BottomNavVisibilityController) requireActivity()).showBottomNav();
        hideContainers();

        setupSkeletonLoader();

        containerProductsHome.setVisibility(View.VISIBLE);

        popularAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.item_product_match_parent, this, this);
        accessoryAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.item_product_match_parent, this, this);
        maybeYouLikeAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.item_product, this, this);
        productContinueAdapter = new ProductContinueAdapter(new ArrayList<>(), user.getUserId(), this, this);
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), getContext(), this, R.layout.item_category);

        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        rvPopular.setLayoutManager(new GridLayoutManager(getContext(),2));
        rvPopular.setAdapter(popularAdapter);

        rvMaybeYouLike.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvMaybeYouLike.setNestedScrollingEnabled(false);

        rvMaybeYouLike.setAdapter(maybeYouLikeAdapter);

        rvForYou = view.findViewById(R.id.rvForYou);
        rvForYou.setLayoutManager(new GridLayoutManager(getContext(),2));
        rvForYou.setNestedScrollingEnabled(false);

        forYouAdapter = new ProductAdapter(productForYouList, user.getUserId(), R.layout.item_product_match_parent, this, this);
        rvForYou.setAdapter(forYouAdapter);

        apiInteraction = RetrofitClient.getRetrofitInstance().create(ApiInteraction.class);
        interactionManager = new InteractionManager(apiInteraction);

        getContinueBuy();
        maybeYouLike();
        getPopular();
        getForYou();
        getCategories();
        productViewModel.verifProductsSaved(user.getUserId(), 1);

        return view;
    }

    @MainThread
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productViewModel.getPopularProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            popularAdapter.updateProducts(products);
            checkIfLoadingFinished();
        });

        // Produtos para "for you"
        productViewModel.getProductsFavCategoriesLiveData().observe(getViewLifecycleOwner(), products -> {
            productForYouList.clear();
            productForYouList.addAll(products);
            forYouAdapter.updateProducts(products);
            checkIfLoadingFinished();
        });

        // IDs dos produtos salvos
        productViewModel.getProductsSavedLiveData().observe(getViewLifecycleOwner(), ids -> {
            favoriteIds.clear();
            if (ids != null) {
                favoriteIds.addAll(ids);
            }

            updateAllAdapters();
        });

        productViewModel.getProductsFromFavCategoryLiveData().observe(getViewLifecycleOwner(), products -> {
            maybeYouLikeAdapter.updateProducts(products);
            checkIfLoadingFinished();
        });

        productViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            categoryAdapter.updateCategories(categories);
            checkIfLoadingFinished();
        });

        productViewModel.getRecommendedProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            if (products.size() >= 3) {
                rvContinueBuy.setVisibility(View.VISIBLE);
                productContinueAdapter.updateProducts(products);
                rvContinueBuy.setAdapter(productContinueAdapter);
            }
            checkIfLoadingFinished();
        });

        productViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Log.e("API_ERROR", error);
        });


        if (isVisible()) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isVisible()) return;

        HeaderFragment.HeaderListener listener = (HeaderFragment.HeaderListener) getActivity();
        ((BottomNavVisibilityController) requireActivity()).showBottomNav();

        if (listener != null) {
            listener.hideBackButton();
        }

        // Garante que o headerContainer seja visível, mesmo se o fragmento estiver a ser restaurado
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

    private void getContinueBuy() {
        productViewModel.getRecommendedProducts(user.getUserId());
    }

    private void getForYou() {
        productViewModel.getProductsFavCategories(user.getUserId(), 4);
    }

    private void getPopular() {
        productViewModel.getPopularProducts(false, 6);
    }

    private void maybeYouLike() {
        productViewModel.getProductsFromFavCategory(user.getUserId());
    }

    private void getCategories() {
        productViewModel.getAllCategories();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAllAdapters() {
        forYouAdapter.setSavedIds(favoriteIds);
        popularAdapter.setSavedIds(favoriteIds);
        accessoryAdapter.setSavedIds(favoriteIds);
        maybeYouLikeAdapter.setSavedIds(favoriteIds);

        forYouAdapter.notifyDataSetChanged();
        popularAdapter.notifyDataSetChanged();
        accessoryAdapter.notifyDataSetChanged();
        productContinueAdapter.notifyDataSetChanged();
        maybeYouLikeAdapter.notifyDataSetChanged();
    }

    private void checkIfLoadingFinished() {
        loadingCount++;
        Log.d("DEBUG", "loadingCount: " + loadingCount);

        if (loadingCount >= 4) {
            containerProductsHome.setVisibility(View.VISIBLE);
            containerCategorias.setVisibility(View.VISIBLE);
            hideSkeletons();
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

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            showSkeletons();

            loadingCount = 0;
            getContinueBuy();
            getForYou();
            maybeYouLike();
            getPopular();
            getCategories();
        });
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
        shimmerForYouSkeleton.setVisibility(View.VISIBLE);
        shimmerCategories.setVisibility(View.VISIBLE);
        shimmerForYouSkeleton.startShimmer();
        shimmerCategories.startShimmer();

        rvContinueBuy.setVisibility(View.GONE);
        containerCategorias.setVisibility(View.GONE);

        hideContainers();
    }

    private void hideSkeletons() {
        shimmerForYouSkeleton.stopShimmer();
        shimmerCategories.stopShimmer();
        shimmerForYouSkeleton.setVisibility(View.GONE);
        shimmerCategories.setVisibility(View.GONE);
        rvContinueBuySkeleton.setVisibility(View.GONE);
    }

    private void hideContainers() {
        popularContainer.setVisibility(View.GONE);
        forYouContainer.setVisibility(View.GONE);
        maybeYouLikeContainer.setVisibility(View.GONE);
        rvContinueBuy.setVisibility(View.GONE);
    }

    private void showContainers() {
        popularContainer.setVisibility(View.VISIBLE);
        forYouContainer.setVisibility(View.VISIBLE);
        maybeYouLikeContainer.setVisibility(View.VISIBLE);
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

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_homeFragment_to_productDetailsFragment3, bundle);
    }

    @Override
    public void onCategoryClick(Category category) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("category", category);
        Fragment fragment = new CategoryFragment();
        fragment.setArguments(bundle);

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.categoryFragment, bundle);
    }

    @Override
    public void onSaveChanged() {
        updateAllAdapters();
    }

}
