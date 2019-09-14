package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Models.User;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.R;

public class UserDetailsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        KryoListener.currentActivity = this;

        User user = (User) getIntent().getSerializableExtra(Constants.USER);

        System.out.println(user.email);
    }
}
