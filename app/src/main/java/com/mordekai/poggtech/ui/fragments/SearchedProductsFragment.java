package com.mordekai.poggtech.ui.fragments;

import com.mordekai.poggtech.data.adapter.ProductSearchedAdapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

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
    private Switch poggersFilter;
    private RecyclerView searchedProducts;
    private ApiProduct apiProduct;
    private ProductManager productManager;
    private List<Integer> favoriteIds = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searched_products, container, false);
        headerFragment = (HeaderFragment) getChildFragmentManager().findFragmentById(R.id.headerContainer);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);

        initializeComponents(view);

        productSearchedAdapter = new ProductSearchedAdapter(new ArrayList<>(), user);
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
                applyFilter();
            }

            @Override
            public void onFailure(Throwable t) {}
        });
    }

    private void applyFilter() {
        List<Product> filtered = new ArrayList<>();

        boolean onlyPoggers = poggersFilter.isChecked();

        for (Product p : allProducts) {
            if (onlyPoggers) {
                if (p.isPoggers()) {
                    filtered.add(p);
                }
            } else {
                filtered.add(p);
            }
        }

        productSearchedAdapter.updateProducts(filtered);
    }


    private void initializeComponents(View view) {
        searchedProducts = view.findViewById(R.id.searchedProducts);
        searchProd = getActivity().findViewById(R.id.searchProd);
        poggersFilter = view.findViewById(R.id.poggersFilter);

        poggersFilter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            applyFilter();
        });
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
