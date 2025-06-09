package com.mordekai.poggtech.data.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setText("FUNCIONA");
        tv.setBackgroundColor(0xFFFF0000); // Vermelho
        tv.setHeight(100);
        tv.setWidth(300);
        return new ViewHolder(tv);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { }
    @Override
    public int getItemCount() { return 10; }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) { super(itemView); }
    }
}
