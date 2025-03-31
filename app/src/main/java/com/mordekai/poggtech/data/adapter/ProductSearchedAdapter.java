package com.mordekai.poggtech.data.adapter;

import android.annotation.SuppressLint;
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
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class ProductSearchedAdapter extends RecyclerView.Adapter<ProductSearchedAdapter.ViewHolder> {

    private final List<Product> products;
    private List<Integer> favoriteIds = new ArrayList<>();
    private final int userId;

    public ProductSearchedAdapter(List<Product> products, int userId) {
        this.products = products;
        this.userId = userId;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_product_searched, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ProductSearchedAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);

        float priceTotal = product.getPrice() != null ? product.getPrice() : 0f;
        int integerPart = (int) priceTotal;
        int cents = Math.round((priceTotal - integerPart) * 100);

        holder.productTitle.setText(product.getTitle());
        holder.productType.setText(product.getCategory());
        holder.seller.setText(product.getSeller_type());

        if(product.getDiscountPercentage() != null && product.getDiscountPercentage() > 0) {
            holder.priceBefore.setVisibility(View.VISIBLE);
            holder.discount.setVisibility(View.VISIBLE);
        } else {
            holder.priceBefore.setVisibility(View.GONE);
            holder.discount.setVisibility(View.GONE);
        }

        holder.priceBefore.setText(String.format("%s %.2f€",
                holder.itemView.getContext().getString(R.string.antes),
                product.getPriceBefore() != null ? product.getPriceBefore() : 0f));

        holder.discount.setText(String.format("%d%% %s", product.getDiscountPercentage() != null ? product.getDiscountPercentage().intValue() : 0, "de desconto"));

        holder.productPrice.setText(String.valueOf(integerPart));
        holder.priceDecimal.setText(String.format("%02d€", cents));

        if(product.getSeller_type().equals("admin")) {
            holder.sellerAdmin.setVisibility(View.VISIBLE);
            holder.seller.setVisibility(View.GONE);
            holder.deliveryType.setVisibility(View.GONE);
        } else {
            holder.seller.setVisibility(View.VISIBLE);
            holder.sellerAdmin.setVisibility(View.GONE);
            holder.deliveryType.setVisibility(View.VISIBLE);
        }

        if (favoriteIds.contains(product.getProduct_id())) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite);
        }

        Glide.with(holder.productImage.getContext())
                .load(product.getImage_url())
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        ImageButton favoriteButton;
        TextView seller;
        TextView productTitle;
        TextView productType;
        TextView productPrice;
        TextView priceBefore;
        TextView priceDecimal;
        TextView sellerAdmin;
        TextView deliveryType;
        TextView discount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            seller = itemView.findViewById(R.id.seller);
            sellerAdmin = itemView.findViewById(R.id.sellerAdmin);
            productTitle = itemView.findViewById(R.id.productTitle);
            productType = itemView.findViewById(R.id.productType);
            productPrice = itemView.findViewById(R.id.price);
            priceBefore = itemView.findViewById(R.id.priceBefore);
            priceDecimal = itemView.findViewById(R.id.priceDecimal);
            deliveryType = itemView.findViewById(R.id.deliveryType);
            discount = itemView.findViewById(R.id.discount);
        }
    }

    public void updateProducts(List<Product> newProducts) {
        this.products.clear();
        this.products.addAll(newProducts);
        notifyDataSetChanged();
    }

    private void addToFavorites(int productId, View view, int position) {
        CartManager cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
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
        CartManager cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
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
}
