package com.mordekai.poggtech.data.adapter;

import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;
import com.mordekai.poggtech.utils.Utils;

import java.util.List;

import okhttp3.ResponseBody;

public class SavedProductAdapter extends RecyclerView.Adapter<SavedProductAdapter.ViewHolder> {
    private final List<Product> products;
    private final int userId;
    private final SavedProductAdapter.OnProductClickListener productClickListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public SavedProductAdapter(List<Product> products, int userId, SavedProductAdapter.OnProductClickListener productClickListener) {
        this.products = products;
        this.userId = userId;
        this.productClickListener = productClickListener;
    }

    @NonNull
    @Override
    public SavedProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_saved, parent, false);
        return new SavedProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedProductAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText("€ " + product.getPrice());
        holder.productType.setText(product.getCategory());

        holder.buttonRemove.setOnClickListener(v -> {
            if (holder.buttonRemove.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            removeFromFavorites(product.getProduct_id(), v, holder.getAdapterPosition());
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle;
        TextView productType;
        TextView productPrice;
        AppCompatImageView buttonRemove, addToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializa os componentes do layout
            productImage = itemView.findViewById(R.id.categoryIcon);
            productTitle = itemView.findViewById(R.id.productTitle);
            productType = itemView.findViewById(R.id.productType);
            productPrice = itemView.findViewById(R.id.productPrice);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
            addToCart = itemView.findViewById(R.id.addToCart);
        }
    }

    private void removeFromFavorites(int productId, View view, int position) {
        CartManager cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
        cartManager.removeFromCart(productId, userId, 1, new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                products.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, products.size());
                Toast.makeText(view.getContext(), "Removido dos favoritos", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(view.getContext(), "Erro ao remover dos favoritos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
