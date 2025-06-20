package com.mordekai.poggtech.data.adapter;

import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.Order;
import com.mordekai.poggtech.data.model.OrderItem;
import com.mordekai.poggtech.presentation.ui.bottomsheets.LeaveReviewBottomSheet;
import com.mordekai.poggtech.utils.Utils;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private final List<OrderItem> orderItems;
    private final FragmentManager fragmentManager;

    public interface OnReviewSentListener {
        void onReviewSent(int product_id, int rating, String reviewText);

    }

    public OrderDetailAdapter.OnReviewSentListener reviewSentListener;


    public OrderDetailAdapter(List<OrderItem> orderItems, FragmentManager fragmentManager) {
        this.orderItems = orderItems;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public OrderDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail, parent, false);

        return new OrderDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailAdapter.ViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        Log.d("BIND", "Exibindo: " + orderItem.getProduct_title());
        holder.bind(orderItem);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public void updateOrderItems(List<OrderItem> newOrderItems) {
        orderItems.clear();
        orderItems.addAll(newOrderItems);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView productImage;
        private final TextView titleProduct;
        private final TextView category;
        private final TextView price;
        private final TextView quantity;
        private final AppCompatButton leaveReviewBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            titleProduct = itemView.findViewById(R.id.titleProduct);
            category = itemView.findViewById(R.id.category);
            price = itemView.findViewById(R.id.price);
            leaveReviewBtn = itemView.findViewById(R.id.leaveReviewBtn);
            quantity = itemView.findViewById(R.id.quantity);
        }

        public void bind(OrderItem orderItem) {
            Utils.loadImageBasicAuth(productImage, orderItem.getProduct_cover());

            titleProduct.setText(orderItem.getProduct_title());
            category.setText(orderItem.getProduct_category());
            price.setText(String.format("%.2f€", orderItem.getUnit_price()));
            quantity.setText(String.valueOf(orderItem.getQuantity()));

            leaveReviewBtn.setOnClickListener(v -> {
                if (v.isHapticFeedbackEnabled()) {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                }

                showDialogLeaveReview(orderItem);
            });
        }
    }

    private void showDialogLeaveReview(OrderItem orderItem) {
        LeaveReviewBottomSheet leaveReviewBottomSheet = new LeaveReviewBottomSheet();
        leaveReviewBottomSheet.setOnReviewSubmittedListener((rating, reviewText) -> {
            if (reviewSentListener != null) {
                reviewSentListener.onReviewSent(orderItem.getProduct_id(), rating, reviewText);
            }
        });
        leaveReviewBottomSheet.show(fragmentManager, "LeaveReviewBottomSheet");
    }
}
