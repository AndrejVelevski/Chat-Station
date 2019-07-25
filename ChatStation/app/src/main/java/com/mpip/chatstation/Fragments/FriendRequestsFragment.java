package com.mpip.chatstation.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mpip.chatstation.Activities.NavUiMainActivity;
import com.mpip.chatstation.Adapters.FriendListAdapter;
import com.mpip.chatstation.Adapters.FriendRequestsAdapter;
import com.mpip.chatstation.Models.User;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.FriendRequestPacket;
import com.mpip.chatstation.Packets.RequestFriendRequestsPacket;
import com.mpip.chatstation.Packets.RequestFriendsPacket;
import com.mpip.chatstation.R;

public class FriendRequestsFragment extends Fragment implements View.OnClickListener {

    private static View view;
    private static FragmentActivity context;

    private static RecyclerView rvFriendRequests;
    public static FriendRequestsAdapter friendRequestsAdapter;
    private static RecyclerView.LayoutManager layoutManager;

    //Add Friend
    private ImageButton btnFriendRequestSendFriendRequest;
    private EditText etFriendRequestAddFriend;

    //Friend List View
    private static RecyclerView rvFriendList;
    public static FriendListAdapter friendListAdapter;
    private static RecyclerView.LayoutManager layoutManager2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_requests,container,false);
        initViews();
        initFragment();
        context = getActivity();

        return view;
    }

    public FriendRequestsFragment() {
    }

    private void initFragment(){
        btnFriendRequestSendFriendRequest.setOnClickListener(this);
    }

    private void initViews(){
        rvFriendRequests = view.findViewById(R.id.rvFriendRequests2);
        layoutManager = new LinearLayoutManager(context);
        rvFriendRequests.setLayoutManager(layoutManager);
        friendRequestsAdapter = new FriendRequestsAdapter();
        rvFriendRequests.setAdapter(friendRequestsAdapter);

        // svFriendRequests = view.findViewById(R.id.svFriendRequests);

        //Add Friend
        btnFriendRequestSendFriendRequest = view.findViewById(R.id.btnFriendRequestSendFriendRequest);
        etFriendRequestAddFriend = view.findViewById(R.id.etFriendRequestAddFriend);

        //Friend List view
        rvFriendList = view.findViewById(R.id.rvFriendList2);
        layoutManager2 = new LinearLayoutManager(context);
        rvFriendList.setLayoutManager(layoutManager2);
        friendListAdapter = new FriendListAdapter();
        rvFriendList.setAdapter(friendListAdapter);



        RequestFriendRequestsPacket packet = new RequestFriendRequestsPacket();
        packet.username = NavUiMainActivity.user.username;
        new SendPacketThread(packet).start();

        RequestFriendsPacket packet2 = new RequestFriendsPacket();
        packet2.username = NavUiMainActivity.user.username;

        new SendPacketThread(packet2).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFriendRequestSendFriendRequest:
                sendFriendRequest();
                break;
        }
    }

    public void sendFriendRequest()
    {
        FriendRequestPacket packet = new FriendRequestPacket();
        packet.user_from = NavUiMainActivity.user.username;
        packet.user_to = etFriendRequestAddFriend.getText().toString();

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
            new CustomToast().Show_Toast(context, view,
                    msg);
        });
    }
}
