package com.mordekai.poggtech.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.Message;
import com.mordekai.poggtech.data.model.MessageDateSeparator;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;
    private static final int TYPE_DATE_SEPARATOR = 3;

    private List<Message> messageList;
    private int currentUserId;

    public MessageAdapter(List<Message> messageList, int currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message instanceof MessageDateSeparator) {
            return TYPE_DATE_SEPARATOR;
        } else if (message.getSender_id() == currentUserId) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_SENT) {
            return new SentMessageViewHolder(inflater.inflate(R.layout.item_message_sent, parent, false));
        } else if (viewType == TYPE_RECEIVED) {
            return new ReceivedMessageViewHolder(inflater.inflate(R.layout.item_message_received, parent, false));
        } else {
            return new DateSeparatorViewHolder(inflater.inflate(R.layout.item_message_date_separator, parent, false));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        } else if (holder instanceof DateSeparatorViewHolder) {
            ((DateSeparatorViewHolder) holder).bind((MessageDateSeparator) message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder para mensagens enviadas
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessageSent, tvTimeSent;
        AppCompatImageView ivCheck;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            tvMessageSent = itemView.findViewById(R.id.tvMessageSent);
            tvTimeSent = itemView.findViewById(R.id.tvTimeSent);
            ivCheck = itemView.findViewById(R.id.ivCheck);
        }

        void bind(Message message) {
            tvMessageSent.setText(message.getMessage());
            tvTimeSent.setText(message.getTimestamp_format());

            if (message.isPending()) {
                ivCheck.setImageResource(R.drawable.ic_check);
            } else {
                ivCheck.setImageResource(R.drawable.ic_check_all);
            }

            if (message.getIs_read() == 1) {
                ivCheck.setColorFilter(ivCheck.getContext().getResources().getColor(R.color.colorPrimary));
            } else {
                ivCheck.setColorFilter(ivCheck.getContext().getResources().getColor(R.color.textPrimary));
            }
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

    // ViewHolder para separadores de data
    static class DateSeparatorViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateSeparator;

        DateSeparatorViewHolder(View itemView) {
            super(itemView);
            tvDateSeparator = itemView.findViewById(R.id.tvDateSeparator);
        }

        void bind(MessageDateSeparator separator) {
            tvDateSeparator.setText(separator.getDateLabel());
        }
    }

}
