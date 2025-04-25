package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
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
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

public class MyAdsFragment extends Fragment {

    private MyAdAdapter myAdAdapter;
    private RecyclerView rvMyAds;
    private List<Product> products = new ArrayList<>();
    private User user;
    private ProductManager productManager;
    private ImageButton btnBack, addNewProduct;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_ads, container, false);
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();
        startComponents(view);

        productManager = new ProductManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));

        myAdAdapter = new MyAdAdapter(products);
        rvMyAds.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvMyAds.setNestedScrollingEnabled(false);
        rvMyAds.setAdapter(myAdAdapter);

        getMyAds();

        swipeRefreshLayout.setOnRefreshListener(this::getMyAds);

        return view;
    }

    private void getMyAds() {
        productManager.getMyProducts(user.getUserId(), new RepositoryCallback<List<Product>>() {

            @Override
            public void onSuccess(List<Product> result) {
                products = result;
                myAdAdapter.updateProducts(products);
                myAdAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos", t);
            }
        });
    }

    private void startComponents(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        addNewProduct = view.findViewById(R.id.addNewProduct);
        rvMyAds = view.findViewById(R.id.rvMyAds);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);

        btnBack.setOnClickListener(v -> {
            if (btnBack.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }

            requireActivity().getSupportFragmentManager().popBackStack();
        });

        addNewProduct.setOnClickListener(v -> {
            if (addNewProduct.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }

            NewAdFragment newAdFragment = new NewAdFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.containerFrame, newAdFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
}
