package com.mpip.chatstation.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.mpip.chatstation.Activities.NavUiMainActivity;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Models.User;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.FriendRequestPacket;
import com.mpip.chatstation.Packets.RequestRandomChatPacket;
import com.mpip.chatstation.R;

//Temporary here, will be moved to Home Fragment
public class HomeFragment extends Fragment implements View.OnClickListener {

    private static View view;
    private static FragmentActivity context;
    private LinearLayout viewRandomChat, viewMaxUsers, viewUserInfo;
    private TextView textHomeChangeStateMaxUsers;
    private EditText etHomeAddFriend, etHomeChatTags;
    private SeekBar sbHomeMaxUsers2;
    private static ScrollView svHome;
    private ImageButton btnHomeSendFriendRequest2;
    private static Animation shakeAnimation;

    private int state = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home2,container,false);
        initViews();
        setListeners();
        context = getActivity();

        return view;
    }

    public HomeFragment() {
    }

    private void initViews() {
        viewRandomChat = view.findViewById(R.id.viewRandomChat);
        viewMaxUsers = view.findViewById(R.id.viewMaxUsers);
        viewUserInfo = view.findViewById(R.id.viewUserInfo);
        textHomeChangeStateMaxUsers = view.findViewById(R.id.textHomeChangeStateMaxUsers);
        etHomeAddFriend = view.findViewById(R.id.etHomeAddFriend);
        etHomeChatTags = view.findViewById(R.id.etHomeChatTags);
        sbHomeMaxUsers2 = view.findViewById(R.id.sbHomeMaxUsers2);
        btnHomeSendFriendRequest2 = view.findViewById(R.id.btnHomeSendFriendRequest2);
        svHome = view.findViewById(R.id.svHome);
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);
    }

    // Set Listeners
    private void setListeners() {
        viewRandomChat.setOnClickListener(this);
        viewMaxUsers.setOnClickListener(this);
        viewUserInfo.setOnClickListener(this);
        btnHomeSendFriendRequest2.setOnClickListener(this);


        sbHomeMaxUsers2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
            {
                textHomeChangeStateMaxUsers.setText(String.format("Max users: %d", (int)Constants.map(0,100,3,20,sbHomeMaxUsers2.getProgress())));
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewRandomChat:
                randomChat();
                break;
            case R.id.viewMaxUsers:
                changeState();
                break;
            case R.id.btnHomeSendFriendRequest2:
                sendFriendRequest();
                break;
            case R.id.viewUserInfo:
                //open user's info fragment/activity
                break;
        }
    }

    public void randomChat()
    {
        RequestRandomChatPacket packet = new RequestRandomChatPacket();
        packet.tags = etHomeChatTags.getText().toString().replaceAll("\\s+","");
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
                packet.maxUsers = (int) Constants.map(0,100,3,20,sbHomeMaxUsers2.getProgress());
                break;
            }
        }

        new SendPacketThread(packet).start();
    }

    public void changeState()
    {
        if (++state > 2)
        {
            state = 0;
        }
        switch (state)
        {
            case 0:
            {
                textHomeChangeStateMaxUsers.setText("Max users: 2");
                sbHomeMaxUsers2.setVisibility(View.GONE);
                break;
            }
            case 1:
            {
                textHomeChangeStateMaxUsers.setText("Max users: Any");
                sbHomeMaxUsers2.setVisibility(View.GONE);
                break;
            }
            case 2:
            {
                textHomeChangeStateMaxUsers.setText(String.format("Max users: %d", (int)Constants.map(0,100,3,20,sbHomeMaxUsers2.getProgress())));
                sbHomeMaxUsers2.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    public void sendFriendRequest()
    {
        FriendRequestPacket packet = new FriendRequestPacket();
        packet.user_from = NavUiMainActivity.user.username;
        packet.user_to = etHomeAddFriend.getText().toString();

        boolean error = false;

        if (packet.user_to.trim().length() == 0)
        {
            error = true;
            showError("Username can't be empty.");
        }
        if (packet.user_from.equals(packet.user_to))
        {
            error = true;
            showError("You can't send a friend request to yourself.");
        }
        if (!error)
        {
            new SendPacketThread(packet).start();
        }
    }

    public static void showError(String msg){
        context.runOnUiThread( () -> {
            svHome.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(context, view,
                    msg);
        });
    }
    public static void showSuccess(String msg){
        context.runOnUiThread( () -> {
            new CustomToast().Show_Toast(context, view,
                    msg);
        });
    }
}
