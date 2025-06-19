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
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.OrdersAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Order;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiOrder;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.OrderManager;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrdersFragment extends Fragment {

    private List<Order> orders = new ArrayList<>();
    private OrdersAdapter ordersAdapter;
    private OrderManager orderManager;
    private User user;
    private AppCompatImageView btnBack;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        ApiOrder apiOrder = Objects.requireNonNull(RetrofitClient.getRetrofitInstance()).create(ApiOrder.class);
        orderManager = new OrderManager(apiOrder);

        startComponents(view);

        ordersAdapter = new OrdersAdapter(orders, this::orderClicked);

        RecyclerView rvOrders = view.findViewById(R.id.rvOrdersDetails);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvOrders.setNestedScrollingEnabled(true);
        rvOrders.setAdapter(ordersAdapter);

        getOrders(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        
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
                ordersAdapter.updateOrders(result);
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

    private void orderClicked(Order order) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);

        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build();

        NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
        navController.navigate(R.id.orderDetailsFragment, bundle, navOptions);
    }

}
