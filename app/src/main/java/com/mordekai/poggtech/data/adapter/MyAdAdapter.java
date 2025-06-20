package com.mordekai.poggtech.data.adapter;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.presentation.ui.bottomsheets.MoreActionsMyProduct;
import com.mordekai.poggtech.utils.SnackbarUtil;
import com.mordekai.poggtech.utils.Utils;

import java.util.List;

public class MyAdAdapter extends RecyclerView.Adapter<MyAdAdapter.ViewHolder> {

    private final List<Product> products;
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

        private final ImageView productImage;
        private final ImageView moreBtn;
        private final TextView productTitle;
        private final TextView productType;
        private final TextView productPrice;
        private final TextView productViews;
        private final TextView productReleaseDate;
        private final TextView productFav;
        private final TextView productLastUpdate;
        private final View expandableLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            productTitle = itemView.findViewById(R.id.productTitle);
            productType = itemView.findViewById(R.id.productType);
            productPrice = itemView.findViewById(R.id.productPrice);

            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            productViews = itemView.findViewById(R.id.productViews);
            productReleaseDate = itemView.findViewById(R.id.productReleaseDate);
            productFav = itemView.findViewById(R.id.productFav);
            productLastUpdate = itemView.findViewById(R.id.productRating);
        }

        public void bind(Product product, boolean isExpanded) {
            productTitle.setText(product.getTitle());
            productType.setText(product.getCategory());
            productPrice.setText(String.format("%.2f€", product.getPrice()));

            productViews.setText(String.format("%d visualizações", product.getViews()));
            productReleaseDate.setText(product.getCreated_at());
            productFav.setText(String.format("%d salvos", product.getSaved_count()));
            productLastUpdate.setText(product.getUpdated_at());

            moreBtn.setOnClickListener(v -> {
                if(moreBtn.isHapticFeedbackEnabled()) {
                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                }
                int position = getAdapterPosition();

                showMoreActions(product, position, v);
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

    private void openEditScreen(Product product, View view) {
        Bundle args = new Bundle();
        args.putSerializable("product", product);

        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_myAdsFragment_to_newAdFragment, args);
    }

    private void markProductAsSold(Product product, View view) {
        ProductManager productManager = new ProductManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));
        productManager.reduceQuantity(product.getProduct_id(), 1, new RepositoryCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Toast.makeText(view.getContext(), "Produto marcado como vendido", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(view.getContext(), "Erro ao marcar como vendido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMoreActions(Product product, int position, View view) {
        MoreActionsMyProduct bottomSheet = new MoreActionsMyProduct( product.isAvailable(),
                () -> {
                    // ação para editar
                    openEditScreen(product, view);
                },
                () -> {
                    // ação para deletar
                    Utils util = new Utils();
                    util.showDialog(view.getContext(), "Tem a certeza que quer deletar este produto?", "Isso apagar todos os dados do seu produto", "Confirmar", v -> {
                        deleteProduct(product, position, view);
                    });
                },
                () -> {
                    // ação para marcar como vendido
                    markProductAsSold(product, view);
                }
        );
        bottomSheet.show(fragmentManager, bottomSheet.getTag());
    }

}
