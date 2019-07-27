package com.mpip.chatstation.Adapters.ViewHolders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mpip.chatstation.R;

public class Server_ChatMessageViewHolder extends RecyclerView.ViewHolder
{
    public View parent;
    public TextView msgBody, sentAt;
    //site elementi od layoutot

    public Server_ChatMessageViewHolder(View view){
        super(view);
        //init elementite i onClick
        msgBody=  view.findViewById(R.id.chatMessage_body);
        sentAt = view.findViewById(R.id.msgSentAt);
    }
}