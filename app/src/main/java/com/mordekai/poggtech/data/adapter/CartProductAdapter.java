package com.mordekai.poggtech.data.adapter;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;
import com.mordekai.poggtech.presentation.ui.bottomsheets.DeleteProductBottomSheet;
import com.mordekai.poggtech.utils.SnackbarUtil;
import com.mordekai.poggtech.utils.Utils;

import java.util.List;

import okhttp3.ResponseBody;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.ViewHolder> {
    private final List<Product> products;
    private final int userId;
    private final OnProductClickListener productClickListener;
    private final OnProductChangeQuantityListener productChangeQuantityListener;
    FragmentManager fragmentManager;


    public CartProductAdapter(List<Product> products, int userId, OnProductClickListener productClickListener,
                              OnProductChangeQuantityListener productChangeQuantityListener, FragmentManager fragmentManager) {
        this.products = products;
        this.userId = userId;
        this.productClickListener = productClickListener;
        this.productChangeQuantityListener = productChangeQuantityListener;
        this.fragmentManager = fragmentManager;
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public interface OnProductChangeQuantityListener {
        void onProductChangeQuantity(Product product);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle;
        TextView productType;
        TextView productPrice;
        TextView quantity;
        ImageView buttonRemove;
        AppCompatImageView minusProduct;
        AppCompatImageView plusProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializa os componentes do layout
            productImage = itemView.findViewById(R.id.categoryIcon);
            productTitle = itemView.findViewById(R.id.productTitle);
            productType = itemView.findViewById(R.id.productType);
            productPrice = itemView.findViewById(R.id.productPrice);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
            quantity = itemView.findViewById(R.id.quantity);
            minusProduct = itemView.findViewById(R.id.minusProduct);
            plusProduct = itemView.findViewById(R.id.plusProduct);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText(String.format("%.2f€", product.getPrice()));
        holder.productType.setText(product.getCategory());

        holder.minusProduct.setOnClickListener(v -> {
            if (holder.minusProduct.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            decreaseProduct(product.getProduct_id(), v, holder.getAdapterPosition());
        });

        holder.plusProduct.setOnClickListener(v -> {
            if (holder.plusProduct.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            addMore(product.getProduct_id(), v, holder.getAdapterPosition());
        });

        holder.quantity.setText(String.valueOf(product.getQuantity()));

        holder.buttonRemove.setOnClickListener(v -> {

            if (holder.buttonRemove.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            showRemoveConfirmation(product.getProduct_id(), v, holder.getAdapterPosition());
        });

        Utils.loadImageBasicAuth(holder.productImage, product.getCover());

        holder.itemView.setOnClickListener(view -> {
            if (productClickListener != null) {
                productClickListener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private void addMore(int productId, View view, int position) {
        CartManager cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
        cartManager.addToCart(productId, userId, 1, new RepositoryCallback<ApiResponse<Void>>() {

            @Override
            public void onSuccess(ApiResponse<Void> result) {
                if (result.isSuccess()) {
                    Product product = products.get(position);
                    product.setQuantity(product.getQuantity() + 1);

                    productChangeQuantityListener.onProductChangeQuantity(product);
                    notifyItemChanged(position);
                } else {
                    SnackbarUtil.showErrorSnackbar(view, result.getMessage(), view.getContext());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(view.getContext(), "Erro ao adicionar ao carrinho", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void decreaseProduct(int productId, View view, int position) {
        CartManager cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
        cartManager.removeOneFromCart(productId, userId, 0, new RepositoryCallback<ApiResponse<Void>>() {

            @Override
            public void onSuccess(ApiResponse<Void> result) {
                if (result.isSuccess()) {
                    Product product = products.get(position);
                    int currentQty = product.getQuantity();

                    if (currentQty > 1) {
                        product.setQuantity(currentQty - 1);
                        notifyItemChanged(position);
                    } else {
                        products.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, products.size());
                    }

                    productChangeQuantityListener.onProductChangeQuantity(product);
                } else {
                    SnackbarUtil.showErrorSnackbar(view, result.getMessage(), view.getContext());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(view.getContext(), "Erro ao adicionar ao carrinho", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromCart(int productId, View view, int position) {
        CartManager cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
        cartManager.removeFromCart(productId, userId, 0, new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                Product product = products.get(position);

                products.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, products.size());

                productChangeQuantityListener.onProductChangeQuantity(product);
                Toast.makeText(view.getContext(), "Removido dos salvos", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(view.getContext(), "Erro ao remover dos salvos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRemoveConfirmation(int productId, View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("title", view.getContext().getString(R.string.removeProduct));
        bundle.putString("button_confirm", view.getContext().getString(R.string.remove));

        DeleteProductBottomSheet bottomSheet = new DeleteProductBottomSheet(new DeleteProductBottomSheet.OnDeleteConfirmedListener() {
            @Override
            public void onDeleteConfirmed() {
                removeFromCart(productId, view, position);
            }
        });
        bottomSheet.setArguments(bundle);
        bottomSheet.show(fragmentManager, bottomSheet.getTag());
    }
}
