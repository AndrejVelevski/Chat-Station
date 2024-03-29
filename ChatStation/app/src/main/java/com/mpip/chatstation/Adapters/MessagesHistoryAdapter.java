package com.mpip.chatstation.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mpip.chatstation.Activities.PrivateChatActivity;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Packets.LastMessagePacket;
import com.mpip.chatstation.R;

import java.util.ArrayList;
import java.util.List;

public class MessagesHistoryAdapter extends RecyclerView.Adapter<MessagesHistoryAdapter.MessageHistoryViewHolder> {
    protected class MessageHistoryViewHolder extends RecyclerView.ViewHolder{
        public View parent;
        public LinearLayout llMessageItem;
        public TextView userName, lastMsg, dateAt;

        public MessageHistoryViewHolder(View view){
            super(view);
            userName = view.findViewById(R.id.messegesFriendName);
            lastMsg = view.findViewById(R.id.messegesLastMsg);
            dateAt = view.findViewById(R.id.dateLastMessage);
            llMessageItem = view.findViewById(R.id.llMessageItem);

            llMessageItem.setOnClickListener(l->{
                Intent intent = new Intent(parent.getContext(), PrivateChatActivity.class);
                intent.putExtra(Constants.USERNAME, userName.getText().toString());
                parent.getContext().startActivity(intent);
            });

        }

    }

    private List<LastMessagePacket> messagesHistory;

    public MessagesHistoryAdapter(){messagesHistory = new ArrayList<>(); }

    @NonNull
    @Override
    public MessageHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_messeges_item, parent, false);
        MessagesHistoryAdapter.MessageHistoryViewHolder holder = new MessagesHistoryAdapter.MessageHistoryViewHolder(view);
        holder.parent = view;
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHistoryViewHolder holder, int position) {
        LastMessagePacket lastMsg = messagesHistory.get(position);

        holder.userName.setText(lastMsg.username);
        holder.lastMsg.setText(lastMsg.message);
        holder.dateAt.setText(lastMsg.date);
    }

    @Override
    public int getItemCount() {
        return messagesHistory.size();
    }

    public void updateData(List<LastMessagePacket> lastMsgs)
    {
        this.messagesHistory.clear();
        messagesHistory.addAll(lastMsgs);

        notifyDataSetChanged();
    }
}
