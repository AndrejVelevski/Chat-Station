package com.mpip.chatstation.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mpip.chatstation.Models.User;
import com.mpip.chatstation.R;

import java.util.ArrayList;
import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder> {

    protected class FriendListViewHolder extends RecyclerView.ViewHolder
    {
        public View parent;
        public Button btnFriendListPic;
        public ImageButton btnMsgFriend,btnFriendInfo;
        public TextView friendListFullName,friendListUsername;

        public FriendListViewHolder(@NonNull View view) {
            super(view);

            btnFriendListPic = view.findViewById(R.id.btnFriendListPic);
            btnFriendInfo = view.findViewById(R.id.btnFriendInfo);
            btnMsgFriend = view.findViewById(R.id.btnMsgFriend);
            friendListFullName = view.findViewById(R.id.friendListFullName);
            friendListUsername = view.findViewById(R.id.friendListUsername);

            btnFriendListPic.setOnClickListener(c->{
                onClickFriendProfile();
            });

            btnFriendInfo.setOnClickListener(c->{
                onClickFriendProfile();
            });

            btnMsgFriend.setOnClickListener(c->{
                onClickMsgFriend();
            });

        }

        public void onClickMsgFriend(){
            //otvori go chatot so prijatelot
        }

        private void onClickFriendProfile(){
            //odnesi go na kliknatiot profil
        }
    }

    private List<User> friends;

    public FriendListAdapter() {
        friends = new ArrayList<>();
    }

    @NonNull
    @Override
    public FriendListAdapter.FriendListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_item, parent, false);
        FriendListAdapter.FriendListViewHolder holder = new FriendListAdapter.FriendListViewHolder(view);
        holder.parent = view;
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.FriendListViewHolder holder, int position) {
        User user = friends.get(position);

        holder.btnFriendListPic.setText(user.getCharsForPic());
        holder.friendListUsername.setText(user.username);
        holder.friendListFullName.setText(user.getFullName());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void updateData(List<User> users)
    {
        this.friends.clear();
        friends.addAll(users);

        // this.friends.addAll(users);
        notifyDataSetChanged();
    }
}

