package com.mpip.chatstation.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mpip.chatstation.Activities.NavUiMainActivity;
import com.mpip.chatstation.Models.User;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.FriendResponsePacket;
import com.mpip.chatstation.R;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.FriendRequestsViewHolder>
{
    protected class FriendRequestsViewHolder extends RecyclerView.ViewHolder
    {
        public View parent;
        public TextView tvUsername;
        public Button btnAccept;
        public Button btnDecline;

        public FriendRequestsViewHolder(View view)
        {
            super(view);
            tvUsername = view.findViewById(R.id.tvFriendRequestUsername2);
            btnAccept = view.findViewById(R.id.btnFriendRequestAccept2);
            btnDecline = view.findViewById(R.id.btnFriendRequestDecline2);

            btnAccept.setOnClickListener(listener ->
            {
                FriendResponsePacket packet = new FriendResponsePacket();
                packet.type = FriendResponsePacket.Type.ACCEPT;
                packet.user_from = tvUsername.getText().toString();
                packet.user_to = NavUiMainActivity.user.username;

                new SendPacketThread(packet).start();

                usernames.remove(packet.user_from);
                notifyDataSetChanged();
            });

            btnDecline.setOnClickListener(listener ->
            {
                FriendResponsePacket packet = new FriendResponsePacket();
                packet.type = FriendResponsePacket.Type.DECLINE;
                packet.user_from = tvUsername.getText().toString();
                packet.user_to = NavUiMainActivity.user.username;

                new SendPacketThread(packet).start();

                usernames.remove(packet.user_from);
                notifyDataSetChanged();
            });
        }
    }

    private List<String> usernames;

    public FriendRequestsAdapter()
    {
        usernames = new ArrayList<>();
    }

    @Override
    public FriendRequestsAdapter.FriendRequestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request, parent, false);
        FriendRequestsViewHolder holder = new FriendRequestsViewHolder(view);
        holder.parent = view;
        return holder;
    }

    @Override
    public void onBindViewHolder(FriendRequestsViewHolder holder, int position)
    {
        holder.tvUsername.setText(usernames.get(position));
    }

    @Override
    public int getItemCount()
    {
        return usernames.size();
    }

    public void updateData(List<String> usernames)
    {
        this.usernames.clear();
        this.usernames.addAll(usernames);
        notifyDataSetChanged();
    }

}