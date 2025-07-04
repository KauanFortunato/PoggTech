package com.mordekai.poggtech.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.Order;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private final List<Order> orders;

    private final OnOrderClicked onOrderClicked;

    public interface OnOrderClicked {
        void onProductClick(Order order);
    }

    public OrdersAdapter(List<Order> orders, OnOrderClicked onOrderClicked) {
        this.orders = orders;
        this.onOrderClicked = onOrderClicked;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);

        return new OrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order, holder.itemView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView date;
        private final TextView orderId;
        private final TextView totalAmount;
        private final TextView totalItems;
        private final FlexboxLayout flexImages;
        private final LinearLayout containerImages;
        private final CardView cardContainer;
        private final TextView otherProducts;
        private final TextView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            totalItems = itemView.findViewById(R.id.totalItems);
            orderId = itemView.findViewById(R.id.orderId);
            flexImages = itemView.findViewById(R.id.flexImages);
            containerImages = itemView.findViewById(R.id.containerImages);
            cardContainer = itemView.findViewById(R.id.cardContainer);
            otherProducts = itemView.findViewById(R.id.otherProducts);
            status = itemView.findViewById(R.id.status);
        }

        public void bind(Order order, View view) {
            date.setText(String.format("%s %s", view.getContext().getString(R.string.order_in), order.getCreated_at_format()));
            totalAmount.setText(String.format("%.2f€", order.getTotal_amount()));
            totalItems.setText(String.format("%d items", order.getTotal_products()));
            orderId.setText(String.format("ID do pedido: %d", order.getId()));
            status.setText(order.getStatus().substring(0, 1).toUpperCase() + order.getStatus().substring(1).toLowerCase());

            switch (status.getText().toString()) {
                case "pendente":
                    status.setBackgroundResource(R.drawable.bg_pending);
                    break;
                case "pago":
                    status.setBackgroundResource(R.drawable.bg_complete);
                    break;
                case "cancelado":
                    status.setBackgroundResource(R.drawable.bg_error);
                    break;
            }

            if (order.getTotal_products() > 3) {
                otherProducts.setText(String.format("+%d", order.getTotal_products() - 3));
                cardContainer.setVisibility(View.VISIBLE);
            } else {
                cardContainer.setVisibility(View.GONE);
            }

            flexImages.removeAllViews();

            if (order.getImages().isEmpty()) {
                containerImages.setVisibility(View.GONE);
                return;
            } else {
                containerImages.setVisibility(View.VISIBLE);
            }

            LayoutInflater inflater = LayoutInflater.from(view.getContext());

            for (String item : order.getImages()) {
                View chipView = inflater.inflate(R.layout.item_image_view, flexImages, false);
                ImageView imageView = chipView.findViewById(R.id.image);

                Glide.with(view.getContext())
                        .load(item)
                        .placeholder(R.drawable.bg_skeleton)
                        .error(R.drawable.bg_skeleton)
                        .into(imageView);

                flexImages.addView(chipView);
            }

            // Quando o produto for clicado
            itemView.setOnClickListener(v -> {
                if (onOrderClicked != null) {
                    onOrderClicked.onProductClick(order);
                }
            });
        }
    }

    public void updateOrders(List<Order> newOrders) {
        orders.clear();
        orders.addAll(newOrders);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

}
