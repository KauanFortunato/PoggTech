package com.mordekai.poggtech.data.adapter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;
import com.mordekai.poggtech.utils.Utils;


import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final List<Product> products;
    private List<Integer> savedIds = new ArrayList<>();
    private final int userId;
    private final OnProductClickListener productClickListener;
    private final OnSavedChangedListener savedChangedListener;
    private final int layoutResId;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public interface OnSavedChangedListener {
        void onSaveChanged();
    }

    public ProductAdapter(List<Product> products, int userId, int layoutResId,
                          OnProductClickListener productClickListener,
                          OnSavedChangedListener savedChangedListener) {
        this.products = products;
        this.userId = userId;
        this.productClickListener = productClickListener;
        this.savedChangedListener = savedChangedListener;
        this.layoutResId = layoutResId;
    }


    public void setSavedIds(List<Integer> savedIds) {
        this.savedIds = savedIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText(String.format("%.2f€", product.getPrice()));
        holder.productType.setText(product.getCategory());

        if(product.getSeller_type().equals("admin")) {
            holder.sellerAdmin.setVisibility(View.VISIBLE);
            holder.seller.setVisibility(View.GONE);

            if(product.getQuantity() <= 10) {
                holder.warning.setVisibility(View.VISIBLE);
            } else {
                holder.warning.setVisibility(View.GONE);
            }
        } else {
            holder.seller.setVisibility(View.VISIBLE);
            holder.sellerAdmin.setVisibility(View.GONE);
            holder.productRatingContainer.setVisibility(View.GONE);
            holder.seller.setText(String.format("%s %s", holder.seller.getContext().getString(R.string.sold_by), product.getUser_name()));
        }

        if (savedIds.contains(product.getProduct_id())) {
            holder.saveButton.setImageResource(R.drawable.ic_bookmark_fill);
        } else {
            holder.saveButton.setImageResource(R.drawable.ic_bookmark);
        }

        holder.saveButton.setOnClickListener(v -> {
            if (holder.saveButton.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            boolean isSave = savedIds.contains(product.getProduct_id());

            if (isSave) {
                // Remove dos favoritos
                savedIds.remove(Integer.valueOf(product.getProduct_id()));
                holder.saveButton.setImageResource(R.drawable.ic_bookmark);
                removeFromSaved(product.getProduct_id(), v, holder.getAdapterPosition());
            } else {
                // Adiciona aos favoritos
                savedIds.add(product.getProduct_id());
                holder.saveButton.setImageResource(R.drawable.ic_bookmark_fill);
                addToSaved(product.getProduct_id(), v, holder.getAdapterPosition());
            }

            // Atualiza todas as listas em tempo real
            if (savedChangedListener != null) {
                savedChangedListener.onSaveChanged();
            }

        });

        if(product.getDiscountPercentage() != null && product.getDiscountPercentage() > 0) {
            holder.discount.setVisibility(View.VISIBLE);
            holder.discount.setText(String.format("-%d%%",
                    product.getDiscountPercentage() != null ? product.getDiscountPercentage().intValue() : 0));
        } else {
            holder.discount.setVisibility(View.GONE);
        }

        if(!product.getStatus().equals("available")) {
            holder.discount.setVisibility(View.GONE);
            holder.saveButton.setVisibility(View.GONE);
            holder.itemView.setAlpha(0.5f);
            holder.textUnavailable.setVisibility(View.VISIBLE);

            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.placeholder_image_error)
                    .placeholder(R.drawable.exemplo_ft3)
                    .error(R.drawable.placeholder_image_error)
                    .into(holder.productImage);
        } else {
            Utils.loadImageBasicAuth(holder.productImage, product.getCover());
        }

        holder.rating.setText(String.format("%.1f", product.getRating()));

        // Quando o produto for clicado
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

    public void updateProducts(List<Product> newProducts) {
        this.products.clear();
        this.products.addAll(newProducts);
        notifyDataSetChanged();
    }

    private void addToSaved(int productId, View view, int position) {
        CartManager cartManager = new CartManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
        cartManager.saveProduct(productId, userId, new RepositoryCallback<ApiResponse<Void>>() {
            @Override
            public void onSuccess(ApiResponse<Void> result) {
                if (result.isSuccess()) {
                    notifyItemChanged(position);
                    Toast.makeText(view.getContext(), "Adicionado ao salvar", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Erro ao adicionar ao salvar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(view.getContext(), "Erro ao adicionar ao salvar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromSaved(int productId, View view, int position) {
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        ImageButton saveButton;
        TextView productTitle;
        TextView productType;
        TextView productPrice;
        TextView sellerAdmin;
        TextView seller;
        TextView discount, warning;
        TextView textUnavailable;
        TextView rating;
        LinearLayout  productRatingContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializa os componentes do layout
            productImage = itemView.findViewById(R.id.productImage);
            saveButton = itemView.findViewById(R.id.saveButton);
            productTitle = itemView.findViewById(R.id.productTitle);
            productType = itemView.findViewById(R.id.productType);
            productPrice = itemView.findViewById(R.id.productPrice);
            sellerAdmin = itemView.findViewById(R.id.sellerAdmin);
            seller = itemView.findViewById(R.id.seller);

            discount = itemView.findViewById(R.id.discount);
            warning = itemView.findViewById(R.id.warning);

            textUnavailable = itemView.findViewById(R.id.textUnavailable);
            rating = itemView.findViewById(R.id.rating);
            productRatingContainer = itemView.findViewById(R.id.productRatingContainer);
        }
    }
}
