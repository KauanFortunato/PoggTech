package com.mordekai.poggtech.presentation.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.MyAdAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

public class MyAdsFragment extends Fragment {

    private MyAdAdapter myAdAdapter;
    private RecyclerView rvMyAds;
    private List<Product> products = new ArrayList<>();
    private User user;
    private ProductManager productManager;
    private ImageButton btnBack;
    private AppCompatButton addNewProduct;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_ads, container, false);
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();
        startComponents(view);

        productManager = new ProductManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));

        myAdAdapter = new MyAdAdapter(products, getChildFragmentManager());
        rvMyAds.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvMyAds.setNestedScrollingEnabled(false);
        rvMyAds.setAdapter(myAdAdapter);

        getMyAds(view);

        swipeRefreshLayout.setOnRefreshListener(() -> getMyAds(view));

        return view;
    }

    private void getMyAds(View view) {
        productManager.getMyProducts(user.getUserId(), new RepositoryCallback<List<Product>>() {

            @Override
            public void onSuccess(List<Product> result) {
                products = result;
                myAdAdapter.updateProducts(products);
                myAdAdapter.notifyDataSetChanged();

                if (products.isEmpty()) {
                    view.findViewById(R.id.emptyAds).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.containerMyAds).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.emptyAds).setVisibility(View.GONE);
                    view.findViewById(R.id.containerMyAds).setVisibility(View.VISIBLE);
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos", t);
                view.findViewById(R.id.emptyAds).setVisibility(View.VISIBLE);
                view.findViewById(R.id.containerMyAds).setVisibility(View.GONE);
            }
        });
    }

    private void startComponents(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        addNewProduct = view.findViewById(R.id.addNewProduct);
        rvMyAds = view.findViewById(R.id.rvMyAds);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        

        btnBack.setOnClickListener(v -> {
            if (btnBack.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
            navController.popBackStack();
        });

        addNewProduct.setOnClickListener(v -> {
            if (addNewProduct.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
            navController.navigate(R.id.action_myAdsFragment_to_newAdFragment);
        });
    }
}
