package com.mordekai.poggtech.presentation.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.ProductAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ProductManager productManager;
    private String listType;
    private SharedPrefHelper sharedPrefHelper;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.item_product, null, null);
        recyclerView.setAdapter(productAdapter);

        productManager = new ProductManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));

        listType = getArguments().getString("listType", "popular");

        loadProductsByType();

        return view;
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
}
