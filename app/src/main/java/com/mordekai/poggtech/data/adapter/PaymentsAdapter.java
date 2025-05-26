package com.mordekai.poggtech.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.Payments;
import com.mordekai.poggtech.data.model.Product;

import java.util.List;

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.ViewHolder> {

    private List<Payments> payments;

    public PaymentsAdapter(List<Payments> payments) {
        this.payments = payments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment, parent, false);

        return new PaymentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentsAdapter.ViewHolder holder, int position) {
        Payments payment = payments.get(position);
        holder.bind(payment, holder.itemView);
    }

    @Override
    public int getItemCount() { return payments.size(); }

    public void updatePayments(List<Payments> newPayments) {
        this.payments.clear();
        this.payments.addAll(newPayments);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView paymentDate, numberPayment, amount, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            paymentDate = itemView.findViewById(R.id.paymentDate);
            numberPayment = itemView.findViewById(R.id.numberPayment);
            amount = itemView.findViewById(R.id.amount);
            status = itemView.findViewById(R.id.status);
        }

        public void bind(Payments payment, View view) {
            paymentDate.setText(payment.getCreatedAtFormatted());
            numberPayment.setText(view.getContext().getString(R.string.numPedido) + ": " + String.valueOf(payment.getOrderId()));
            amount.setText(String.format("-EUR %.2f€", payment.getAmount()));

            switch (payment.getStatus()) {
                case "pendente":
                    status.setTextColor(view.getContext().getResources().getColor(R.color.colorAccent));
                    break;
                case "concluido":
                    status.setTextColor(view.getContext().getResources().getColor(R.color.colorSuccess));
                    break;
                case "falhou":
                    status.setTextColor(view.getContext().getResources().getColor(R.color.colorError));
                    break;
            }

            status.setText(payment.getStatus());
        }
    }
}
