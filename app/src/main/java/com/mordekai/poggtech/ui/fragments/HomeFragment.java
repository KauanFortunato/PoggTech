package com.mordekai.poggtech.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.mordekai.poggtech.utils.SharedPrefHelper;

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

        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);
        forYouAdapter = new ProductAdapter(productForYouList, user.getUserId(), R.layout.item_product_match_parent, this, this);
        rvForYou.setAdapter(forYouAdapter);

        apiInteraction = RetrofitClient.getRetrofitInstance().create(ApiInteraction.class);
        interactionManager = new InteractionManager(apiInteraction);

        getContinueBuy();
        getForYou();
        maybeYouLike();
        getPopular();
        getCategories();

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
        productManager.getProductsFavCategories(user.getUserId(), 4, new RepositoryCallback<List<Product>>() {
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
                        forYouAdapter.updateProducts(result);
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

    private void maybeYouLike() {
        productManager.getProductsFromFavCategory(user.getUserId(), new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                maybeYouLikeAdapter.updateProducts(result);
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
            }
        });
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

        if (loadingCount >= 5) {
            containerProductsHome.setVisibility(View.VISIBLE);

            shimmerForYouSkeleton.stopShimmer();
            shimmerCategories.stopShimmer();
            shimmerCategories.setVisibility(View.GONE);
            shimmerForYouSkeleton.setVisibility(View.GONE);
            containerCategorias.setVisibility(View.VISIBLE);
            showContainers();

            swipeRefreshLayout.setRefreshing(false); // para o swipe
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

    private void hideContainers() {
        popularContainer.setVisibility(View.GONE);
        forYouContainer.setVisibility(View.GONE);
        maybeYouLikeContainer.setVisibility(View.GONE);
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
                .add(R.id.containerFrame, fragment)
                .addToBackStack("product_details")
                .commit();
    }

    @Override
    public void onCategoryClick(Category category) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("category", category);
        Fragment fragment = new CategoryFragment();
        fragment.setArguments(bundle);

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

    @Override
    public void onSaveChanged() {
        updateAllAdapters();
    }

}
