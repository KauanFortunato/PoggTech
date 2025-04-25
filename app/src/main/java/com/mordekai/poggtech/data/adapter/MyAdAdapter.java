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
import com.mordekai.poggtech.utils.Utils;

import java.util.List;

public class MyAdAdapter extends RecyclerView.Adapter<MyAdAdapter.ViewHolder> {

    private List<Product> products;

    public MyAdAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_ad, parent, false);

        return new MyAdAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() { return products.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage, buttonEdit;
        private TextView removeButton, productTitle, productType, productPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            removeButton = itemView.findViewById(R.id.removeButton);
            productTitle = itemView.findViewById(R.id.productTitle);
            productType = itemView.findViewById(R.id.productType);
            productPrice = itemView.findViewById(R.id.productPrice);

        }

        public void bind(Product product) {
            productTitle.setText(product.getTitle());
            productType.setText(product.getCategory());
            productPrice.setText(String.format("%.2f€", product.getPrice()));

            Utils.loadImageBasicAuth(productImage, product.getCover());
        }
    }

    public void updateProducts(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
    }
}
