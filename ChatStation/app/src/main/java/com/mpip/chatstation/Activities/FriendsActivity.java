package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.RequestFriendsPacket;
import com.mpip.chatstation.R;

public class FriendsActivity extends AppCompatActivity
{
    public static TextView tvFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        KryoListener.currentActivity = this;

        tvFriends = findViewById(R.id.tvFriendsFriends);

        RequestFriendsPacket packet = new RequestFriendsPacket();
        packet.username = HomeActivity.user.username;

        new SendPacketThread(packet).start();
    }
}
