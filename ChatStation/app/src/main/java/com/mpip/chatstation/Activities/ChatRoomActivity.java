package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mpip.chatstation.Adapters.MessageBoxAdapter;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.MessagePacket;
import com.mpip.chatstation.Packets.SystemMessagePacket;
import com.mpip.chatstation.R;

import java.util.ArrayList;
import java.util.List;


public class ChatRoomActivity extends AppCompatActivity
{

    public static RecyclerView.Adapter mbAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public static RecyclerView rvMessageBox;
    private static EditText etMessage;

    public static final List<String> data = new ArrayList<String>();;

    private MessagePacket message;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        rvMessageBox = findViewById(R.id.rvChatRoomMessageBox);
        layoutManager = new LinearLayoutManager(this);
        rvMessageBox.setLayoutManager(layoutManager);
        mbAdapter = new MessageBoxAdapter(data);
        rvMessageBox.setAdapter(mbAdapter);
        data.clear();

        etMessage = findViewById(R.id.etChatRoomMessage);

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

    public void send(View view)
    {
        message.type = MessagePacket.Type.MESSAGE;
        message.message = etMessage.getText().toString();
        etMessage.setText("");
        if (message.message.trim().length() > 0)
        {
            new SendPacketThread(message).start();
        }
    }
}