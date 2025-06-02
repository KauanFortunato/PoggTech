package com.mordekai.poggtech.ui.fragments;

import com.mordekai.poggtech.data.adapter.ProductSearchedAdapter;

import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiInteraction;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.InteractionManager;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchedProductsFragment extends Fragment implements HeaderFragment.HeaderListener {

    private HeaderFragment headerFragment;
    private ProductSearchedAdapter productSearchedAdapter;
    private SharedPrefHelper sharedPrefHelper;
    private InteractionManager interactionManager;
    private User user;
    private EditText searchProd;
    private Switch poggersFilter;
    private boolean highPrice = false, lowPrice = false;
    private AppCompatButton filterHigh, filterLow;
    private RecyclerView searchedProducts;
    private ApiProduct apiProduct;
    private ProductManager productManager;
    private List<Integer> favoriteIds = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searched_products, container, false);

        ((MainActivity) requireActivity()).setForceBackToHome(true);
        ((BottomNavVisibilityController) requireActivity()).showBottomNav();

        headerFragment = (HeaderFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.headerContainer);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);

        initializeComponents(view);

        // Quando o produto for clicado
        productSearchedAdapter = new ProductSearchedAdapter(new ArrayList<>(), user, product -> {
            Bundle bundle = new Bundle();
            bundle.putInt("productId", product.getProduct_id());

            interactionManager.userInteraction(product.getProduct_id(), user.getUserId(), "view", new RepositoryCallback<String>() {
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
            navController.navigate(R.id.productDetailsFragment);
        });

        getFavoriteProducts();
        searchedProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        searchedProducts.setAdapter(productSearchedAdapter);

        getSearchedProducts(searchProd.getText().toString());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (headerFragment != null) {
            headerFragment.closeSearchProd();
        }
        ((MainActivity) requireActivity()).setForceBackToHome(true);
        ((BottomNavVisibilityController) requireActivity()).showBottomNav();
        showBackButton();

        buttonSelect(filterHigh);
        buttonSelect(filterLow);
    }

    private void getFavoriteProducts() {
        productManager.fetchUserFavOrCart(user.getUserId(), 1, new RepositoryCallback<List<Integer>>() {
            @Override
            public void onSuccess(List<Integer> favorites) {
                favoriteIds.clear();
                favoriteIds.addAll(favorites);

                productSearchedAdapter.setFavoriteIds(favoriteIds);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos favoritos", t);
            }
        });
    }

    private void getSearchedProducts(String search) {
        productManager.searchProducts(search, new RepositoryCallback<List<Product>>() {

            @Override
            public void onSuccess(List<Product> result) {
                allProducts = result;
                applyCombinedFitlers();
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    private void applyCombinedFitlers() {
        List<Product> filtered = new ArrayList<>();

        boolean onlyPoggers = poggersFilter.isChecked();

        for (Product product : allProducts) {
            if (onlyPoggers) {
                if (product.isPoggers()) {
                    filtered.add(product);
                }
            } else {
                filtered.add(product);
            }
        }

        if (highPrice) {
            Collections.sort(filtered, (p1, p2) -> Float.compare(p2.getPrice(), p1.getPrice()));
        } else {
            if (lowPrice) {
                Collections.sort(filtered, (p1, p2) -> Float.compare(p1.getPrice(), p2.getPrice()));
            }
        }

        filteredProducts.clear();
        filteredProducts.addAll(filtered);

        productSearchedAdapter.updateProducts(filteredProducts);
    }

    private void initializeComponents(View view) {
        searchedProducts = view.findViewById(R.id.searchedProducts);
        searchProd = getActivity().findViewById(R.id.searchProd);
        poggersFilter = view.findViewById(R.id.poggersFilter);
        filterHigh = view.findViewById(R.id.filterHigh);
        filterLow = view.findViewById(R.id.filterLow);

        getActivity().findViewById(R.id.headerContainer).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        poggersFilter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            applyCombinedFitlers();
        });

        filterHigh.setOnClickListener(v -> {
            if (filterHigh.isHapticFeedbackEnabled()) {
                filterHigh.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            highPrice = !highPrice;
            lowPrice = false;

            applyCombinedFitlers();
            buttonSelect(filterLow);
            buttonSelect(filterHigh);
        });

        filterLow.setOnClickListener(v -> {
            if (filterLow.isHapticFeedbackEnabled()) {
                filterLow.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            lowPrice = !lowPrice;
            highPrice = false;

            applyCombinedFitlers();
            buttonSelect(filterHigh);
            buttonSelect(filterLow);
        });

        interactionManager = new InteractionManager(RetrofitClient.getRetrofitInstance().create(ApiInteraction.class));
    }

    private void buttonSelect(AppCompatButton button) {

        if (button == filterHigh) {
            if (highPrice) {
                button.setBackgroundResource(R.drawable.bg_item_filter_selected);
                button.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                button.setBackgroundResource(R.drawable.bg_item_filter);
                button.setTextColor(getResources().getColor(R.color.textSecondary));
            }
        } else {
            if (lowPrice) {
                button.setBackgroundResource(R.drawable.bg_item_filter_selected);
                button.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                button.setBackgroundResource(R.drawable.bg_item_filter);
                button.setTextColor(getResources().getColor(R.color.textSecondary));
            }
        }
    }

    @Override
    public void showBackButton() {
        if (headerFragment != null) {
            headerFragment.showButtonBack();
        }
    }

    @Override
    public void hideBackButton() {
        if (headerFragment != null) {
            headerFragment.hideButtonBack();
        }
    }

    @Override
    public void closeSearchProd() {
        if (headerFragment != null) {
            headerFragment.closeSearchProd();
        }
    }
}
