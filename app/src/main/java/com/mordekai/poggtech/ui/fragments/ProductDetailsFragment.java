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

import android.os.Bundle;
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
    private ImageView productImage;
    private ProductManager productManager;
    private ProductApi productApi;
    private SharedPrefHelper sharedPrefHelper;
    private User user;
    private int productId;

    private BottomNavigationView bottomNavigationView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        if (getArguments() != null) {
            productId = getArguments().getInt("productId");
        }
        startComponents(view);

        productApi = RetrofitClient.getRetrofitInstance().create(ProductApi.class);
        productManager = new ProductManager(productApi);
        fetchProduct(productId);

        return view;
    }

    private void fetchProduct(int productId) {
        productManager.fetchProductById(productId, new RepositoryCallback<Product>() {
            @Override
            public void onSuccess(Product product) {
                if (product != null) {
                    float priceTotal = product.getPrice();
                    int integerPart = (int) priceTotal;
                    float decimalPart = priceTotal - integerPart;

                    int cents = Math.round(decimalPart * 100);

                    titleProduct.setText(product.getTitle());
                    category.setText(product.getCategory());
                    price.setText(String.valueOf(integerPart));
                    priceDecimal.setText(String.format("%02d€", cents));
                    String imageUrl = product.getImage_url();

                    Glide.with(productImage.getContext())
                            .load(imageUrl)
                            .into(productImage);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Handle failure
            }
        });
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
}
