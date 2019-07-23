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

import com.mpip.chatstation.Adapters.FriendRequestsAdapter;
import com.mpip.chatstation.Models.User;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.RequestFriendRequestsPacket;
import com.mpip.chatstation.R;

public class FriendRequestsFragment extends Fragment implements View.OnClickListener {

    private static View view;
    private static FragmentActivity context;

    private static RecyclerView rvFriendRequests;
    public static FriendRequestsAdapter friendRequestsAdapter;
    private static RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_requests,container,false);
        initViews();
        context = getActivity();

        return view;
    }

    public FriendRequestsFragment() {
    }

    private void initFragment(){

    }

    private void initViews(){
        rvFriendRequests = view.findViewById(R.id.rvFriendRequests2);
        layoutManager = new LinearLayoutManager(context);
        rvFriendRequests.setLayoutManager(layoutManager);
        friendRequestsAdapter = new FriendRequestsAdapter();
        rvFriendRequests.setAdapter(friendRequestsAdapter);

        RequestFriendRequestsPacket packet = new RequestFriendRequestsPacket();
        packet.username = User.username;
        new SendPacketThread(packet).start();
    }

    @Override
    public void onClick(View view) {

    }
}
