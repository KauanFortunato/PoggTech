package com.mordekai.poggtech.data.adapter;

import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<String> historyList;
    private SharedPrefHelper sharedPrefHelper;
    private OnHistoryClickListener listener;

    public HistoryAdapter(List<String> historyList, SharedPrefHelper sharedPrefHelper, OnHistoryClickListener listener) {
        this.historyList = historyList;
        this.sharedPrefHelper = sharedPrefHelper;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recently_searched, parent, false);
        return new HistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        String history = historyList.get(position);
        holder.bind(history, position);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView historyText;
        ImageButton dellSearch;
        LinearLayout historyItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            historyText = itemView.findViewById(R.id.historyText);
            dellSearch = itemView.findViewById(R.id.dellSearch);
            historyItem = itemView.findViewById(R.id.historyItem);
        }

        void bind(String history, int position) {
            historyText.setText(history);

            historyItem.setOnClickListener(v -> {
                if(historyItem.isHapticFeedbackEnabled()) {
                    historyItem.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                }

                listener.onHistoryClick(history);
            });

            dellSearch.setOnClickListener(v -> {
                if(dellSearch.isHapticFeedbackEnabled()) {
                    dellSearch.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                }

                sharedPrefHelper.removeItemFromHistory(history);
                historyList = sharedPrefHelper.getSearchHistory();
                notifyItemRemoved(position);
            });
        }
    }

    public interface OnHistoryClickListener {
        void onHistoryClick(String history);
    }
}
