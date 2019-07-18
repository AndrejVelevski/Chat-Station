package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.mpip.chatstation.Fragments.FriendsFragment;
import com.mpip.chatstation.Fragments.HomeFragment;
import com.mpip.chatstation.Fragments.MessagesFragment;
import com.mpip.chatstation.R;

public class NavUiMainActivity extends AppCompatActivity {

    AHBottomNavigation bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_ui_main);

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_nav);

        //Create Item
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Home", R.drawable.ic_home_black_24dp, android.R.color.white);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Messages", R.drawable.ic_message_black_24dp, android.R.color.white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Friends", R.drawable.ic_people_black_24dp, android.R.color.white);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        // Set background color
        //bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#009688"));

        // Use colored navigation with circle reveal effect
        //bottomNavigation.setColored(true);



        //poceten fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment(bottomNavigation)).commit();

        bottomNavigation.setOnTabSelectedListener((pos,isSelected)->{
            Toast.makeText(NavUiMainActivity.this, "Start activity: " + pos, Toast.LENGTH_SHORT).show();

            Fragment selectedFragment = null;

            switch (pos){
                case 0:
                    selectedFragment = new HomeFragment(bottomNavigation);
                    break;
                case 1:
                    selectedFragment = new MessagesFragment();
                    break;
                case 2:
                    selectedFragment = new FriendsFragment();
                    break;
            }

            if(selectedFragment!=null){
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}