package com.mordekai.poggtech.presentation.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.ProductAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiInteraction;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.InteractionManager;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment implements ProductAdapter.OnProductClickListener, ProductAdapter.OnSavedChangedListener,  HeaderFragment.HeaderListener {

    private HeaderFragment headerFragment;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ProductManager productManager;
    private String listType;
    private SharedPrefHelper sharedPrefHelper;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        headerFragment = (HeaderFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.headerContainer);
        showBackButton();

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.item_product, this, this);
        recyclerView.setAdapter(productAdapter);

        productManager = new ProductManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));

        listType = getArguments().getString("listType", "popular");

        loadProductsByType();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        showBackButton();
    }

    private void loadProductsByType() {
        switch (listType) {
            case "promotion":
                productManager.getPromotionProducts(10, 100, 0, 1, new RepositoryCallback<List<Product>>() {
                    @Override
                    public void onSuccess(List<Product> result) {
                        productAdapter.updateProducts(result);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("API_ERROR", t.getMessage());
                    }
                });
                break;

            case "popular":
            default:
                productManager.getPopularProducts(true, 0, new RepositoryCallback<List<Product>>() {
                    @Override
                    public void onSuccess(List<Product> result) {
                        productAdapter.updateProducts(result);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("API_ERROR", t.getMessage());
                    }
                });
                break;
        }
    }

    @Override
    public void onProductClick(Product product) {
        Bundle bundle = new Bundle();
        bundle.putInt("productId", product.getProduct_id());

        InteractionManager interactionManager = new InteractionManager(RetrofitClient.getRetrofitInstance().create(ApiInteraction.class));


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

        NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
        navController.navigate(R.id.action_productListFragment_to_productDetailsFragment, bundle);
    }

    @Override
    public void onSaveChanged() {
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
