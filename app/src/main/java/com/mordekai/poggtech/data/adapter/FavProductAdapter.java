package com.mordekai.poggtech.data.adapter;

import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;

import java.util.List;

import okhttp3.ResponseBody;

public class FavProductAdapter extends RecyclerView.Adapter<FavProductAdapter.ViewHolder> {
    private final List<Product> products;
    private final int userId;

    public FavProductAdapter(List<Product> products, int userId) {
        this.products = products;
        this.userId = userId;
    }

    @NonNull
    @Override
    public FavProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_product_favorite, parent, false);
        return new FavProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavProductAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText("€ " + product.getPrice());
        holder.productType.setText(product.getCategory());

        holder.removeButton.setOnClickListener(v -> {
           if(holder.removeButton.isHapticFeedbackEnabled()) {
               v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
           }

           removeFromFavorites(product.getProduct_id(), v, holder.getAdapterPosition());
       });

        holder.buttonAddToCart.setOnClickListener(v -> {
            if(holder.buttonAddToCart.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            addToCart(product.getProduct_id(), v, holder.getAdapterPosition());
        });

        Glide.with(
                    holder.productImage.getContext())
            .load(product.getImage_url())
            .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        ImageView buttonAddToCart;
        TextView productTitle;
        TextView productType;
        TextView productPrice;
        TextView removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializa os componentes do layout
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productType = itemView.findViewById(R.id.productType);
            productPrice = itemView.findViewById(R.id.productPrice);
            removeButton = itemView.findViewById(R.id.removeButton);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
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

    private void addToCart(int productId, View view, int position) {
        CartManager cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
        cartManager.addToCart(productId, userId, 0, new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                notifyItemChanged(position);
                Toast.makeText(view.getContext(), "Adicionado ao carrinho", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(view.getContext(), "Erro ao adicionar ao carrinho", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
