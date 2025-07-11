package com.mordekai.poggtech.presentation.ui.fragments;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.GalleryAdapter;
import com.mordekai.poggtech.data.adapter.ProductAdapter;
import com.mordekai.poggtech.data.adapter.ReviewAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Chat;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.ProductWithUser;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiInteraction;
import com.mordekai.poggtech.data.remote.ApiMessage;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.ApiReview;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;
import com.mordekai.poggtech.domain.InteractionManager;
import com.mordekai.poggtech.domain.MessageManager;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.domain.ReviewManager;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.presentation.ui.bottomsheets.ProductAddedBottomSheet;
import com.mordekai.poggtech.presentation.viewmodel.ReviewViewModel;
import com.mordekai.poggtech.utils.ProductLoader;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.SnackbarUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.mordekai.poggtech.presentation.viewmodel.MessageViewModel;
import com.mordekai.poggtech.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class ProductDetailsFragment extends Fragment {

    private TextView titleProduct, category, price, priceDecimal, discount, priceBefore,
            productRating1, productRating2, reviewCount, noReviews;
    private EditText inputText;
    private LinearLayout contactSellerContainer, containerReviews, containerBuy;
    private LinearLayout starsContainer, starsContainer2;
    private RecyclerView rvReviews;
    private AppCompatButton actionButton;
    private final List<Product> productList = new ArrayList<>();
    private final List<Integer> favoriteIds = new ArrayList<>();
    private ImageButton btnSend;
    private AppCompatImageView saveButton, minusProduct, plusProduct;
    private TextView quantityProduct;
    private View contentView;
    private ViewPager2 viewPagerGallery;
    private GalleryAdapter galleryAdapter;
    private TabLayout tabLayoutDots;
    List<String> imageUrls = new ArrayList<>();
    private CartManager cartManager;
    private ProductManager productManager;
    private MessageManager messageManager;
    private ReviewManager reviewManager;
    private ReviewAdapter reviewAdapter;
    private Boolean isSaved;
    private Chat chatProduct;
    private User user;
    private Product product;
    private User sellerUser;
    private MessageViewModel messageViewModel;
    private ReviewViewModel reviewViewModel;
    private int productId;
    private int quantityAdd = 1;
    private ShimmerFrameLayout shimmerLayout;


    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        ((MainActivity) requireActivity()).setForceBackToHome(false);

        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
        messageManager = new MessageManager(RetrofitClient.getRetrofitInstance().create(ApiMessage.class));

        MessageManager msgMgr = new MessageManager(RetrofitClient.getRetrofitInstance().create(ApiMessage.class));
        messageViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MessageViewModel(msgMgr);
            }
        }).get(MessageViewModel.class);

        ReviewManager rvMgr = new ReviewManager(RetrofitClient.getRetrofitInstance().create(ApiReview.class));
        reviewViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ReviewViewModel(rvMgr);
            }
        }).get(ReviewViewModel.class);


        if (getArguments() != null) {
            productId = getArguments().getInt("productId");
        }

        startComponents(view);
        verifyProductIsSaved(productId, user.getUserId(), 1);

        ApiProduct apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);
        reviewManager = new ReviewManager(RetrofitClient.getRetrofitInstance().create(ApiReview.class));

        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvReviews.setAdapter(reviewAdapter);

        galleryAdapter = new GalleryAdapter(requireActivity(), imageUrls);
        viewPagerGallery.setAdapter(galleryAdapter);

        viewPagerGallery.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < tabLayoutDots.getTabCount(); i++) {
                    TabLayout.Tab tab = tabLayoutDots.getTabAt(i);
                    if (tab != null && tab.getCustomView() != null) {
                        tab.getCustomView().setBackgroundResource(
                                i == position ? R.drawable.tab_indicator_selected : R.drawable.tab_indicator_default
                        );
                    }
                }
            }
        });

        // Buscar os detalhes do produto
        Log.d("ProductDetailsFragment", "productId: " + productId);
        fetchProduct(productId, view);
        getReviews(productId);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int productId = getArguments().getInt("productId", -1);
        if (productId != -1) {
            fetchProduct(productId, view);
            getReviews(productId);
        }
    }

    private void getReviews(int productId) {
        reviewViewModel.loadReviews(productId, 4);
    }

    private void startUser(View view) {
        AppCompatImageView userAvatar = view.findViewById(R.id.userAvatar);
        TextView userName = view.findViewById(R.id.userName);
        TextView userCreateDate = view.findViewById(R.id.userCreateDate);

        Utils.loadImageBasicAuth(userAvatar, sellerUser.getAvatar());
        userName.setText(sellerUser.getName());
        userCreateDate.setText(String.format("%s %s", getString(R.string.in_app_since), sellerUser.getCreatedAt()));
    }

    private void fetchProduct(int productId, View view) {
        productManager.fetchProductById(productId, new RepositoryCallback<ProductWithUser>() {
            @Override
            public void onSuccess(ProductWithUser productWithUser) {
                if (productWithUser != null) {
                    product = productWithUser.getProduct();
                    sellerUser = productWithUser.getUser();
                    if (sellerUser != null) {
                        startUser(view);
                    }
                    updateUIWithProduct(view);

                    productManager.getProductImages(productId, new RepositoryCallback<List<String>>() {
                        @Override
                        public void onSuccess(List<String> result) {
                            if (result != null && !result.isEmpty()) {
                                product.setImages(result);
                                imageUrls.clear();
                                imageUrls.addAll(result);
                            } else {
                                if (product.getCover() != null) {
                                    imageUrls.add(product.getCover());
                                }
                            }

                            // Galeria de imagens
                            galleryAdapter.updateImages(imageUrls);

                            new TabLayoutMediator(tabLayoutDots, viewPagerGallery, (tab, position) -> {
                            }).attach();

                            for (int i = 0; i < tabLayoutDots.getTabCount(); i++) {
                                TabLayout.Tab tab = tabLayoutDots.getTabAt(i);
                                if (tab != null) {
                                    View tabView = new View(requireContext());
                                    int size = (int) (getResources().getDisplayMetrics().density * 8); // corrigir: multiplicar
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                                    tabView.setLayoutParams(params);
                                    tabView.setBackgroundResource(i == 0 ? R.drawable.tab_indicator_selected : R.drawable.tab_indicator_default);
                                    tab.setCustomView(tabView);
                                }
                            }


                            shimmerLayout.setVisibility(View.GONE);
                            shimmerLayout.stopShimmer();

                            contentView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e("ProductDetailsFragment", "Erro ao buscar imagens do produto", t);
                            imageUrls.add(product.getCover());
                            galleryAdapter.updateImages(imageUrls);

                            shimmerLayout.setVisibility(View.GONE);
                            shimmerLayout.stopShimmer();

                            contentView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ProductDetailsFragment", "Erro ao buscar detalhes do produto", t);
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmer();

                contentView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void sendMessage() {
        messageViewModel.loadChat(user.getUserId(), productId);
    }

    private void createNewChat() {
        messageViewModel.createChat(productId, product.getUser_id(), user.getUserId());
    }

    private void sendMessageToChat() {
        if (chatProduct != null) {
            String text = inputText.getText().toString();
            messageViewModel.sendMessage(
                    user.getUserId(),
                    product.getUser_id(),
                    chatProduct.getChatId(),
                    text
            );

            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.chat);
        }
    }

    private void startComponents(View view) {
        titleProduct = view.findViewById(R.id.titleProduct);
        category = view.findViewById(R.id.category);
        saveButton = view.findViewById(R.id.saveButton);
        price = view.findViewById(R.id.price);
        priceDecimal = view.findViewById(R.id.priceDecimal);
        discount = view.findViewById(R.id.discount);
        priceBefore = view.findViewById(R.id.priceBefore);
        actionButton = view.findViewById(R.id.actionPrimaryProduct);
        inputText = view.findViewById(R.id.inputText);
        contactSellerContainer = view.findViewById(R.id.contactSellerContainer);
        btnSend = view.findViewById(R.id.btnSend);
        viewPagerGallery = view.findViewById(R.id.viewPagerGallery);
        tabLayoutDots = view.findViewById(R.id.tabLayoutDots);
        productRating1 = view.findViewById(R.id.productRating1);
        productRating2 = view.findViewById(R.id.productRating2);
        reviewCount = view.findViewById(R.id.reviewCount);
        noReviews = view.findViewById(R.id.noReviews);
        containerReviews = view.findViewById(R.id.containerReviews);
        containerBuy = view.findViewById(R.id.containerBuy);
        starsContainer = view.findViewById(R.id.starsContainer);
        starsContainer2 = view.findViewById(R.id.starsContainer2);
        rvReviews = view.findViewById(R.id.rvReviews);
        contentView = view.findViewById(R.id.contentView);
        shimmerLayout = view.findViewById(R.id.shimmerLayout);

        quantityProduct = view.findViewById(R.id.quantityProduct);
        minusProduct = view.findViewById(R.id.minusProduct);
        plusProduct = view.findViewById(R.id.plusProduct);

        AppCompatImageView shareButton = view.findViewById(R.id.shareButton);

        shareButton.setOnClickListener(v -> {
            shareProduct(product.getProduct_id());
        });

        quantityProduct.setText(String.valueOf(quantityAdd));

        minusProduct.setOnClickListener(v -> {
            if (quantityAdd > 1) {
                quantityAdd--;
                quantityProduct.setText(String.valueOf(quantityAdd));
            }
        });

        plusProduct.setOnClickListener(v -> {
            if (quantityAdd < product.getQuantity()) {
                quantityAdd++;
                quantityProduct.setText(String.valueOf(quantityAdd));
            } else {
                SnackbarUtil.showErrorSnackbar(getView(), "Quantidade máxima atingida", getContext());
            }
        });

        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();

        saveButton.setOnClickListener(v -> {
            if (saveButton.isHapticFeedbackEnabled()) {
                saveButton.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            if (isSaved) {
                removeFromSaved(productId);
            } else {
                addToSaved(productId);
            }
        });

        actionButton.setOnClickListener(v -> {
            if (actionButton.isHapticFeedbackEnabled()) {
                actionButton.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            addToCart(productId);
        });

        ((MainActivity) getActivity()).showBackButton();
        setupObservers();
    }

    @SuppressLint("DefaultLocale")
    private void updateUIWithProduct(View view) {
        Log.d("ProductDetailsFragment", "Logs: " + product.getUser_id() + " " + productId);

        float priceTotal = product.getPrice() != null ? product.getPrice() : 0f;
        int integerPart = (int) priceTotal;
        int cents = Math.round((priceTotal - integerPart) * 100);

        titleProduct.setText(product.getTitle());
        category.setText(product.getCategory());

        price.setText(String.valueOf(integerPart));
        priceDecimal.setText(String.format("%02d€", cents));
        productRating1.setText(String.valueOf(product.getRating()));
        productRating2.setText(String.valueOf(product.getRating()));

        setRatingStars(starsContainer, product.getRating());
        setRatingStars(starsContainer2, product.getRating());

        TextView descriptionText = view.findViewById(R.id.descriptionText);
        descriptionText.setText(product.getDescription());

        AppCompatImageView icUp = view.findViewById(R.id.icUp);

        LinearLayout descriptionTitle = view.findViewById(R.id.descriptionTitle);
        descriptionTitle.setOnClickListener(v -> {
            if (descriptionText.isHapticFeedbackEnabled()) {
                descriptionText.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            if (descriptionText.getVisibility() == View.GONE) {
                descriptionText.setVisibility(View.VISIBLE);
                descriptionText.setAlpha(0f);
                descriptionText.animate().alpha(1f).setDuration(300).start();
                icUp.setImageResource(R.drawable.ic_down);
            } else {
                descriptionText.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                    descriptionText.setVisibility(View.GONE);
                }).start();
                icUp.setImageResource(R.drawable.ic_up);
            }
        });

        if (product.isPoggers()) {
            view.findViewById(R.id.containerSeller).setVisibility(View.VISIBLE);
            view.findViewById(R.id.containerReviewsAll).setVisibility(View.VISIBLE);
            view.findViewById(R.id.containerUser).setVisibility(View.GONE);

            TextView stock = view.findViewById(R.id.stock);
            stock.setText(String.format("Em estoque: %d", product.getQuantity()));

            if (product.getQuantity() <= 10) {
                stock.setTextColor(getResources().getColor(R.color.colorError));
            } else {
                if(product.getQuantity() <= 20) {
                    stock.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        } else {
            view.findViewById(R.id.productRatingContainer).setVisibility(View.GONE);
            view.findViewById(R.id.containerReviewsAll).setVisibility(View.GONE);
        }

        if (product.getReviewCount() == 0) {
            reviewCount.setVisibility(View.GONE);
            containerReviews.setVisibility(View.GONE);
            noReviews.setVisibility(View.VISIBLE);
        } else {
            reviewCount.setVisibility(View.VISIBLE);
            containerReviews.setVisibility(View.VISIBLE);
            noReviews.setVisibility(View.GONE);
            reviewCount.setText(String.valueOf(product.getReviewCount()));
        }

        // Verifica o dono do produto, para não deixar o próprio vendedor comprar o produto dele
        if (product.getUser_id() != user.getUserId()) {
            if (product.isPoggers()) {
                actionButton.setVisibility(View.VISIBLE);
                contactSellerContainer.setVisibility(View.GONE);
                actionButton.setEnabled(true);
                actionButton.setBackgroundResource(R.drawable.rounded_button);
            } else {
                containerBuy.setVisibility(View.GONE);
                contactSellerContainer.setVisibility(View.VISIBLE);
            }
        } else {
            containerBuy.setVisibility(View.GONE);
            actionButton.setBackgroundResource(R.drawable.bg_button_unable);
            actionButton.setVisibility(View.VISIBLE);
            actionButton.setEnabled(false);
            contactSellerContainer.setVisibility(View.GONE);
        }

        if (product.getDiscountPercentage() != null && product.getDiscountPercentage() > 0) {
            priceBefore.setText(String.format("%s %.2f€", getString(R.string.before),
                    product.getPriceBefore() != null ? product.getPriceBefore() : 0f));
            discount.setText(String.format("-%d%%",
                    product.getDiscountPercentage() != null ? product.getDiscountPercentage().intValue() : 0));
        } else {
            priceBefore.setVisibility(View.GONE);
            discount.setVisibility(View.GONE);
        }

        btnSend.setOnClickListener(v -> {
            if (!inputText.getText().toString().isEmpty()) {
                if (btnSend.isHapticFeedbackEnabled()) {
                    btnSend.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                }
                sendMessage();
            } else {
                if (btnSend.isHapticFeedbackEnabled()) {
                    btnSend.performHapticFeedback(HapticFeedbackConstants.REJECT);
                }
                inputText.setError("Escreva uma mensagem");
            }
        });

    }

    private void updateSaveButton(boolean isSaved) {
        if (isSaved) {
            saveButton.setImageResource(R.drawable.ic_bookmark_fill);
        } else {
            saveButton.setImageResource(R.drawable.ic_bookmark);
        }
    }

    private void setupObservers() {
        messageViewModel.getCurrentChat().observe(getViewLifecycleOwner(), chat -> {
            if (chat != null) {
                chatProduct = chat;
                sendMessageToChat();
            } else {
                Log.d("ProductDetailsFragment", "Chat nulo");
                createNewChat();
            }
        });

        messageViewModel.getSendStatus().observe(getViewLifecycleOwner(), result -> {
            Log.d("ProductDetailsFragment", "Mensagem enviada: " + result);
        });

        messageViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            SnackbarUtil.showErrorSnackbar(getView(), error, getContext());
        });

        messageViewModel.getCreatedChatId().observe(getViewLifecycleOwner(), result -> {
            Log.d("ProductDetailsFragment", "Chat criado: " + result);

            if (chatProduct == null) {
                chatProduct = new Chat();
                chatProduct.setUserId(product.getUser_id());
            }

            chatProduct.setChatId(result);
            sendMessageToChat();
        });

        reviewViewModel.reviews.observe(getViewLifecycleOwner(), reviews -> {
            reviewAdapter.updateReviews(reviews);
        });
    }

    private void addToSaved(int productId) {
        cartManager.saveProduct(productId, user.getUserId(), new RepositoryCallback<ApiResponse<Void>>() {
            @Override
            public void onSuccess(ApiResponse<Void> result) {
                if (result.isSuccess()) {
                    isSaved = true;
                    updateSaveButton(true);
                } else {
                    SnackbarUtil.showErrorSnackbar(getView(), result.getMessage(), getContext());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ProductDetailsFragment", "Erro ao adicionar aos salvos", t);
            }
        });
    }

    private void removeFromSaved(int productId) {
        cartManager.removeFromCart(productId, user.getUserId(), 1, new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                isSaved = false;
                updateSaveButton(false);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ProductDetailsFragment", "Erro ao remover dos salvos", t);
            }
        });
    }

    private void addToCart(int productId) {
        cartManager.addToCart(productId, user.getUserId(), quantityAdd, new RepositoryCallback<ApiResponse<Void>>() {
            @Override
            public void onSuccess(ApiResponse<Void> result) {
                if (result.isSuccess()) {

                    InteractionManager interactionManager = new InteractionManager(RetrofitClient.getRetrofitInstance().create(ApiInteraction.class));

                    interactionManager.userInteraction(product.getProduct_id(), user.getUserId(), "fav", new RepositoryCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d("API_RESPONSE", "Interaction result: " + result);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e("API_RESPONSE", "Error in userInteraction", t);
                        }
                    });

                    showBottomSheet();
                } else {
                    SnackbarUtil.showErrorSnackbar(getView(), result.getMessage(), getContext());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ProductDetailsFragment", "Erro ao adicionar aos salvos", t);
            }
        });
    }

    private void verifyProductIsSaved(int productId, int userId, int tipo) {
        cartManager.verifyProductOnCart(productId, userId, tipo, new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isSaved = result;
                Log.d("ProductDetailsFragment", "Logs: " + result);
                updateSaveButton(result);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ProductDetailsFragment", "Erro ao verificar produto no carrinho", t);
            }
        });
    }

    private void setRatingStars(LinearLayout container, float rating) {
        container.removeAllViews();

        int filledStars = (int) rating;
        boolean hasHalfStar = (rating - filledStars) >= 0.25f && (rating - filledStars) < 0.75f;
        int totalStars = 5;

        for (int i = 0; i < totalStars; i++) {
            AppCompatImageView star = new AppCompatImageView(container.getContext());

            if (i < filledStars) {
                star.setImageResource(R.drawable.ic_star_filled);
            } else if (i == filledStars && hasHalfStar) {
                star.setImageResource(R.drawable.ic_star_half);
            } else {
                star.setImageResource(R.drawable.ic_star);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) getResources().getDisplayMetrics().density * 24,
                    (int) getResources().getDisplayMetrics().density * 24
            );
            params.setMargins(2, 0, 2, 0);
            star.setLayoutParams(params);

            container.addView(star);
        }
    }

    private void showBottomSheet() {
        Bundle bundle = new Bundle();
        bundle.putString("image_product", product.getCover());

        ProductAddedBottomSheet bottomSheet = new ProductAddedBottomSheet();
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
    }

    private void shareProduct(int productId) {
        String link = "http://poggers.ddns.net/PoggTech-APIs/public/product/" + productId;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Confira este produto: " + link);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Compartilhar produto via...");
        startActivity(shareIntent);
    }
}
