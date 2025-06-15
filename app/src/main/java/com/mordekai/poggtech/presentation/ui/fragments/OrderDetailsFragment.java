package com.mordekai.poggtech.presentation.ui.fragments;

import static androidx.lifecycle.AndroidViewModel_androidKt.getApplication;

import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.OrderDetailAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Order;
import com.mordekai.poggtech.data.model.OrderItem;
import com.mordekai.poggtech.data.model.Review;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiOrder;
import com.mordekai.poggtech.data.remote.ApiReview;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.OrderManager;
import com.mordekai.poggtech.domain.ReviewManager;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.presentation.viewmodel.ReviewViewModel;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsFragment extends Fragment {

    private RecyclerView rvOrdersDetails;
    private AppCompatImageView btn_back;
    private Order order;
    private OrderDetailAdapter orderDetailAdapter;
    private ReviewViewModel reviewViewModel;
    private List<OrderItem> orderItems = new ArrayList<>();

    private User user;
    private SharedPrefHelper sharedPrefHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        order = (Order) getArguments().getSerializable("order");
        startComponent(view);

        orderDetailAdapter = new OrderDetailAdapter(orderItems, getChildFragmentManager());
        rvOrdersDetails.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvOrdersDetails.setNestedScrollingEnabled(false);
        rvOrdersDetails.setAdapter(orderDetailAdapter);

        ReviewManager rvMgr = new ReviewManager(RetrofitClient.getRetrofitInstance().create(ApiReview.class));
        reviewViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ReviewViewModel(rvMgr);
            }
        }).get(ReviewViewModel.class);


        orderDetailAdapter.reviewSentListener = (product_id, rating, reviewText) -> {
            Review review = new Review();
            review.setRating(rating);
            review.setComment(reviewText);
            review.setUser_id(user.getUserId());
            review.setProduct_id(product_id);


            Log.d("API_RESPONSE", "Review: " + review);
            reviewViewModel.createReview(review, getContext());
        };

        orderItems();
        return view;
    }

    public void orderItems() {
        OrderManager orderManager = new OrderManager(RetrofitClient.getRetrofitInstance().create(ApiOrder.class));
        orderManager.GetOrderItems(order.getId(), new RepositoryCallback<List<OrderItem>>() {
            @Override
            public void onSuccess(List<OrderItem> result) {
                if (result != null) {
                    orderItems.clear();
                    orderItems.addAll(result);
                    Log.d("API_RESPONSE", "Itens recebidos: " + orderItems.size());
                    orderDetailAdapter.updateOrderItems(result);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_FAILURE", "Falha na comunicação com a API", t);
            }
        });
    }

    public void startComponent(View view) {
        rvOrdersDetails = view.findViewById(R.id.rvOrdersDetails);
        btn_back = view.findViewById(R.id.btn_back);

        btn_back.setOnClickListener( v -> {
            if(btn_back.isHapticFeedbackEnabled()) {
                btn_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
            navController.popBackStack();
        });
    }
}
