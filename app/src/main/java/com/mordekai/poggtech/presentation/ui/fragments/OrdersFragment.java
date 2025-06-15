package com.mordekai.poggtech.presentation.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.OrdersAdapter;
import com.mordekai.poggtech.data.adapter.ProductAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Order;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiInteraction;
import com.mordekai.poggtech.data.remote.ApiOrder;
import com.mordekai.poggtech.data.remote.ApiReview;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.InteractionManager;
import com.mordekai.poggtech.domain.OrderManager;
import com.mordekai.poggtech.domain.ReviewManager;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.presentation.viewmodel.ReviewViewModel;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements ProductAdapter.OnProductClickListener,
        ProductAdapter.OnSavedChangedListener {

    private List<Order> orders = new ArrayList<>();
    private OrdersAdapter ordersAdapter;
    private OrderManager orderManager;
    private User user;
    private List<Integer> favoriteIds = new ArrayList<>();
    private ProductAdapter forYouAdapter;
    private InteractionManager interactionManager;
    private AppCompatImageView btnBack;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        ((BottomNavVisibilityController) requireActivity()).hideBottomNav();

        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        ApiOrder apiOrder = RetrofitClient.getRetrofitInstance().create(ApiOrder.class);
        orderManager = new OrderManager(apiOrder);

        startComponents(view);

        ordersAdapter = new OrdersAdapter(orders, getChildFragmentManager());
        RecyclerView rvOrders = view.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvOrders.setNestedScrollingEnabled(true);
        rvOrders.setAdapter(ordersAdapter);

        interactionManager = new InteractionManager(RetrofitClient.getRetrofitInstance().create(ApiInteraction.class));


        ReviewManager rvMgr = new ReviewManager(RetrofitClient.getRetrofitInstance().create(ApiReview.class));
        ReviewViewModel reviewViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ReviewViewModel(rvMgr);
            }
        }).get(ReviewViewModel.class);

        getOrders(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BottomNavVisibilityController) requireActivity()).hideBottomNav();
    }

    private void getOrders(View view) {
        orderManager.GetOrders(user.getUserId(), new RepositoryCallback<List<Order>>() {

            @Override
            public void onSuccess(List<Order> result) {
                if(result.isEmpty()) {
                    view.findViewById(R.id.emptyOrder).setVisibility(View.VISIBLE);
                    return;
                }
                orders = result;
                ordersAdapter.updateOrders(orders);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar pedidos", t);
            }
        });
    }

    private void startComponents(View view) {
        btnBack = view.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> {
            if (btnBack.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
            navController.popBackStack();
        });
    }

    @Override
    public void onProductClick(Product product) {
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

        NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
        navController.navigate(R.id.productDetailsFragment, bundle);
    }

    @Override
    public void onSaveChanged() {
        forYouAdapter.setSavedIds(favoriteIds);
        forYouAdapter.notifyDataSetChanged();
    }
}
