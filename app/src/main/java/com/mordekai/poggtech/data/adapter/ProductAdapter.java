package com.mordekai.poggtech.data.adapter;

import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ProductApi;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;
import com.mordekai.poggtech.ui.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final List<Product> products;
    private List<Integer> favoriteIds = new ArrayList<>();
    private final int userId;
    private HomeFragment homeFragment;

    public ProductAdapter(List<Product> products, int userId, HomeFragment homeFragment) {
        this.products = products;
        this.userId = userId;
        this.homeFragment = homeFragment;
    }

    public void setFavoriteIds(List<Integer> favoriteIds) {
        this.favoriteIds = favoriteIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText("€ " + product.getPrice());
        holder.productType.setText(product.getCategory());

        if (favoriteIds.contains(product.getProduct_id())) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite);
        }

        holder.favoriteButton.setOnClickListener(v -> {
            if (holder.favoriteButton.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            boolean isFavorite = favoriteIds.contains(product.getProduct_id());

            if (isFavorite) {
                // Remove dos favoritos
                favoriteIds.remove(Integer.valueOf(product.getProduct_id()));
                holder.favoriteButton.setImageResource(R.drawable.ic_favorite);
                removeFromFavorites(product.getProduct_id(), v, holder.getAdapterPosition());
            } else {
                // Adiciona aos favoritos
                favoriteIds.add(product.getProduct_id());
                holder.favoriteButton.setImageResource(R.drawable.ic_favorite_filled);
                addToFavorites(product.getProduct_id(), v, holder.getAdapterPosition());
            }

            // Atualiza todas as listas em tempo real
            if (homeFragment != null) {
                homeFragment.updateAllAdapters();
            }
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

    public void updateProducts(List<Product> newProducts) {
        this.products.clear();
        this.products.addAll(newProducts);
        notifyDataSetChanged();
    }

    private void addToFavorites(int productId, View view, int position) {
        CartManager cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ProductApi.class));
        cartManager.addToCart(productId, userId, 1, new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                notifyItemChanged(position);
                Toast.makeText(view.getContext(), "Adicionado aos favoritos", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(view.getContext(), "Erro ao adicionar aos favoritos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromFavorites(int productId, View view, int position) {
        CartManager cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ProductApi.class));
        cartManager.removeFromCart(productId, userId, 1, new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                notifyItemChanged(position);
                Toast.makeText(view.getContext(), "Removido dos favoritos", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(view.getContext(), "Erro ao remover dos favoritos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        ImageButton favoriteButton;
        TextView productTitle;
        TextView productType;
        TextView productPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializa os componentes do layout
            productImage = itemView.findViewById(R.id.productImage);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            productTitle = itemView.findViewById(R.id.productTitle);
            productType = itemView.findViewById(R.id.productType);
            productPrice = itemView.findViewById(R.id.productPrice);
        }
    }
}
