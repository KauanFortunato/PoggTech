package com.mordekai.poggtech.data.adapter;

import android.annotation.SuppressLint;
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

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;
import com.mordekai.poggtech.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class ProductSearchedAdapter extends RecyclerView.Adapter<ProductSearchedAdapter.ViewHolder> {

    private final List<Product> products;
    private List<Integer> favoriteIds = new ArrayList<>();
    private final User user;
    private OnProductClickListener productClickListener;

    public ProductSearchedAdapter(List<Product> products, User user, OnProductClickListener productClickListener) {
        this.products = products;
        this.user = user;
        this.productClickListener = productClickListener;
    }

    public void setFavoriteIds(List<Integer> favoriteIds) {
        this.favoriteIds = favoriteIds;
        notifyDataSetChanged();
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_searched, parent, false);

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

        holder.discount.setText(String.format("%d%% %s",
                product.getDiscountPercentage() != null ? product.getDiscountPercentage().intValue() : 0,
                holder.itemView.getContext().getString(R.string.discount)));

        holder.productPrice.setText(String.valueOf(integerPart));
        holder.priceDecimal.setText(String.format("%02d€", cents));

        if(product.getSeller_type().equals("admin")) {
            holder.sellerAdmin.setVisibility(View.VISIBLE);
            holder.seller.setVisibility(View.GONE);
            holder.deliveryType.setVisibility(View.GONE);
        } else {
            holder.seller.setVisibility(View.VISIBLE);
            holder.seller.setText(holder.itemView.getContext().getString(R.string.soldeBy) + " " + user.getName());

            holder.sellerAdmin.setVisibility(View.GONE);
            holder.deliveryType.setVisibility(View.VISIBLE);
        }

        if (favoriteIds.contains(product.getProduct_id())) {
            holder.favoriteButton.setImageResource(R.drawable.ic_bookmark_fill);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_bookmark);
        }

        holder.favoriteButton.setOnClickListener(v -> {
            if (holder.favoriteButton.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            boolean isFavorite = favoriteIds.contains(product.getProduct_id());

            if (isFavorite) {
                // Remove dos favoritos
                favoriteIds.remove(Integer.valueOf(product.getProduct_id()));
                holder.favoriteButton.setImageResource(R.drawable.ic_bookmark);
                removeFromFavorites(product.getProduct_id(), v, holder.getAdapterPosition());
            } else {
                // Adiciona aos favoritos
                favoriteIds.add(product.getProduct_id());
                holder.favoriteButton.setImageResource(R.drawable.ic_bookmark_fill);
                addToFavorites(product.getProduct_id(), v, holder.getAdapterPosition());
            }
        });

        Utils.loadImageBasicAuth(holder.productImage, product.getCover());

        holder.itemView.setOnClickListener( v -> {
            if (productClickListener != null) {
                productClickListener.onProductClick(product);
            }
        });
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
            favoriteButton = itemView.findViewById(R.id.saveButton);
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
        cartManager.addToCart(productId, user.getUserId(), 1, new RepositoryCallback<ResponseBody>() {
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
        cartManager.removeFromCart(productId, user.getUserId(), 1, new RepositoryCallback<ResponseBody>() {
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
