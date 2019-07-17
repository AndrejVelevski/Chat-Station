package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.mpip.chatstation.Adapters.ChatMessageAdapter;
import com.mpip.chatstation.Models.ChatMessage;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.MessagePacket;
import com.mpip.chatstation.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatRoomActivity extends AppCompatActivity
{

    private static EditText etMessage;
    public static ChatMessageAdapter messageAdapter;
    public static ListView messagesView;

    public static final List<String> data = new ArrayList<String>();;

    private MessagePacket message;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        messagesView = findViewById(R.id.messageBox);
        messageAdapter = new ChatMessageAdapter(ChatRoomActivity.this);
        etMessage = findViewById(R.id.etMessage);
        messagesView.setAdapter(messageAdapter);

        data.clear();

        message = new MessagePacket();
        message.username = HomeActivity.user.username;
        message.type = MessagePacket.Type.JOIN;
        message.message = String.format("User %s has entered the chat.", HomeActivity.user.username);

        new SendPacketThread(message).start();
    }

    @Override
    public void onBackPressed()
    {
        HomeActivity.requested = false;
        HomeActivity.btnRandomChat.setText("Random Chat");
        message.type = MessagePacket.Type.LEAVE;
        message.message = String.format("User %s has left the chat.", HomeActivity.user.username);
        new SendPacketThread(message).start();
        finish();
    }

    public void sendMessage(View view) {
        String msgText= etMessage.getText().toString();
        message.type = MessagePacket.Type.MESSAGE;
        message.message = msgText;

        if (msgText.trim().length() > 0) {

            runOnUiThread(()->{
                messageAdapter.add(new ChatMessage("sda", HomeActivity.user.username, true, new Date().toString()));
                // scroll the ListView to the last added element
                messagesView.setSelection(messagesView.getCount() - 1);

                new SendPacketThread(message).start();
            });

            etMessage.getText().clear();
        }
    }
}