package com.mpip.chatstation.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mpip.chatstation.Adapters.ViewHolders.ChatMessageViewHolder;
import com.mpip.chatstation.Models.ChatMessage;
import com.mpip.chatstation.R;

import java.util.ArrayList;
import java.util.List;

import static com.mpip.chatstation.Adapters.ChatMessageAdapter.Type.MESSAGE;
import static com.mpip.chatstation.Adapters.ChatMessageAdapter.Type.MESSAGE_R;


public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageViewHolder> {

    List<ChatMessage> chatMessages;
    Context context;

    public ChatMessageAdapter(Context context) {
        super();
        this.context = context;
        chatMessages = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage cm = chatMessages.get(position);

        switch (cm.getType())
        {
            case JOIN: return Type.JOIN.ordinal();
            case LEAVE: return Type.LEAVE.ordinal();
            case TOSELF: return Type.TOSELF.ordinal();
            default:
            {
                if (!cm.isBelongsToCurrentUser())
                    return MESSAGE_R.ordinal();
                else
                    return MESSAGE.ordinal();
            }
        }
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        ChatMessageViewHolder holder;

        switch (Type.values()[viewType]){
            case MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_message, parent, false);
                break;
            case MESSAGE_R:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_message, parent, false);
                break;
            case JOIN:
            case LEAVE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_chat_message, parent, false);
                break;
            case TOSELF:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_self_message, parent, false);
                break;
        }

        holder = new ChatMessageViewHolder(view);
        holder.parent = view;
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        ChatMessage chatMsg = chatMessages.get(position);

        switch (chatMsg.getType()){
            case MESSAGE:
                if (chatMsg.isBelongsToCurrentUser()) {
                    holder.sentAt.setText(chatMsg.getSentAt());
                    holder.msgBody.setText(chatMsg.getText());

                } else { // this Message was sent by someone else so let's create an advanced chat bubble on the left
                    holder.name.setText(chatMsg.getUserData());
                    holder.msgBody.setText(chatMsg.getText());
                    holder.sentAt.setText(chatMsg.getSentAt());
                }
                break;
            case JOIN:
            case LEAVE:
                holder.sentAt.setText(chatMsg.getSentAt());
                holder.msgBody.setText(chatMsg.getText());
                break;
            case TOSELF:
                holder.msgBody.setText(chatMsg.getText());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void addChatMessage(ChatMessage msg) {
        this.chatMessages.add(msg);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    public void updateData(List<ChatMessage> msgs){
        this.chatMessages.clear();
        chatMessages.addAll(msgs);

        notifyDataSetChanged();
    }

    public enum Type
    {
        JOIN,
        LEAVE,
        TOSELF,
        MESSAGE,
        MESSAGE_R
    }


}