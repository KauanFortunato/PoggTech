package com.mordekai.poggtech.data.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.utils.Utils;

import java.util.List;

import okhttp3.internal.Util;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private List<String> imageUrls;
    private final Context context;

    public GalleryAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery_image, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        Utils.loadImageBasicAuth(holder.imageView, imageUrl);

        holder.imageView.setOnClickListener(v -> {
            showImageDialog(imageUrl);
        });

    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewGallery);
        }
    }

    public void updateImages(List<String> newImages) {
        this.imageUrls = newImages;
        notifyDataSetChanged();
    }

    private void showImageDialog(String imageUrl) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_zoom);

        ImageView imageView = dialog.findViewById(R.id.imageViewZoom);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        Utils.loadImageBasicAuth(imageView, imageUrl);

        btnClose.setOnClickListener(v -> { dialog.dismiss(); });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
