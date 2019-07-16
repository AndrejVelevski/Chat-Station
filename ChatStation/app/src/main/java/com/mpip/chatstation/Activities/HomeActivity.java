package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Models.User;
import com.mpip.chatstation.Packets.RequestUserPacket;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.SystemMessagePacket;
import com.mpip.chatstation.R;

public class HomeActivity extends AppCompatActivity
{
    public static TextView tvWelcome;
    public static Button btnRandomChat;
    public static User user;
    public static boolean requested;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        KryoListener.currentActivity = this;

        tvWelcome = findViewById(R.id.tvHomeWelcome);
        btnRandomChat = findViewById(R.id.btnHomeRandomChat);
        user = new User();
        user.email = getIntent().getStringExtra(Constants.EMAIL);
        requested = false;

        RequestUserPacket packet = new RequestUserPacket();
        packet.username_email = user.email;
        new SendPacketThread(packet).start();
    }

    @Override
    public void onBackPressed()
    {
        SystemMessagePacket packet = new SystemMessagePacket();
        packet.type = SystemMessagePacket.Type.LOGOUT;
        packet.message = String.format("User %s logged out.", user.email);
        new SendPacketThread(packet).start();
        finish();
    }

    public void randomChat(View view)
    {
        if (!requested)
        {
            SystemMessagePacket packet = new SystemMessagePacket();
            packet.type = SystemMessagePacket.Type.REQUEST_RANDOM_CHAT;
            packet.message = String.format("%s requests to be added to the queue.", user.username);
            new SendPacketThread(packet).start();
            requested = true;
            btnRandomChat.setText("Cancel");
        }
        else
        {
            SystemMessagePacket packet = new SystemMessagePacket();
            packet.type = SystemMessagePacket.Type.STOP_RANDOM_CHAT;
            packet.message = String.format("%s requests to be removed from the queue.", user.username);
            new SendPacketThread(packet).start();
            requested = false;
            btnRandomChat.setText("Random Chat");
        }

    }
}
