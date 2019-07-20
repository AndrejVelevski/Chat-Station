package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Models.User;
import com.mpip.chatstation.Packets.FriendRequestPacket;
import com.mpip.chatstation.Packets.RequestRandomChatPacket;
import com.mpip.chatstation.Packets.RequestUserPacket;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.SystemMessagePacket;
import com.mpip.chatstation.R;

public class HomeActivity extends AppCompatActivity
{
    public static TextView tvWelcome;
    public static Button btnRandomChat;
    public static Button btnChangeState;
    public static TextView tvMessage;
    private static EditText etTags;
    private static EditText etFriendUsername;
    private static SeekBar sbMaxUsers;

    public static User user;

    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        KryoListener.currentActivity = this;

        tvWelcome = findViewById(R.id.tvHomeWelcome);
        btnRandomChat = findViewById(R.id.btnHomeRandomChat);
        btnChangeState = findViewById(R.id.btnHomeChangeState);
        tvMessage = findViewById(R.id.tvHomeMessage);
        etTags = findViewById(R.id.etHomeTags);
        etFriendUsername = findViewById(R.id.etHomeFriendUsername);
        sbMaxUsers = findViewById(R.id.sbHomeMaxUsers);

        user = new User();
        user.email = getIntent().getStringExtra(Constants.EMAIL);

        RequestUserPacket packet = new RequestUserPacket();
        packet.username_email = user.email;
        new SendPacketThread(packet).start();

        sbMaxUsers.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
            {
                btnChangeState.setText(String.format("Max users: %d", (int)Constants.map(0,100,3,20,sbMaxUsers.getProgress())));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
        RequestRandomChatPacket packet = new RequestRandomChatPacket();
        packet.tags = etTags.getText().toString().replaceAll("\\s+","");
        switch (state)
        {
            case 0:
            {
                packet.maxUsers = 2;
                break;
            }
            case 1:
            {
                packet.maxUsers = 0;
                break;
            }
            case 2:
            {
                packet.maxUsers = (int)Constants.map(0,100,3,20,sbMaxUsers.getProgress());
                break;
            }
        }

        new SendPacketThread(packet).start();
    }

    public void changeState(View view)
    {
        if (++state > 2)
        {
            state = 0;
        }
        switch (state)
        {
            case 0:
            {
                btnChangeState.setText("Max users: 2");
                sbMaxUsers.setVisibility(View.GONE);
                break;
            }
            case 1:
            {
                btnChangeState.setText("Max users: Any");
                sbMaxUsers.setVisibility(View.GONE);
                break;
            }
            case 2:
            {
                btnChangeState.setText(String.format("Max users: %d", (int)Constants.map(0,100,3,20,sbMaxUsers.getProgress())));
                sbMaxUsers.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    public void sendFriendRequest(View view)
    {
        FriendRequestPacket packet = new FriendRequestPacket();
        packet.user_from = user.username;
        packet.user_to = etFriendUsername.getText().toString();

        boolean error = false;

        if (packet.user_to.trim().length() == 0)
        {
            error = true;
            tvMessage.setTextColor(Color.rgb(255,0,0));
            tvMessage.setText("Username can't be empty.");
        }
        if (packet.user_from.equals(packet.user_to))
        {
            error = true;
            tvMessage.setTextColor(Color.rgb(255,0,0));
            tvMessage.setText("You can't send a friend request to yourself.");
        }

        if (!error)
        {
            new SendPacketThread(packet).start();
        }
    }

    public void friendRequests(View view)
    {
        Intent intent = new Intent(this, FriendRequestsActivity.class);
        startActivity(intent);
    }

    public void friends(View view)
    {
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }
}
