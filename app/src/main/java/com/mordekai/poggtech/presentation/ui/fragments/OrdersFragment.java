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
import androidx.navigation.fragment.NavHostFragment;
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
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.InteractionManager;
import com.mordekai.poggtech.domain.OrderManager;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements ProductAdapter.OnProductClickListener,
        ProductAdapter.OnSavedChangedListener {

    private List<Order> orders = new ArrayList<>();
    private OrdersAdapter ordersAdapter;
    private ApiOrder apiOrder;
    private OrderManager orderManager;
    private User user;
    private SharedPrefHelper sharedPrefHelper;
    private RecyclerView rvOrders, rvForYou;
    private ProductManager productManager;
    private List<Integer> favoriteIds = new ArrayList<>();
    private ProductAdapter forYouAdapter;
    private InteractionManager interactionManager;
    private AppCompatImageView btnBack;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        apiOrder = RetrofitClient.getRetrofitInstance().create(ApiOrder.class);
        orderManager = new OrderManager(apiOrder);

        startComponents(view);

        ordersAdapter = new OrdersAdapter(orders);
        rvOrders = view.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvOrders.setNestedScrollingEnabled(true);
        rvOrders.setAdapter(ordersAdapter);

        productManager = new ProductManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));

        interactionManager = new InteractionManager(RetrofitClient.getRetrofitInstance().create(ApiInteraction.class));

        rvForYou.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvForYou.setNestedScrollingEnabled(false);

        forYouAdapter = new ProductAdapter(new ArrayList<>(), user.getUserId(), R.layout.item_product_match_parent, this, this);
        rvForYou.setAdapter(forYouAdapter);

        getOrders();
        getForYou();
        return view;
    }

    private void getOrders() {
        orderManager.GetOrders(user.getUserId(), new RepositoryCallback<List<Order>>() {

            @Override
            public void onSuccess(List<Order> result) {
                orders = result;
                ordersAdapter.updateOrders(orders);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar pedidos", t);
            }
        });
    }

    private void getForYou() {
        productManager.getProductsFavCategories(user.getUserId(), 4, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                forYouAdapter.updateProducts(result);
                Log.d("DEBUG_PRODUCTS", "Produtos recebidos: " + result.size());

                productManager.verifProductsSaved(user.getUserId(), 1, new RepositoryCallback<List<Integer>>() {
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

    private void startComponents(View view) {
        rvForYou = view.findViewById(R.id.rvForYou);
        btnBack = view.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> {
            if (btnBack.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            NavController navController = NavHostFragment.findNavController(this);
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

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_save_to_productDetailsFragment, bundle);
    }

    @Override
    public void onSaveChanged() {
        forYouAdapter.setSavedIds(favoriteIds);
        forYouAdapter.notifyDataSetChanged();
    }
}
