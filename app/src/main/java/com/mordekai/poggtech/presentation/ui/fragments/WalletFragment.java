package com.mordekai.poggtech.presentation.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.PaymentsAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Payments;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.model.Wallet;
import com.mordekai.poggtech.data.remote.ApiWallet;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.WalletManager;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class WalletFragment extends Fragment {

    private AppCompatImageView btnBack;
    private RecyclerView rvTransaction;
    private SharedPrefHelper sharedPrefHelper;
    private User user;
    private List<Payments> payments;
    private Wallet wallet;
    private TextView balance, emptyTransactions;
    private WalletManager walletManager;
    private PaymentsAdapter paymentsAdapter;
    private ShimmerFrameLayout shimmerPayments;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout containerShimmerPayments;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        ((BottomNavVisibilityController) requireActivity()).hideBottomNav();

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        rvTransaction = view.findViewById(R.id.rvTransaction);
        rvTransaction.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvTransaction.setNestedScrollingEnabled(true);

        paymentsAdapter = new PaymentsAdapter(new ArrayList<>());
        rvTransaction.setAdapter(paymentsAdapter);

        walletManager = new WalletManager(RetrofitClient.getRetrofitInstance().create(ApiWallet.class));

        startComponents(view);

        getWallet();
        getPayments();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((BottomNavVisibilityController) requireActivity()).hideBottomNav();
    }

    private void getWallet() {
        walletManager.getWallet(user.getUserId(), new RepositoryCallback<Wallet>() {

            @Override
            public void onSuccess(Wallet result) {
                wallet = result;
                balance.setText(String.valueOf(getString(R.string.eur) + " " + wallet.getBalance()));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos", t);
            }
        });
    }

    private void getPayments() {
        walletManager.getPayments(user.getUserId(), new RepositoryCallback<List<Payments>>() {

            @Override
            public void onSuccess(List<Payments> result) {
                payments = result;
                paymentsAdapter.updatePayments(payments);

                rvTransaction.postDelayed(() -> {
                    swipeRefreshLayout.setRefreshing(false);

                    shimmerPayments.stopShimmer();
                    shimmerPayments.setVisibility(View.GONE);
                    rvTransaction.setVisibility(View.VISIBLE);
                }, 600);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar pagamentos", t);

                rvTransaction.postDelayed(() -> {
                    swipeRefreshLayout.setRefreshing(false);

                    shimmerPayments.stopShimmer();
                    shimmerPayments.setVisibility(View.GONE);
                    emptyTransactions.setVisibility(View.VISIBLE);
                }, 600);

            }
        });
    }

    private void startComponents(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        balance = view.findViewById(R.id.balance);
        shimmerPayments = view.findViewById(R.id.shimmerPayments);
        emptyTransactions = view.findViewById(R.id.emptyTransactions);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        containerShimmerPayments = view.findViewById(R.id.containerShimmerPayments);

        shimmerPayments.startShimmer();

        ((BottomNavVisibilityController) requireActivity()).showBottomNav();

        btnBack.setOnClickListener(v -> {
            if (btnBack.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            shimmerPayments.startShimmer();
            shimmerPayments.setVisibility(View.VISIBLE);
            rvTransaction.setVisibility(View.GONE);
            emptyTransactions.setVisibility(View.GONE);

            containerShimmerPayments.startLayoutAnimation();

            getPayments();
        });
    }
}
