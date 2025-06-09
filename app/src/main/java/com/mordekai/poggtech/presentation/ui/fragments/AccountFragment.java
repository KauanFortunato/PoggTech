package com.mordekai.poggtech.presentation.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.ProductAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ApiInteraction;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.InteractionManager;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.presentation.ui.activity.LoginActivity;
import com.mordekai.poggtech.presentation.viewmodel.ProductViewModel;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment
        implements ProductAdapter.OnProductClickListener,
        ProductAdapter.OnSavedChangedListener {
    private TextView helloUser, numberAccount;
    private ImageButton buttonConfig, buttonMyPurchases, buttonMyAds;
    private AppCompatImageView walletButton;
    private FirebaseUser currentUser;
    private AppCompatImageView userAvatar;
    private SharedPrefHelper sharedPrefHelper;
    private InteractionManager interactionManager;
    private ProductViewModel productViewModel;
    private User user;
    private RecyclerView rvForYou;
    private ProductAdapter forYouAdapter;
    private List<Product> productList = new ArrayList<>();
    private ProductManager productManager;
    private List<Integer> favoriteIds = new ArrayList<>();
    private ApiInteraction apiInteraction;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_account, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        productManager = new ProductManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
        productViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ProductViewModel(productManager);
            }
        }).get(ProductViewModel.class);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
            return null;
        }

        StartComponents(view);

        helloUser.setText("Olá, " + user.getName());
        numberAccount.setText("Número Da Conta: " + user.getUserId());

        rvForYou.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvForYou.setNestedScrollingEnabled(false);

        forYouAdapter = new ProductAdapter(productList, user.getUserId(), R.layout.item_product, this, this);
        rvForYou.setAdapter(forYouAdapter);

        apiInteraction = RetrofitClient.getRetrofitInstance().create(ApiInteraction.class);
        interactionManager = new InteractionManager(apiInteraction);

        productViewModel.getProductsFavCategories(user.getUserId(), 6);
        productViewModel.verifProductsSaved(user.getUserId(), 1);

        return view;
    }

    @MainThread
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Produtos para "for you"
        productViewModel.getProductsFavCategoriesLiveData().observe(getViewLifecycleOwner(), products -> {
            productList.clear();
            productList.addAll(products);
            forYouAdapter.updateProducts(products);
            rvForYou.setVisibility(View.VISIBLE);

            forYouAdapter.notifyDataSetChanged();
        });

        // IDs dos produtos salvos
        productViewModel.getProductsSavedLiveData().observe(getViewLifecycleOwner(), ids -> {
            favoriteIds.clear();
            if (ids != null) {
                favoriteIds.addAll(ids);
            }

            forYouAdapter.setSavedIds(ids);
            forYouAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isVisible()) return;

        getActivity().findViewById(R.id.headerContainer).setVisibility(View.GONE);
        productViewModel.verifProductsSaved(user.getUserId(), 1);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void StartComponents(View view) {
        helloUser = view.findViewById(R.id.helloUser);
        numberAccount = view.findViewById(R.id.numberAccount);
        rvForYou = view.findViewById(R.id.rvForYou);
        buttonConfig = view.findViewById(R.id.buttonConfig);
        walletButton = view.findViewById(R.id.walletButton);
        userAvatar = view.findViewById(R.id.userAvatar);

        buttonConfig = view.findViewById(R.id.buttonConfig);
        buttonMyPurchases = view.findViewById(R.id.buttonMyPurchases);
        buttonMyAds = view.findViewById(R.id.buttonMyAds);

        ((BottomNavVisibilityController) requireActivity()).showBottomNav();

        Utils.loadImageBasicAuth(userAvatar, user.getAvatar());

        buttonConfig.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(50).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });

        buttonMyPurchases.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(50).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });

        buttonMyAds.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(50).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });

        buttonConfig.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_accountFragment_to_userConfigFragment);
        });

        buttonMyPurchases.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_account_to_ordersFragment);
        });

        buttonMyAds.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_accountFragment_to_myAdsFragment);
        });

        walletButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_account_to_WalletFragment);
        });
    }

    @Override
    public void onSaveChanged() {
        forYouAdapter.setSavedIds(favoriteIds);
        forYouAdapter.notifyDataSetChanged();
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

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_accountFragment_to_productDetailsFragment, bundle);
    }
}
