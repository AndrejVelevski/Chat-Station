package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Models.User;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.R;

public class UserDetailsActivity extends AppCompatActivity
{
    private TextView tvUsername;
    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvAge;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        KryoListener.currentActivity = this;

        User user = (User) getIntent().getSerializableExtra(Constants.USER);

        System.out.println(user.email);

        initUI();

        tvUsername.setText(user.username);
        tvFirstName.setText(user.first_name);
        tvLastName.setText(user.last_name);
        tvAge.setText(user.age);
    }

    private void initUI(){
        tvUsername = findViewById(R.id.userInfoUsername);
        tvFirstName = findViewById(R.id.userInfoFirstName);
        tvLastName = findViewById(R.id.userInfoLastName);
        tvAge = findViewById(R.id.userInfoAge);
    }
}
