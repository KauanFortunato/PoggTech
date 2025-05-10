package com.mordekai.poggtech.ui.fragments;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.mordekai.poggtech.ui.activity.LoginActivity;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAccountFragment extends Fragment
        implements ProductAdapter.OnProductClickListener,
        ProductAdapter.OnSavedChangedListener {
    private TextView helloUser, numberAccount;
    private ImageButton buttonConfig, buttonMyPurchases, buttonMyAds;
    private FirebaseUser currentUser;
    private BottomNavigationView bottomNavigationView;
    private SharedPrefHelper sharedPrefHelper;
    private InteractionManager interactionManager;
    private User user;
    private RecyclerView rvForYou;
    private ProductAdapter forYouAdapter;
    private List<Product> productList = new ArrayList<>();
    private ApiProduct apiProduct;
    private ProductManager productManager;
    private List<Integer> favoriteIds = new ArrayList<>();
    private ApiInteraction apiInteraction;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_account, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
            return null;
        }

        StartComponents(view);

        helloUser.setText("Olá, " + user.getName());
        numberAccount.setText("Número Da Conta: " + user.getUserId());

        setLayout();

        rvForYou.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvForYou.setNestedScrollingEnabled(false);

        forYouAdapter = new ProductAdapter(productList, user.getUserId(), R.layout.item_product, this, this);
        rvForYou.setAdapter(forYouAdapter);

        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);

        apiInteraction = RetrofitClient.getRetrofitInstance().create(ApiInteraction.class);
        interactionManager = new InteractionManager(apiInteraction);

        getForYou();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.headerContainer).setVisibility(View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void StartComponents(View view) {
        helloUser = view.findViewById(R.id.helloUser);
        numberAccount = view.findViewById(R.id.numberAccount);
        rvForYou = view.findViewById(R.id.rvForYou);

        buttonConfig = view.findViewById(R.id.buttonConfig);
        buttonMyPurchases = view.findViewById(R.id.buttonMyPurchases);
        buttonMyAds = view.findViewById(R.id.buttonMyAds);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

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
            UserConfigFragment userConfigFragment = new UserConfigFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.containerFrame, userConfigFragment)
                    .addToBackStack(null) // Permite voltar com o botão de voltar do sistema
                    .commit();
        });

        buttonMyAds.setOnClickListener(v -> {
            MyAdsFragment myAdsFragment = new MyAdsFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.containerFrame, myAdsFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void getForYou() {
        productManager.getProductsFavCategories(user.getUserId(), 10, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                productList.clear();
                productList.addAll(result);
                forYouAdapter.updateProducts(result);
                Log.d("DEBUG_PRODUCTS", "Produtos recebidos: " + result.size());

                productManager.fetchUserFavOrCart(user.getUserId(), 1, new RepositoryCallback<List<Integer>>() {
                    @Override
                    public void onSuccess(List<Integer> favorites) {
                        favoriteIds.clear();
                        favoriteIds.addAll(favorites);
                        forYouAdapter.setSavedIds(favoriteIds);
                        forYouAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("API_RESPONSE", "Erro ao buscar produtos favoritos", t);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos", t);
            }
        });

    }

    private void setLayout() {}

    @Override
    public void onSaveChanged() {
        forYouAdapter.setSavedIds(favoriteIds);
        forYouAdapter.notifyDataSetChanged();
    }

    @Override
    public void onProductClick(Product product) {
        Bundle bundle = new Bundle();
        bundle.putInt("productId", product.getProduct_id());

        ProductDetailsFragment fragment = new ProductDetailsFragment();
        fragment.setArguments(bundle);

        interactionManager.userInteraction(product.getProduct_id(), user.getUserId(), "view",new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("API_RESPONSE", "Interaction result: " + result);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Error in userInteraction", t);
            }
        });

        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                )
                .replace(R.id.containerFrame, fragment)
                .addToBackStack(null)
                .commit();
    }
}
