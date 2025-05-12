package com.mordekai.poggtech.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private List<Message> messageList;
    private int currentUserId;

    public MessageAdapter(List<Message> messageList, int currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSender_id() == currentUserId) {
            return TYPE_SENT; // Mensagem enviada pelo utilizador (alinhada à direita)
        } else {
            return TYPE_RECEIVED; // Mensagem recebida (alinhada à esquerda)
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder.getItemViewType() == TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder para mensagens enviadas
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessageSent, tvTimeSent;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            tvMessageSent = itemView.findViewById(R.id.tvMessageSent);
            tvTimeSent = itemView.findViewById(R.id.tvTimeSent);
        }

        void bind(Message message) {
            tvMessageSent.setText(message.getMessage());
            tvTimeSent.setText(message.getTimestamp_format());
        }
    }

    // ViewHolder para mensagens recebidas
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessageReceived, tvTimeReceived;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            tvMessageReceived = itemView.findViewById(R.id.tvMessageReceived);
            tvTimeReceived = itemView.findViewById(R.id.tvTimeReceived);
        }

        void bind(Message message) {
            tvMessageReceived.setText(message.getMessage());
            tvTimeReceived.setText(message.getTimestamp_format());
        }
    }
}
