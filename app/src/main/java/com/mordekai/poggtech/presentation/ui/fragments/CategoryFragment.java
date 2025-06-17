package com.mordekai.poggtech.presentation.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.ProductAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Category;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiInteraction;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.InteractionManager;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryFragment extends Fragment implements
        ProductAdapter.OnProductClickListener,
        ProductAdapter.OnSavedChangedListener, HeaderFragment.HeaderListener{

    private SharedPrefHelper sharedPrefHelper;
    private User user;
    private Category category;
    private RecyclerView rvCategory;
    private ProductAdapter productAdapter;
    private ProductManager productManager;
    private List<Product> productList = new ArrayList<>();
    private List<Integer> favoriteIds = new ArrayList<>();
    private InteractionManager interactionManager;
    private ApiInteraction apiInteraction;
    private HeaderFragment headerFragment;

    private AppCompatButton filterHigh, filterLow;
    private Switch poggersFilter;
    private boolean highPrice = false, lowPrice = false;
    private List<Product> filteredProducts = new ArrayList<>();


    private TextView categoryTitle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        headerFragment = (HeaderFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.headerContainer);

        category = getArguments().getParcelable("category");

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();


        startComponentes(view);

        rvCategory.setLayoutManager(new GridLayoutManager(getContext(),2));
        rvCategory.setNestedScrollingEnabled(false);

        productManager = new ProductManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
        productAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.item_product_match_parent, this, this);
        rvCategory.setAdapter(productAdapter);

        apiInteraction = RetrofitClient.getRetrofitInstance().create(ApiInteraction.class);
        interactionManager = new InteractionManager(apiInteraction);

        getProducts();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        showBackButton();
        buttonSelect(filterHigh);
        buttonSelect(filterLow);
    }

    private void startComponentes(View view) {
        rvCategory = view.findViewById(R.id.rvCategory);
        categoryTitle = view.findViewById(R.id.categoryTitle);
        filterHigh = view.findViewById(R.id.filterHigh);
        filterLow = view.findViewById(R.id.filterLow);
        poggersFilter = view.findViewById(R.id.poggersFilter);

        categoryTitle.setText("Categoria de " + category.getName());

        poggersFilter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            applyCombinedFitlers();
        });

        filterHigh.setOnClickListener(v -> {
            if(filterHigh.isHapticFeedbackEnabled()) {
                filterHigh.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            highPrice = !highPrice;
            lowPrice = false;

            applyCombinedFitlers();
            buttonSelect(filterLow);
            buttonSelect(filterHigh);
        });

        filterLow.setOnClickListener(v -> {
            if(filterLow.isHapticFeedbackEnabled()) {
                filterLow.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            lowPrice = !lowPrice;
            highPrice = false;

            applyCombinedFitlers();
            buttonSelect(filterHigh);
            buttonSelect(filterLow);
        });

        showBackButton();
    }

    private void getProducts() {
        productManager.getProductsByCategory(category.getName(), true,new RepositoryCallback<List<Product>>() {

            @Override
            public void onSuccess(List<Product> result) {
                productList = result;
                productAdapter.updateProducts(productList);

                productManager.fetchUserFavOrCart(user.getUserId(), 1, new RepositoryCallback<List<Integer>>() {

                    @Override
                    public void onSuccess(List<Integer> result) {
                        favoriteIds.clear();
                        favoriteIds.addAll(result);

                        productAdapter.setSavedIds(favoriteIds);
                        productAdapter.notifyDataSetChanged();

                        applyCombinedFitlers();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("API_ERROR", "Erro ao buscar produtos: " + t.getMessage(), t);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getContext(), "Erro ao buscar produtos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyCombinedFitlers() {
        List<Product> filtered = new ArrayList<>();

        boolean onlyPoggers = poggersFilter.isChecked();

        for (Product product : productList) {
            if(onlyPoggers) {
                if(product.isPoggers()){
                    filtered.add(product);
                }
            }else{
                filtered.add(product);
            }
        }

        if(highPrice){
            Collections.sort(filtered, (p1, p2) -> Float.compare(p2.getPrice(), p1.getPrice()));
        } else {
            if(lowPrice) {
                Collections.sort(filtered, (p1, p2) -> Float.compare(p1.getPrice(), p2.getPrice()));
            }
        }

        filteredProducts.clear();
        filteredProducts.addAll(filtered);

        productAdapter.updateProducts(filteredProducts);
    }

    private void buttonSelect(AppCompatButton button) {

        if(button == filterHigh) {
            if(highPrice) {
                button.setBackgroundResource(R.drawable.bg_item_filter_selected);
                button.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                button.setBackgroundResource(R.drawable.bg_item_filter);
                button.setTextColor(getResources().getColor(R.color.textSecondary));
            }
        } else {
            if(lowPrice) {
                button.setBackgroundResource(R.drawable.bg_item_filter_selected);
                button.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                button.setBackgroundResource(R.drawable.bg_item_filter);
                button.setTextColor(getResources().getColor(R.color.textSecondary));
            }
        }
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
        navController.navigate(R.id.action_categoryFragment_to_productDetailsFragment, bundle);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSaveChanged() {
        productAdapter.setSavedIds(favoriteIds);
        productAdapter.notifyDataSetChanged();
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
