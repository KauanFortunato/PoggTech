package com.mordekai.poggtech.ui.fragments;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ProductApi;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import okhttp3.ResponseBody;

public class ProductDetailsFragment extends Fragment {

    private TextView titleProduct, category, price, priceDecimal, discount, priceBefore, productPoggers;
    private AppCompatButton actionPrimaryProduct;
    private ImageButton favoriteButton;
    private View skeletonView, contentView;
    private ImageView productImage;
    private CartManager cartManager;
    private ProductManager productManager;
    private ProductApi productApi;
    private SharedPrefHelper sharedPrefHelper;
    private Boolean isFavorite;
    private User user;
    private int productId;

    private BottomNavigationView bottomNavigationView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        // Obtemos as referências das Views
        skeletonView = view.findViewById(R.id.skeletonView);
        animateContentView(skeletonView);
        contentView = view.findViewById(R.id.contentView);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();
        cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ProductApi.class));

        if (getArguments() != null) {
            productId = getArguments().getInt("productId");
        }

        startComponents(contentView);
        verifyProductIsFavorite(productId, user.getUserId(), 1);

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
        }, 400);
    }

    @SuppressLint("DefaultLocale")
    private void updateUIWithProduct(Product product) {
        float priceTotal = product.getPrice() != null ? product.getPrice() : 0f;
        int integerPart = (int) priceTotal;
        int cents = Math.round((priceTotal - integerPart) * 100);

        titleProduct.setText(product.getTitle());
        category.setText(product.getCategory());
        price.setText(String.valueOf(integerPart));
        priceDecimal.setText(String.format("%02d€", cents));
        Log.d("ProductDetailsFragment", "Logs: " + product.getDiscountPercentage());

        if (product.getDiscountPercentage() != null && product.getDiscountPercentage() > 0) {
            priceBefore.setText(String.format("%s %.2f€", getString(R.string.antes),
                    product.getPriceBefore() != null ? product.getPriceBefore() : 0f));
            discount.setText(String.format("-%d%%",
                    product.getDiscountPercentage() != null ? product.getDiscountPercentage().intValue() : 0));
        } else {
            priceBefore.setVisibility(View.GONE);
            discount.setVisibility(View.GONE);
        }

        Glide.with(productImage.getContext())
                .load(product.getImage_url())
                .into(productImage);
    }

    private void updateFavoriteButton(boolean isFavorite) {
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite);
        }
    }

    private void addToFavorites(int productId) {
        cartManager.addToCart(productId, user.getUserId(), 1, new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                isFavorite = true;
                updateFavoriteButton(true);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ProductDetailsFragment", "Erro ao adicionar aos favoritos", t);
            }
        });
    }

    private void removeFromFavorites(int productId) {
        cartManager.removeFromCart(productId, user.getUserId(), 1, new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                isFavorite = false;
                updateFavoriteButton(false);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ProductDetailsFragment", "Erro ao remover dos favoritos", t);
            }
        });
    }

    private void addToCart(int productId) {
        cartManager.addToCart(productId, user.getUserId(), 0, new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                isFavorite = true;
                updateFavoriteButton(true);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ProductDetailsFragment", "Erro ao adicionar aos favoritos", t);
            }
        });
    }

    private void verifyProductIsFavorite(int productId, int userId, int tipo) {
        Log.d("ProductDetailsFragment", "Logs: " + productId + " " + userId + " " + tipo + "");
        cartManager.verifyProductOnCart(productId, userId, tipo, new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isFavorite = result;
                updateFavoriteButton(result);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ProductDetailsFragment", "Erro ao verificar produto no carrinho", t);
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
        actionPrimaryProduct = view.findViewById(R.id.actionPrimaryProduct);

        favoriteButton.setOnClickListener(v -> {
            if(favoriteButton.isHapticFeedbackEnabled()) {
                favoriteButton.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            if(isFavorite) {
                removeFromFavorites(productId);
            } else {
                addToFavorites(productId);
            }
        });

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

    private void animateContentView(View view) {
        view.setTranslationY(300f);
        view.setAlpha(0f);

        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(400)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        view.setVisibility(View.VISIBLE);
    }
}
