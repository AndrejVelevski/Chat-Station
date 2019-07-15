package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Config.UserPacketType;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.UserPacket;
import com.mpip.chatstation.R;

public class HomeActivity extends AppCompatActivity
{
    public static TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        KryoListener.currentActivity = this;

        tvWelcome = findViewById(R.id.tvHomeWelcome);

        UserPacket user = new UserPacket();
        user.type = UserPacketType.REQUEST_USER;
        user.email = getIntent().getStringExtra(Constants.EMAIL);
        new SendPacketThread(user).start();
    }
}
