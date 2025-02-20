package com.mordekai.poggtech.ui.fragments;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ProductApi;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ProductDetailsFragment extends Fragment {

    private TextView titleProduct, category, price, priceDecimal, discount, priceBefore, productPoggers;
    private ImageButton favoriteButton;
    private View skeletonView, contentView;
    private ImageView productImage;
    private ProductManager productManager;
    private ProductApi productApi;
    private SharedPrefHelper sharedPrefHelper;
    private User user;
    private int productId;

    private BottomNavigationView bottomNavigationView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        // Obtemos as referências das Views
        skeletonView = view.findViewById(R.id.skeletonView);
        contentView = view.findViewById(R.id.contentView);

        sharedPrefHelper = new SharedPrefHelper(requireContext());

        if (getArguments() != null) {
            productId = getArguments().getInt("productId");
        }

        startComponents(contentView);

        productApi = RetrofitClient.getRetrofitInstance().create(ProductApi.class);
        productManager = new ProductManager(productApi);

        // Iniciar animação do Skeleton
        animateSkeleton(skeletonView);

        // Buscar os detalhes do produto
        fetchProduct(productId);

        return view;
    }

    private void fetchProduct(int productId) {
        new android.os.Handler().postDelayed(() -> {
            productManager.fetchProductById(productId, new RepositoryCallback<Product>() {
                @Override
                public void onSuccess(Product product) {
                    if (product != null) {
                        updateUIWithProduct(product);

                        // Esconder Skeleton e mostrar conteúdo real
                        skeletonView.setVisibility(View.GONE);
                        contentView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("ProductDetailsFragment", "Erro ao buscar detalhes do produto", t);
                    skeletonView.setVisibility(View.GONE);
                    contentView.setVisibility(View.VISIBLE);
                }
            });
        }, 500);
    }

    private void updateUIWithProduct(Product product) {
        float priceTotal = product.getPrice();
        int integerPart = (int) priceTotal;
        int cents = Math.round((priceTotal - integerPart) * 100);

        titleProduct.setText(product.getTitle());
        category.setText(product.getCategory());
        price.setText(String.valueOf(integerPart));
        priceDecimal.setText(String.format("%02d€", cents));

        Glide.with(productImage.getContext())
                .load(product.getImage_url())
                .into(productImage);
    }

    private void startComponents(View view) {
        titleProduct = view.findViewById(R.id.titleProduct);
        category = view.findViewById(R.id.category);
        favoriteButton = view.findViewById(R.id.favoriteButton);
        price = view.findViewById(R.id.price);
        priceDecimal = view.findViewById(R.id.priceDecimal);
        discount = view.findViewById(R.id.discount);
        priceBefore = view.findViewById(R.id.priceBefore);
        productPoggers = view.findViewById(R.id.productPoggers);
        productImage = view.findViewById(R.id.productImage);

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
        getActivity().findViewById(R.id.btnBackHeader).setVisibility(View.VISIBLE);
    }

    private void animateSkeleton(View view) {
        for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++) {
            View child = ((ViewGroup)view).getChildAt(i);
            ObjectAnimator animator = ObjectAnimator.ofFloat(child, "alpha", 0.5f, 1f);
            animator.setDuration(1000);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();
        }
    }
}
