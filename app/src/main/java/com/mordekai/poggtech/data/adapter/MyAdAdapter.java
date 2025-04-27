package com.mordekai.poggtech.data.adapter;

import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.CartManager;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.ui.bottomsheets.DeleteProductBottomSheet;
import com.mordekai.poggtech.utils.SnackbarUtil;
import com.mordekai.poggtech.utils.Utils;

import java.util.List;

public class MyAdAdapter extends RecyclerView.Adapter<MyAdAdapter.ViewHolder> {

    private List<Product> products;
    FragmentManager fragmentManager;
    private int expandedPosition = -1;

    public MyAdAdapter(List<Product> products, FragmentManager fragmentManager) {
        this.products = products;
        this.fragmentManager = fragmentManager;
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
        boolean isExpanded = position == expandedPosition;
        holder.bind(product, isExpanded);

        holder.itemView.setOnClickListener( v -> {
            int oldExpandedPosition = expandedPosition;
            if (expandedPosition == position) {
                expandedPosition = -1;
            } else {
                expandedPosition = position;
            }

            notifyItemChanged(position);
            if (oldExpandedPosition != -1) {
                notifyItemChanged(oldExpandedPosition);
            }
        });
    }

    @Override
    public int getItemCount() { return products.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage, buttonEdit;
        private TextView removeButton, productTitle, productType, productPrice;
        private TextView productViews, productReleaseDate, productFav, productLastUpdate;
        private View expandableLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            removeButton = itemView.findViewById(R.id.removeButton);
            productTitle = itemView.findViewById(R.id.productTitle);
            productType = itemView.findViewById(R.id.productType);
            productPrice = itemView.findViewById(R.id.productPrice);

            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            productViews = itemView.findViewById(R.id.productViews);
            productReleaseDate = itemView.findViewById(R.id.productReleaseDate);
            productFav = itemView.findViewById(R.id.productFav);
            productLastUpdate = itemView.findViewById(R.id.productLastUpdate);
        }

        public void bind(Product product, boolean isExpanded) {
            productTitle.setText(product.getTitle());
            productType.setText(product.getCategory());
            productPrice.setText(String.format("%.2f€", product.getPrice()));

            productViews.setText(String.format("%d visualizações", product.getViews()));
            productReleaseDate.setText(product.getCreated_at());
            productFav.setText(String.format("%d favoritos", product.getFavorite_count()));
            productLastUpdate.setText(product.getUpdated_at());

            removeButton.setOnClickListener(v -> {
                if(removeButton.isHapticFeedbackEnabled()) {
                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                }

                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    showDeleteConfirmation(product, position, v);
                }
            });

            Utils.loadImageBasicAuth(productImage, product.getCover());

            expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            itemView.setActivated(isExpanded);
        }
    }

    public void updateProducts(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
    }

    private void deleteProduct(Product product, int position, View view) {
        ProductManager productManager = new ProductManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
        productManager.deleteProduct(product.getProduct_id(), new RepositoryCallback<String>() {

            @Override
            public void onSuccess(String result) {
                products.remove(position);
                notifyItemRemoved(position);
            }

            @Override
            public void onFailure(Throwable t) {
                SnackbarUtil.showErrorSnackbar(view, "Erro deletar produto", view.getContext());
            }
        });
    }

    private void showDeleteConfirmation(Product product, int position, View view) {
        DeleteProductBottomSheet bottomSheet = new DeleteProductBottomSheet(new DeleteProductBottomSheet.OnDeleteConfirmedListener() {
            @Override
            public void onDeleteConfirmed() {
                deleteProduct(product, position, view);
            }
        });
        bottomSheet.show(fragmentManager, bottomSheet.getTag());
    }
}
