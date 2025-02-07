package com.mordekai.poggtech.data.adapter;

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
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ProductApi;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.List;

import okhttp3.ResponseBody;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final List<Product> products;
    private final int userId;

    public ProductAdapter(List<Product> products, int userId) {
        this.products = products;
        this.userId = userId;
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
        CartManager cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ProductApi.class));
        Product product = products.get(position);

        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText("€ " + product.getPrice());
        holder.productType.setText(product.getCategory());

        holder.buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartManager.addToCart(product.getProduct_id(), userId, 0, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody result) {
                        Toast.makeText(v.getContext(), "Adicionado ao carrinho", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(v.getContext(), "Erro ao adicionar ao carrinho", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartManager.addToCart(product.getProduct_id(), userId, 0, new RepositoryCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody result) {
                        Toast.makeText(v.getContext(), "Adicionado aos favoritos", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(v.getContext(), "Erro ao adicionar aos favoritos", Toast.LENGTH_SHORT).show();
                    }
                });
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        ImageButton favoriteButton;
        TextView productTitle;
        TextView productType;
        TextView productPrice;
        ImageView buttonAddToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializa os componentes do layout
            productImage = itemView.findViewById(R.id.productImage);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            productTitle = itemView.findViewById(R.id.productTitle);
            productType = itemView.findViewById(R.id.productType);
            productPrice = itemView.findViewById(R.id.productPrice);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
        }
    }
}
