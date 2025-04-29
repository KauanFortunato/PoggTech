package com.mordekai.poggtech.ui.fragments;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.GalleryAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Chat;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiMessage;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;
import com.mordekai.poggtech.domain.MessageManager;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.SnackbarUtil;
import com.mordekai.poggtech.utils.Utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class ProductDetailsFragment extends Fragment {

    private TextView titleProduct, category, price, priceDecimal, discount, priceBefore, productPoggers;
    private EditText inputText;
    private LinearLayout contactSellerContainer;
    private AppCompatButton actionButton;
    private ImageButton favoriteButton, btnSend;
    private View skeletonView, contentView;
    private ViewPager2 viewPagerGallery;
    private GalleryAdapter galleryAdapter;
    private TabLayout tabLayoutDots;
    List<String> imageUrls = new ArrayList<>();
    private CartManager cartManager;
    private ProductManager productManager;
    private MessageManager messageManager;
    private ApiProduct apiProduct;
    private SharedPrefHelper sharedPrefHelper;
    private Boolean isFavorite;
    private Chat chatProduct;
    private User user;
    private Product product;
    private int productId;

    private BottomNavigationView bottomNavigationView;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        ((MainActivity) requireActivity()).setForceBackToHome(false);

        // Referencias das views
        skeletonView = view.findViewById(R.id.skeletonView);
        animateContentView(skeletonView);
        contentView = view.findViewById(R.id.contentView);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();
        cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
        messageManager = new MessageManager(RetrofitClient.getRetrofitInstance().create(ApiMessage.class));


        if (getArguments() != null) {
            productId = getArguments().getInt("productId");
        }

        startComponents(contentView);
        verifyProductIsFavorite(productId, user.getUserId(), 1);

        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);

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
                public void onSuccess(Product productDetails) {
                    if (productDetails != null) {
                        product = productDetails;
                        updateUIWithProduct();

                        productManager.getProductImages(productId, new RepositoryCallback<List<String>>() {
                            @Override
                            public void onSuccess(List<String> result) {
                                if (result != null && !result.isEmpty()) {
                                    product.setImages(result);
                                    imageUrls.addAll(result);
                                } else {
                                    if (product.getCover() != null) {
                                        imageUrls.add(product.getCover());
                                    }
                                }

                                galleryAdapter.updateImages(imageUrls);

                                new TabLayoutMediator(tabLayoutDots, viewPagerGallery, (tab, position) -> {}).attach();

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

                                skeletonView.setVisibility(View.GONE);
                                contentView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.e("ProductDetailsFragment", "Erro ao buscar imagens do produto", t);
                                imageUrls.add(product.getCover());
                                galleryAdapter.updateImages(imageUrls);
                                skeletonView.setVisibility(View.GONE);
                                contentView.setVisibility(View.VISIBLE);
                            }
                        });
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

    private void sendMessage() {
        verifyIfChatExist(new RepositoryCallback<Chat>() {
            @Override
            public void onSuccess(Chat chat) {
                if (chat != null) {
                    chatProduct = chat;
                    sendMessageToChat();
                } else {
                    createNewChat();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                SnackbarUtil.showErrorSnackbar(getView(), "Erro ao verificar chat: " + t.getMessage(), getContext());
            }
        });
    }

    private void verifyIfChatExist(RepositoryCallback<Chat> callback) {
        messageManager.fetchChat(user.getUserId(), productId, new RepositoryCallback<Chat>() {
            @Override
            public void onSuccess(Chat chat) {
                callback.onSuccess(chat);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    private void createNewChat() {
        messageManager.createChat(productId, new RepositoryCallback<Integer>() {
            @Override
            public void onSuccess(Integer chatId) {
                if (chatId != null) {
                    chatProduct = new Chat(chatId, productId); // Criar um objeto Chat com o ID correto
                    sendMessageToChat();
                } else {
                    SnackbarUtil.showErrorSnackbar(getView(), "Erro ao obter chat_id", getContext());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                SnackbarUtil.showErrorSnackbar(getView(), "Erro ao criar chat: " + t.getMessage(), getContext());
            }
        });
    }

    private void sendMessageToChat() {
        if (chatProduct != null) {
            messageManager.sendMessage(
                    user.getUserId(),
                    product.getUser_id(),
                    chatProduct.getChat_id(),
                    inputText.getText().toString(),
                    new RepositoryCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d("ProductDetailsFragment", "Mensagem enviada: " + result);
                            verifyIfChatExist(new RepositoryCallback<Chat>() {
                                @Override
                                public void onSuccess(Chat result) {
                                    Utils.goToChat(requireActivity(), result);
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    SnackbarUtil.showErrorSnackbar(getView(), "Erro ao buscar chat: " + t.getMessage(), getContext());
                                }
                            });
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            SnackbarUtil.showErrorSnackbar(getView(), "Erro ao enviar mensagem: " + t.getMessage(), getContext());
                        }
                    }
            );
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateUIWithProduct() {
        Log.d("ProductDetailsFragment", "Logs: " + product.getUser_id() + " " + productId);

        float priceTotal = product.getPrice() != null ? product.getPrice() : 0f;
        int integerPart = (int) priceTotal;
        int cents = Math.round((priceTotal - integerPart) * 100);

        titleProduct.setText(product.getTitle());
        category.setText(product.getCategory());

        price.setText(String.valueOf(integerPart));
        priceDecimal.setText(String.format("%02d€", cents));
        Log.d("ProductDetailsFragment", "Logs: " + product.getDiscountPercentage());

        if(product.getUser_id() != user.getUserId()) {
            actionButton.setVisibility(View.GONE);
            contactSellerContainer.setVisibility(View.VISIBLE);
        } else {
            actionButton.setBackgroundResource(R.drawable.bg_button_unable);
            actionButton.setVisibility(View.VISIBLE);
            contactSellerContainer.setVisibility(View.GONE);
        }

        if (product.getDiscountPercentage() != null && product.getDiscountPercentage() > 0) {
            priceBefore.setText(String.format("%s %.2f€", getString(R.string.antes),
                    product.getPriceBefore() != null ? product.getPriceBefore() : 0f));
            discount.setText(String.format("-%d%%",
                    product.getDiscountPercentage() != null ? product.getDiscountPercentage().intValue() : 0));
        } else {
            priceBefore.setVisibility(View.GONE);
            discount.setVisibility(View.GONE);
        }


        btnSend.setOnClickListener(v -> {
            if(!inputText.getText().toString().isEmpty()) {
                if(btnSend.isHapticFeedbackEnabled()) {
                    btnSend.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                }
                sendMessage();
            } else {
                if(btnSend.isHapticFeedbackEnabled()) {
                    btnSend.performHapticFeedback(HapticFeedbackConstants.REJECT);
                }
                inputText.setError("Escreva uma mensagem");
            }
        });
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
        actionButton = view.findViewById(R.id.actionPrimaryProduct);
        inputText = view.findViewById(R.id.inputText);
        contactSellerContainer = view.findViewById(R.id.contactSellerContainer);
        btnSend = view.findViewById(R.id.btnSend);
        viewPagerGallery = view.findViewById(R.id.viewPagerGallery);
        tabLayoutDots = view.findViewById(R.id.tabLayoutDots);

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
        getActivity().findViewById(R.id.headerContainer).setVisibility(View.VISIBLE);
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
        view.setVisibility(View.VISIBLE);
    }
}
