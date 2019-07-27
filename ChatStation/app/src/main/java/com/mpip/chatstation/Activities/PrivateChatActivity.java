package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.mpip.chatstation.Adapters.ChatMessageAdapter;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.PrivateMessagePacket;
import com.mpip.chatstation.Packets.RequestMessagesHistoryPacket;
import com.mpip.chatstation.R;

public class PrivateChatActivity extends AppCompatActivity
{
    private String user_to;
    private EditText etMessage;
    private PrivateMessagePacket message;

    public static ChatMessageAdapter messageAdapter;
    public static ListView lvMessageBox;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        KryoListener.currentActivity = this;

        user_to = getIntent().getStringExtra(Constants.USERNAME);
        etMessage = findViewById(R.id.etChatRoomMessage);
        message = new PrivateMessagePacket();

        lvMessageBox = findViewById(R.id.lvChatRoomMessageBox);
        messageAdapter = new ChatMessageAdapter(PrivateChatActivity.this);
        lvMessageBox.setAdapter(messageAdapter);

        RequestMessagesHistoryPacket packet = new RequestMessagesHistoryPacket();
        packet.user_from = NavUiMainActivity.user.username;
        packet.user_to = user_to;
        new SendPacketThread(packet).start();
    }

    public void sendMessage(View view)
    {
        if (etMessage.getText().toString().trim().length() > 0)
        {
            message.user_from = NavUiMainActivity.user.username;
            message.user_to = user_to;
            message.message = etMessage.getText().toString();
            new SendPacketThread(message).start();
            etMessage.setText("");
        }
    }
}
