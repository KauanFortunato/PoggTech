package com.mordekai.poggtech.presentation.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.CartProductAdapter;
import com.mordekai.poggtech.data.adapter.ProductAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.OrderRequest;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiInteraction;
import com.mordekai.poggtech.data.remote.ApiOrder;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;
import com.mordekai.poggtech.domain.InteractionManager;
import com.mordekai.poggtech.domain.OrderManager;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.ProductLoader;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.SnackbarUtil;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartProductAdapter.OnProductClickListener,
        CartProductAdapter.OnProductChangeQuantityListener, ProductAdapter.OnProductClickListener,
        ProductAdapter.OnSavedChangedListener {

    private SharedPrefHelper sharedPrefHelper;
    private User user;
    private CartProductAdapter cartProductAdapter;
    private RecyclerView rvItemsCart, rvForYou;
    private ApiProduct apiProduct;
    private ProductManager productManager;
    private CartManager cartManager;
    private List<Product> productList;
    private List<Product> productListForY;
    private TextView totalPrice, subtotal;
    private FrameLayout emptyCart, finishOrder;
    private LinearLayout containerBuy, forYouContainer;
    private List<Integer> favoriteIds = new ArrayList<>();
    private ProductAdapter forYouAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerLayout;
    private AppCompatButton buy;
    private ProgressBar buttonProgress;
    private boolean buying = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        initComponents(view);

        // Recycler View
        productList = new ArrayList<>();
        productListForY = new ArrayList<>();

        cartProductAdapter = new CartProductAdapter(productList, user.getUserId(), this, this, getChildFragmentManager());
        rvItemsCart.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvItemsCart.setNestedScrollingEnabled(false);
        rvItemsCart.setAdapter(cartProductAdapter);

        // Iniciar API e gerenciador de produtos
        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);
        cartManager = new CartManager(apiProduct);

        swipeRefreshLayout.setOnRefreshListener(this::fetchCartProducts);


        // Adapter de produtos recomendados
        rvForYou.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvForYou.setNestedScrollingEnabled(false);

        forYouAdapter = new ProductAdapter(productListForY, user.getUserId(), R.layout.item_product_match_parent, this, this);
        rvForYou.setAdapter(forYouAdapter);

        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);

        // Carregar produtos
        fetchCartProducts();
        ProductLoader.loadForYouProducts(
                productManager,
                user.getUserId(),
                forYouAdapter,
                productListForY,
                favoriteIds,
                4,
                1,
                () -> {}
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        fetchCartProducts();
    }

    // Tipo 0 = Carrinho
    private void fetchCartProducts() {
        cartManager.fetchCartProducts(user.getUserId(), 0, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                rvItemsCart.postDelayed(() -> {
                    shimmerLayout.setVisibility(View.GONE);
                    shimmerLayout.stopShimmer();
                    swipeRefreshLayout.setRefreshing(false);

                    productList.clear();

                    if (products.isEmpty()) {
                        rvItemsCart.setVisibility(View.GONE);
                        containerBuy.setVisibility(View.GONE);
                        forYouContainer.setVisibility(View.VISIBLE);
                        finishOrder.setVisibility(View.GONE);
                        emptyCart.setVisibility(View.VISIBLE);

                        Log.d("API_RESPONSE", "Nenhum produto encontrado");
                    } else {
                        productList.addAll(products);
                        cartProductAdapter.notifyDataSetChanged();

                        rvItemsCart.setVisibility(View.VISIBLE);
                        emptyCart.setVisibility(View.GONE);
                        containerBuy.setVisibility(View.VISIBLE);
                        forYouContainer.setVisibility(View.VISIBLE);
                        finishOrder.setVisibility(View.GONE);

                        Log.d("API_RESPONSE", "Item 0: " + productList.get(0).getTitle());
                    }

                    updatePrice();
                }, 300);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar produtos", t);
                rvItemsCart.postDelayed(() -> {
                    shimmerLayout.setVisibility(View.GONE);
                    shimmerLayout.stopShimmer();
                    swipeRefreshLayout.setRefreshing(false);

                    rvItemsCart.setVisibility(View.GONE);
                    emptyCart.setVisibility(View.VISIBLE);
                    containerBuy.setVisibility(View.GONE);
                    finishOrder.setVisibility(View.GONE);
                    forYouContainer.setVisibility(View.VISIBLE);

                }, 300);
            }
        });
    }

    private void updatePrice() {
        double total = 0.00;
        int quantityCart = 0;

        for (Product product : productList) {
            total += product.getPrice() * product.getQuantity();
            quantityCart += product.getQuantity();
        }

        totalPrice.setText(String.format("€%.2f", total));
        subtotal.setText(String.format("SUBTOTAL(%d ARTIGO(S))", quantityCart));
    }

    private void initComponents(View view) {
        // Componentes
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        rvItemsCart = view.findViewById(R.id.rvItemsCart);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        emptyCart = view.findViewById(R.id.emptyCart);
        totalPrice = view.findViewById(R.id.totalPrice);
        subtotal = view.findViewById(R.id.subtotal);
        buy = view.findViewById(R.id.buy);
        containerBuy = view.findViewById(R.id.containerBuy);
        rvForYou = view.findViewById(R.id.rvForYou);
        forYouContainer = view.findViewById(R.id.forYouContainer);
        finishOrder = view.findViewById(R.id.finishOrder);
        buttonProgress = view.findViewById(R.id.buttonProgress);

        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();

        buy.setOnClickListener(v -> showOrderInfoBottomSheet());

    }

    private OrderRequest buildOrderRequest(String location, String name, String phone) {
        List<OrderRequest.OrderItem> orderItems = new ArrayList<>();

        for (Product p : productList) {
            orderItems.add(new OrderRequest.OrderItem(
                    p.getProduct_id(),
                    p.getQuantity(),
                    p.getPrice()
            ));
        }

        return new OrderRequest(user.getUserId(), location, name, phone, orderItems);
    }


    @SuppressLint("NotifyDataSetChanged")
    private void finishOrder() {
        productList.clear();
        cartProductAdapter.notifyDataSetChanged();
        finishOrder.setVisibility(View.VISIBLE);
        containerBuy.setVisibility(View.GONE);
        emptyCart.setVisibility(View.GONE);
    }

    private void showOrderInfoBottomSheet() {
        View sheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_order_info, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(sheetView);

        EditText etLocation = sheetView.findViewById(R.id.etLocation);
        EditText etUserName = sheetView.findViewById(R.id.etUserName);
        EditText etPhone = sheetView.findViewById(R.id.etPhone);
        AppCompatButton btnSubmit = sheetView.findViewById(R.id.btnSubmitOrder);

        btnSubmit.setOnClickListener(v -> {
            String location = etLocation.getText().toString().trim();
            String name = etUserName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (location.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                SnackbarUtil.showErrorSnackbar(sheetView, "Preencha todos os campos", getContext());
                return;
            }

            OrderRequest request = buildOrderRequest(location, name, phone);
            bottomSheetDialog.dismiss();
            submitOrder(request);
        });

        bottomSheetDialog.show();
    }

    private void submitOrder(OrderRequest request) {
        buttonProgress.setVisibility(View.VISIBLE);
        buy.setText("");

        ApiOrder apiOrder = RetrofitClient.getRetrofitInstance().create(ApiOrder.class);
        OrderManager orderManager = new OrderManager(apiOrder);

        orderManager.RegisterOrder(request, new RepositoryCallback<ApiResponse<Integer>>() {
            @Override
            public void onSuccess(ApiResponse<Integer> result) {
                if (result.isSuccess()) {
                    buying = true;
                    finishOrder();
                    buy.setText(getString(R.string.checkout));
                    buttonProgress.setVisibility(View.GONE);
                    SnackbarUtil.showSuccessSnackbar(buy.getRootView(), "Compra realizada com sucesso!", getContext());
                    Log.d("API_RESPONSE", result.getMessage());
                } else {
                    Log.e("API_RESPONSE", result.getMessage());
                    buttonProgress.setVisibility(View.GONE);
                    buy.setText(getString(R.string.checkout));
                    SnackbarUtil.showErrorSnackbar(buy.getRootView(), "Erro ao fazer compra, verifique sua carteira", getContext());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao registrar compra", t);
                buttonProgress.setVisibility(View.GONE);
                buy.setText(getString(R.string.checkout));
                SnackbarUtil.showErrorSnackbar(buy.getRootView(), "Erro ao conectar com servidor", getContext());
            }
        });
    }

    @Override
    public void onProductChangeQuantity(Product product) {
        updatePrice();
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

        InteractionManager interactionManager = new InteractionManager(RetrofitClient.getRetrofitInstance().create(ApiInteraction.class));
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
}
