package com.mpip.chatstation.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mpip.chatstation.Models.ChatMessage;
import com.mpip.chatstation.R;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends BaseAdapter {

    List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
    Context context;

    public ChatMessageAdapter(Context context) {
        super();
        this.context = context;
    }

    public void add(ChatMessage msg) {
        this.chatMessages.add(msg);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public Object getItem(int i) {
        return chatMessages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ChatMessageViewHolder holder = new ChatMessageViewHolder();
        LayoutInflater testMessageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ChatMessage testMessage = chatMessages.get(i);

        if (testMessage.isBelongsToCurrentUser()) { // this Message was sent by us so let's create a basic chat bubble on the right
            convertView = testMessageInflater.inflate(R.layout.sender_message, null);
            holder.testMessageBody =  convertView.findViewById(R.id.chatMessage_body);
            holder.sentAt = convertView.findViewById(R.id.msgSentAt);

            convertView.setTag(holder);

            holder.sentAt.setText(testMessage.getSentAt());
            holder.testMessageBody.setText(testMessage.getText());

        } else { // this Message was sent by someone else so let's create an advanced chat bubble on the left
            convertView = testMessageInflater.inflate(R.layout.receiver_message, null);
            holder.name =  convertView.findViewById(R.id.receiverName);
            holder.testMessageBody =  convertView.findViewById(R.id.chatMessage_body);
            holder.sentAt = convertView.findViewById(R.id.msgSentAt);

            convertView.setTag(holder);

            holder.name.setText(testMessage.getUserData());
            holder.testMessageBody.setText(testMessage.getText());
            holder.sentAt.setText(testMessage.getSentAt());
        }

        return convertView;
    }

}

class ChatMessageViewHolder {
    public TextView name;
    public TextView testMessageBody;
    public TextView sentAt;
}