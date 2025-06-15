package com.mordekai.poggtech.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private Context context;
    private OnCategoryClickListerner listener;
    private final int layoutResId;

    public interface OnCategoryClickListerner {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(List<Category> categories, Context context, OnCategoryClickListerner listener, int layoutResId) {
        this.categories = categories;
        this.context = context;
        this.listener = listener;
        this.layoutResId = layoutResId;
    }

    public void updateCategories(List<Category> newCategories) {
        categories.clear();
        categories.addAll(newCategories);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutResId, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() { return categories.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView categoryName;
        ImageView categoryIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.categoryName);
            categoryIcon = itemView.findViewById(R.id.productImage);
        }

        void bind(Category category) {
            categoryName.setText(category.getName());

            String iconName = category.getIcon();
            int imageResId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());

            if (imageResId != 0) {
                categoryIcon.setImageResource(imageResId);
            } else {
                categoryIcon.setImageResource(R.drawable.ic_all_categories);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }
    }
}
