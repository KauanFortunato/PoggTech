package com.mordekai.poggtech.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.mordekai.poggtech.R;

import java.util.ArrayList;
import java.util.List;

public class PlaceSuggestionAdapter extends RecyclerView.Adapter<PlaceSuggestionAdapter.ViewHolder> {

    private List<AutocompletePrediction> predictions = new ArrayList<>();
    private final OnPlaceClickListener listener;

    public interface OnPlaceClickListener {
        void onPlaceClick(AutocompletePrediction prediction);
    }

    public PlaceSuggestionAdapter(OnPlaceClickListener listener) {
        this.listener = listener;
    }

    public void setPredictions(List<AutocompletePrediction> newList) {
        predictions = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AutocompletePrediction prediction = predictions.get(position);
        holder.placeName.setText(prediction.getFullText(null));
        holder.itemView.setOnClickListener(v -> listener.onPlaceClick(prediction));
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView placeName;
        public ViewHolder(View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.placeName);
        }
    }
}
