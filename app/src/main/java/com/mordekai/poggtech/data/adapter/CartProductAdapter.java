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
import com.mordekai.poggtech.utils.Utils;

import java.util.List;

import okhttp3.ResponseBody;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.ViewHolder> {
    private final List<Product> products;
    private final int userId;

    public CartProductAdapter(List<Product> products, int userId) {
        this.products = products;
        this.userId = userId;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle;
        TextView productType;
        TextView productPrice;
        ImageView buttonRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializa os componentes do layout
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productType = itemView.findViewById(R.id.productType);
            productPrice = itemView.findViewById(R.id.productPrice);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_product_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText("€ " + product.getPrice());
        holder.productType.setText(product.getCategory());

        holder.buttonRemove.setOnClickListener(v -> {

            if(holder.buttonRemove.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            removeFromCart(product.getProduct_id(), v, holder.getAdapterPosition());
        });

        Utils.loadImageBasicAuth(holder.productImage, product.getCover());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private void removeFromCart(int productId, View view, int position) {
        CartManager cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
        cartManager.removeFromCart(productId, userId, 0, new RepositoryCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                products.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, products.size());
                Toast.makeText(view.getContext(), "Removido dos salvos", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(view.getContext(), "Erro ao remover dos salvos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
