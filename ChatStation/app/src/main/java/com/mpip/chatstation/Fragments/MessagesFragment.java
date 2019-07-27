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
import com.mpip.chatstation.Adapters.MessagesHistoryAdapter;
import com.mpip.chatstation.Packets.LastMessagePacket;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.RequestLastMessagesPacket;
import com.mpip.chatstation.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagesFragment extends Fragment implements View.OnClickListener {

    private static View view;
    private static FragmentActivity context;

    private static RecyclerView rvLastMesseges;
    public static  MessagesHistoryAdapter lastMessagesAdapter;
    private static RecyclerView.LayoutManager layoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_messages,container,false);
        initViews();
        context = getActivity();

        RequestLastMessagesPacket packet = new RequestLastMessagesPacket();
        packet.username = NavUiMainActivity.user.username;
        new SendPacketThread(packet).start();

        return view;
    }

    public MessagesFragment() {
    }


    private void initViews(){
        rvLastMesseges = view.findViewById(R.id.rvLastMesseges);
        layoutManager = new LinearLayoutManager(context);
        rvLastMesseges.setLayoutManager(layoutManager);
        lastMessagesAdapter = new MessagesHistoryAdapter();
        rvLastMesseges.setAdapter(lastMessagesAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }


    public static void showError(String msg){
        context.runOnUiThread( () -> {
            new CustomToast().Show_Toast(context, view,
                    msg);
        });
    }
}
