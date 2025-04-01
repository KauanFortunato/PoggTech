package com.mordekai.poggtech.ui.fragments;

import com.mordekai.poggtech.data.adapter.ProductSearchedAdapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.ApiService;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchedProductsFragment extends Fragment implements HeaderFragment.HeaderListener {

    private HeaderFragment headerFragment;
    private ProductSearchedAdapter productSearchedAdapter;
    private SharedPrefHelper sharedPrefHelper;
    private User user;
    private EditText searchProd;
    private RecyclerView searchedProducts;
    private ApiProduct apiProduct;
    private ProductManager productManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searched_products, container, false);
        headerFragment = (HeaderFragment) getChildFragmentManager().findFragmentById(R.id.headerContainer);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        initializeComponents(view);

        productSearchedAdapter = new ProductSearchedAdapter(new ArrayList<>(), user.getUserId());
        searchedProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        searchedProducts.setAdapter(productSearchedAdapter);

        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);


        getSearchedProducts(searchProd.getText().toString());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (headerFragment != null) {
            headerFragment.closeSearchProd();
        }
    }

    public void getSearchedProducts(String search) {
        productManager.searchProducts(search, new RepositoryCallback<List<Product>>() {

            @Override
            public void onSuccess(List<Product> result) {
                productSearchedAdapter.updateProducts(result);
            }

            @Override
            public void onFailure(Throwable t) {}
        });
    }

    private void initializeComponents(View view) {
        searchedProducts = view.findViewById(R.id.searchedProducts);
        searchProd = getActivity().findViewById(R.id.searchProd);
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
