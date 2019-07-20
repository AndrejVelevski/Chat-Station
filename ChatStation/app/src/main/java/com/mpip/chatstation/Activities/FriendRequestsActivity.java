package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.mpip.chatstation.Adapters.FriendRequestsAdapter;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.RequestFriendRequestsPacket;
import com.mpip.chatstation.R;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestsActivity extends AppCompatActivity
{
    private static RecyclerView rvFriendRequests;
    public static FriendRequestsAdapter friendRequestsAdapter;
    private static RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        KryoListener.currentActivity = this;

        rvFriendRequests = findViewById(R.id.rvFriendRequests);
        layoutManager = new LinearLayoutManager(this);
        rvFriendRequests.setLayoutManager(layoutManager);
        friendRequestsAdapter = new FriendRequestsAdapter();
        rvFriendRequests.setAdapter(friendRequestsAdapter);

        RequestFriendRequestsPacket packet = new RequestFriendRequestsPacket();
        packet.username = HomeActivity.user.username;
        new SendPacketThread(packet).start();
    }
}
