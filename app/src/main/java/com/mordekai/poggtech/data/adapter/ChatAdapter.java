package com.mordekai.poggtech.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private final List<Chat> chats;
    private final int userId;

    public ChatAdapter(List<Chat> chats, int userId) {
        this.chats = chats;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_chat, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        Chat chat = chats.get(position);

        holder.productTitle.setText(chat.getProduct_title());
        holder.sellerName.setText(chat.getChat_with_name() + " " + chat.getChat_with_last_name());
        holder.lastMessage.setText(chat.getLast_message());
        holder.messageTime.setText(chat.getLast_message_time_format());
//        holder.newMessageIcon.setVisibility(View.GONE);

        Glide.with(
                        holder.imageProduct.getContext())
                .load(chat.getImage_product())
                .into(holder.imageProduct);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView productTitle;
        TextView sellerName;
        TextView lastMessage;
        TextView messageTime;
//        View newMessageIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProduct = itemView.findViewById(R.id.imageProduct);
            productTitle = itemView.findViewById(R.id.productTitle);
            sellerName = itemView.findViewById(R.id.sellerName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            messageTime = itemView.findViewById(R.id.messageTime);
//            newMessageIcon = itemView.findViewById(R.id.newMessageIcon);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
