package com.mpip.chatstation.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mpip.chatstation.Activities.NavUiMainActivity;
import com.mpip.chatstation.Adapters.FriendListAdapter;
import com.mpip.chatstation.Models.User;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.RequestFriendsPacket;
import com.mpip.chatstation.R;

public class FriendsListFragment extends Fragment implements View.OnClickListener {

    private static View view;
    private static FragmentActivity context;

    private static RecyclerView rvFriendList;
    public static FriendListAdapter friendListAdapter;
    private static RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_requests,container,false);
        initViews();
        context = getActivity();

        return view;
    }

    public FriendsListFragment() {
    }

    private void initFragment(){

    }

    private void initViews(){
        rvFriendList = view.findViewById(R.id.rvFriendRequests2);
        layoutManager = new LinearLayoutManager(context);
        rvFriendList.setLayoutManager(layoutManager);
        friendListAdapter = new FriendListAdapter();
        rvFriendList.setAdapter(friendListAdapter);

        RequestFriendsPacket packet = new RequestFriendsPacket();
        packet.username = NavUiMainActivity.user.username;

        new SendPacketThread(packet).start();
    }

    @Override
    public void onClick(View view) {

    }
}
