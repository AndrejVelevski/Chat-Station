package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.mpip.chatstation.Adapters.ChatMessageAdapter;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Models.User;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.MessagePacket;
import com.mpip.chatstation.R;


public class ChatRoomActivity extends AppCompatActivity
{

    private static EditText etMessage;
    public static ChatMessageAdapter messageAdapter;
    public static ListView lvMessageBox;

    private MessagePacket message;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        KryoListener.currentActivity = this;

        lvMessageBox = findViewById(R.id.lvChatRoomMessageBox);
        messageAdapter = new ChatMessageAdapter(ChatRoomActivity.this);
        etMessage = findViewById(R.id.etChatRoomMessage);
        lvMessageBox.setAdapter(messageAdapter);

        message = new MessagePacket();
        message.username = NavUiMainActivity.user.username;

        message.type = MessagePacket.Type.TOSELF;
        message.message = getIntent().getStringExtra(Constants.MESSAGE);
        new SendPacketThread(message).start();

        MessagePacket tmp = new MessagePacket();
        tmp.username = NavUiMainActivity.user.username;
        tmp.type = MessagePacket.Type.JOIN;
        tmp.message = String.format("%s has entered the chat.", NavUiMainActivity.user.username);
        if (getIntent().getStringExtra(Constants.ROOM_TAGS).length() > 0)
        {
            tmp.message += String.format("\n%s likes: %s", NavUiMainActivity.user.username, getIntent().getStringExtra(Constants.MATCHING_TAGS));
        }
        new SendPacketThread(tmp).start();
    }

    @Override
    public void onBackPressed()
    {
        message.type = MessagePacket.Type.LEAVE;
        message.message = String.format("%s has left the chat.", NavUiMainActivity.user.username);
        new SendPacketThread(message).start();
        finish();
    }

    public void sendMessage(View view) {
        String msgText= etMessage.getText().toString();
        message.type = MessagePacket.Type.MESSAGE;
        message.message = msgText;

        if (msgText.trim().length() > 0)
        {
            new SendPacketThread(message).start();
            etMessage.getText().clear();
        }
    }
}