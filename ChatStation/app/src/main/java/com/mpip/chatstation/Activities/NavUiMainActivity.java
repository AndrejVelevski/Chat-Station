package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.mpip.chatstation.R;

public class NavUiMainActivity extends AppCompatActivity {

    AHBottomNavigation bottomNavigation;
    int count = 0;
    Button btn_count = findViewById(R.id.btn_count);

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
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#009688"));

        // Use colored navigation with circle reveal effect
        bottomNavigation.setColored(true);

        btn_count.setOnClickListener(v->{
            count++;
            bottomNavigation.setNotification(""+count,1);
        });

        bottomNavigation.setOnTabSelectedListener((pos,isSelected)->{
            Toast.makeText(NavUiMainActivity.this, "Start activity: " + pos, Toast.LENGTH_SHORT).show();
            return true;
        });
    }
}
