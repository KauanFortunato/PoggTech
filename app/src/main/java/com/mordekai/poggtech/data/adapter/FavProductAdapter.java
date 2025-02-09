package com.mordekai.poggtech.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.Product;

import java.util.List;

public class FavProductAdapter extends RecyclerView.Adapter<FavProductAdapter.ViewHolder> {
    private final List<Product> products;

    public FavProductAdapter(List<Product> products) {
        this.products = products;
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
}
