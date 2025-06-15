package com.mordekai.poggtech.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.presentation.ui.fragments.HomeFragment;
import com.mordekai.poggtech.utils.Utils;

import java.util.List;

public class ProductContinueAdapter extends RecyclerView.Adapter<ProductContinueAdapter.ViewHolder>{

    private final List<Product> products;
    private final int userId;
    private final HomeFragment homeFragment;
    private final OnProductContinueClickListener listener;

    public interface OnProductContinueClickListener {
        void onProductClick(Product product);
    }


    public ProductContinueAdapter(List<Product> products, int userId, HomeFragment homeFragment, OnProductContinueClickListener listener) {
        this.products = products;
        this.userId = userId;
        this.homeFragment = homeFragment;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductContinueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_continue_buy, parent, false);
        return new ProductContinueAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductContinueAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);

        if(product.getCover() != null) {
            Utils.loadImageBasicAuth(holder.productImage, product.getCover());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializa os componentes do layout
            productImage = itemView.findViewById(R.id.productImage);
        }
    }
}
