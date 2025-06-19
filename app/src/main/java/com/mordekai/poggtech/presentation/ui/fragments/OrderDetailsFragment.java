package com.mordekai.poggtech.presentation.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private TextView userInfo, location;

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
        setupShippingLayout(view);
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
        userInfo = view.findViewById(R.id.userInfo);
        location = view.findViewById(R.id.location);

        location.setText(order.getLocation());

        userInfo.setText(order.getUser_name() + " - " + order.getUser_phone());

        btn_back.setOnClickListener(v -> {
            if (btn_back.isHapticFeedbackEnabled()) {
                btn_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
            navController.popBackStack();
        });
    }

    public void setupShippingLayout(View view) {
        AppCompatImageView icon_confirmado, icon_enviado, icon_transito, icon_entregue;
        TextView text_confirmado,text_enviado, text_transito, text_entregue;
        View line1, line2, line3;
        icon_confirmado = view.findViewById(R.id.icon_confirmado);
        text_confirmado = view.findViewById(R.id.text_confirmado);
        icon_enviado = view.findViewById(R.id.icon_enviado);
        text_enviado = view.findViewById(R.id.text_enviado);
        icon_transito = view.findViewById(R.id.icon_transito);
        text_transito = view.findViewById(R.id.text_transito);
        icon_entregue = view.findViewById(R.id.icon_entregue);
        text_entregue = view.findViewById(R.id.text_entregue);

        line1 = view.findViewById(R.id.line1);
        line2 = view.findViewById(R.id.line2);
        line3 = view.findViewById(R.id.line3);

        switch (order.getShipping_status()) {
            case "confirmado":
                icon_confirmado.setColorFilter(getResources().getColor(R.color.colorSecondary));
                text_confirmado.setTextColor(getResources().getColor(R.color.colorSecondary));
                break;
            case "enviado":
                icon_confirmado.setColorFilter(getResources().getColor(R.color.colorSecondary));
                text_confirmado.setTextColor(getResources().getColor(R.color.colorSecondary));

                line1.setBackgroundColor(view.getContext().getColor(R.color.colorSecondary));
                icon_enviado.setColorFilter(getResources().getColor(R.color.colorSecondary));
                text_enviado.setTextColor(getResources().getColor(R.color.colorSecondary));
                break;
            case "transito":
                icon_confirmado.setColorFilter(getResources().getColor(R.color.colorSecondary));
                text_confirmado.setTextColor(getResources().getColor(R.color.colorSecondary));

                line1.setBackgroundColor(view.getContext().getColor(R.color.colorSecondary));
                icon_enviado.setColorFilter(getResources().getColor(R.color.colorSecondary));
                text_enviado.setTextColor(getResources().getColor(R.color.colorSecondary));

                line2.setBackgroundColor(view.getContext().getColor(R.color.colorSecondary));
                icon_transito.setColorFilter(getResources().getColor(R.color.colorSecondary));
                text_transito.setTextColor(getResources().getColor(R.color.colorSecondary));
                break;
            case "entregue":
                icon_confirmado.setColorFilter(getResources().getColor(R.color.colorSecondary));
                text_confirmado.setTextColor(getResources().getColor(R.color.colorSecondary));

                line1.setBackgroundColor(view.getContext().getColor(R.color.colorSecondary));
                icon_enviado.setColorFilter(getResources().getColor(R.color.colorSecondary));
                text_enviado.setTextColor(getResources().getColor(R.color.colorSecondary));

                line2.setBackgroundColor(view.getContext().getColor(R.color.colorSecondary));
                icon_transito.setColorFilter(getResources().getColor(R.color.colorSecondary));
                text_transito.setTextColor(getResources().getColor(R.color.colorSecondary));

                line3.setBackgroundColor(view.getContext().getColor(R.color.colorSecondary));
                icon_entregue.setColorFilter(getResources().getColor(R.color.colorSecondary));
                text_entregue.setTextColor(getResources().getColor(R.color.colorSecondary));
                break;
        }
    }
}
