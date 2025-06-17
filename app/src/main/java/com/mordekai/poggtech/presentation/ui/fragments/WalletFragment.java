package com.mordekai.poggtech.presentation.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.PaymentsAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Payments;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.model.Wallet;
import com.mordekai.poggtech.data.remote.ApiReview;
import com.mordekai.poggtech.data.remote.ApiWallet;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.ReviewManager;
import com.mordekai.poggtech.domain.WalletManager;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.presentation.viewmodel.ReviewViewModel;
import com.mordekai.poggtech.presentation.viewmodel.WalletViewModel;
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
    private WalletViewModel walletViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        rvTransaction = view.findViewById(R.id.rvTransaction);
        rvTransaction.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvTransaction.setNestedScrollingEnabled(true);

        paymentsAdapter = new PaymentsAdapter(new ArrayList<>());
        rvTransaction.setAdapter(paymentsAdapter);


        WalletManager walletManager = new WalletManager(RetrofitClient.getRetrofitInstance().create(ApiWallet.class));
        walletViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new WalletViewModel(walletManager);
            }
        }).get(WalletViewModel.class);

        setupObservers(view);
        startComponents(view);

        getWallet();
        getPayments();

        return view;
    }

    private void getWallet() {
        walletViewModel.fetchWallet(user.getUserId());
    }

    private void getPayments() {
        walletViewModel.fetchPayments(user.getUserId());
    }

    private void deposit(float amount) {
        walletViewModel.deposit(user.getUserId(), amount);
    }

    private void setupObservers(View view) {
        walletViewModel.getWalletLiveData().observe(getViewLifecycleOwner(), wlt -> {
            wallet = wlt;
            balance.setText(String.format("%s %2f", getString(R.string.eur), wallet.getBalance()));
        });

        walletViewModel.getWalletError().observe(getViewLifecycleOwner(), errorMessage -> {
            Log.e("API_RESPONSE", "Erro ao buscar carteira: " + errorMessage);
        });

        walletViewModel.getPaymentsLiveData().observe(getViewLifecycleOwner(), result -> {
            payments = result;
            paymentsAdapter.updatePayments(payments);

            rvTransaction.postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);

                shimmerPayments.stopShimmer();
                shimmerPayments.setVisibility(View.GONE);
                rvTransaction.setVisibility(View.VISIBLE);
            }, 300);
        });

        walletViewModel.getPaymentsError().observe(getViewLifecycleOwner(), errorMessage -> {
            Log.e("API_RESPONSE", "Erro ao buscar pagamentos: " + errorMessage);

            rvTransaction.postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);

                shimmerPayments.stopShimmer();
                shimmerPayments.setVisibility(View.GONE);
                emptyTransactions.setVisibility(View.VISIBLE);
            }, 300);
        });

    }

    private void startComponents(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        balance = view.findViewById(R.id.balance);
        shimmerPayments = view.findViewById(R.id.shimmerPayments);
        emptyTransactions = view.findViewById(R.id.emptyTransactions);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        containerShimmerPayments = view.findViewById(R.id.containerShimmerPayments);
        TextView btnAddMoney = view.findViewById(R.id.addMoney);

        shimmerPayments.startShimmer();

        btnBack.setOnClickListener(v -> {
            if (btnBack.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
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

        btnAddMoney.setOnClickListener(v -> showDepositDialog());
    }


    private void showDepositDialog() {
        Context context = requireContext();

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 10, 60, 10);

        TextView infoText = new TextView(context);
        infoText.setText("Digite o valor que deseja adicionar à carteira:");
        infoText.setTextSize(14);
        infoText.setTextColor(context.getResources().getColor(R.color.textTertiary));
        infoText.setPadding(0, 25, 0, 0);
        infoText.setGravity(Gravity.LEFT);
        layout.addView(infoText);

        EditText inputAmount = new EditText(context);
        inputAmount.setHint("Ex: 50.00");
        inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(inputAmount);

        builder.setTitle("Adicionar saldo");
        builder.setBackground(context.getResources().getDrawable(R.drawable.bg_card_product, null));
        builder.setView(layout);

        builder.setPositiveButton("Depositar", (dialog, which) -> {
            String value = inputAmount.getText().toString().trim();
            if (!value.isEmpty()) {
                try {
                    float amount = Float.parseFloat(value);
                    if (amount > 0) {
                        deposit(amount);
                    } else {
                        Toast.makeText(context, "Insira um valor maior que zero", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Valor inválido", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Campo vazio", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
