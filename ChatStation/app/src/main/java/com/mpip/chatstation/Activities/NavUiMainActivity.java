package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Fragments.FriendRequestsFragment;
import com.mpip.chatstation.Fragments.FriendsFragment;
import com.mpip.chatstation.Fragments.FriendsListFragment;
import com.mpip.chatstation.Fragments.HomeFragment;
import com.mpip.chatstation.Fragments.MessagesFragment;
import com.mpip.chatstation.Models.User;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.RequestUserPacket;
import com.mpip.chatstation.Packets.SystemMessagePacket;
import com.mpip.chatstation.R;

public class NavUiMainActivity extends AppCompatActivity
{

    AHBottomNavigation bottomNavigation;
    public static String username_email;

    public static User user;
    private int lastSelectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_ui_main);
        KryoListener.currentActivity = this;

        username_email = getIntent().getStringExtra(Constants.USERNAMEEMAIL);

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_nav);

        //Create Item
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Home", R.drawable.ic_home_black_24dp, android.R.color.white);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Messages", R.drawable.ic_message_black_24dp, android.R.color.white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Friends", R.drawable.ic_people_black_24dp, android.R.color.white);
        //AHBottomNavigationItem item4 = new AHBottomNavigationItem("Friend Requests", R.drawable.ic_people_black_24dp, android.R.color.white);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        //bottomNavigation.addItem(item4);

        // Set background color
        //bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#009688"));

        // Use colored navigation with circle reveal effect
        //bottomNavigation.setColored(true);

        //poceten fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
        lastSelectedFragment = 0;

        bottomNavigation.setOnTabSelectedListener((pos,isSelected)->{
            //Toast.makeText(NavUiMainActivity.this, "Start activity: " + pos, Toast.LENGTH_SHORT).show();


            Fragment selectedFragment = null;

            switch (pos){
                case 0:
                    if(lastSelectedFragment != 0) {
                        selectedFragment = new HomeFragment();
                        lastSelectedFragment = 0;
                    }
                    break;
                case 1:
                    if(lastSelectedFragment != 1){
                        selectedFragment = new MessagesFragment();
                        lastSelectedFragment = 1;
                    }
                    break;
                case 2:
                    if(lastSelectedFragment != 2)
                        selectedFragment = new FriendRequestsFragment();
                        lastSelectedFragment = 2;
                        break;

            }

            if(selectedFragment!=null){
                //this.getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment)
                        .commit();
            }

            return true;
        });
    }

    @Override
    public void onBackPressed()
    {
        logout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        KryoListener.currentActivity = this;
    }

    private void logout()
    {
        SystemMessagePacket packet = new SystemMessagePacket();
        packet.type = SystemMessagePacket.Type.LOGOUT;
        packet.message = String.format("User '%s' logged out.", user.username);
        new SendPacketThread(packet).start();
        finish();
    }
}
