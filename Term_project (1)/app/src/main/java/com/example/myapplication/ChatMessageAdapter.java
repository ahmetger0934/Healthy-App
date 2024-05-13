package com.example.myapplication;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder> {
    private static final int MESSAGE_TYPE_SENDER = 1;
    private static final int MESSAGE_TYPE_RECEIVER = 2;

    private List<ChatMessage> messagesList;
    private String currentUserId;

    private OnMessageLongClickListener onLongClickListener;

    public interface OnMessageLongClickListener {
        void onMessageLongClick(ChatMessage message);
    }

    public void setOnMessageLongClickListener(OnMessageLongClickListener listener) {
        this.onLongClickListener = listener;
    }

    public ChatMessageAdapter(List<ChatMessage> messagesList, String currentUserId) {
        this.messagesList = messagesList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MESSAGE_TYPE_SENDER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sender, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_receiver, parent, false);
        }
        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        ChatMessage message = messagesList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messagesList.get(position);
        if (message.isSender(currentUserId)) {
            return MESSAGE_TYPE_SENDER;
        } else {
            return MESSAGE_TYPE_RECEIVER;
        }
    }

    class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView;
        private ImageView profileImageView;

        ChatMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_view_message);
            profileImageView = itemView.findViewById(R.id.image_view_profile);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return ChatMessageViewHolder.this.onLongClick(v);
                }
            });

        }


        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ChatMessage message = messagesList.get(position);
                if (onLongClickListener != null) {
                    onLongClickListener.onMessageLongClick(message);
                    return true;
                }
            }
            return false;
        }

        void bind(ChatMessage message) {
            messageTextView.setText(message.getText());

            String photoUrl;
            if (message.isSender(currentUserId)) {
                photoUrl = message.getSenderPhotoUrl();
            } else {
                photoUrl = message.getReceiverPhotoUrl();
            }

            if (photoUrl != null) {
                Glide.with(itemView.getContext())
                        .load(photoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .into(profileImageView);
            } else {
                Glide.with(itemView.getContext())
                        .load(R.drawable.profile_placeholder)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImageView);
            }
        }
    }
}
