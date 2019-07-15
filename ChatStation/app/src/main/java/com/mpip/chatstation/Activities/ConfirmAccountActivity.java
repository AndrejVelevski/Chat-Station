package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Config.UserPacketType;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.UserPacket;
import com.mpip.chatstation.R;

public class ConfirmAccountActivity extends AppCompatActivity
{
    public static String email;
    private static EditText etCode;
    public static TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_account);
        KryoListener.currentActivity = this;

        etCode = findViewById(R.id.etConfirmAccountCode);
        tvErrorMessage = findViewById(R.id.tvConfirmAccountErrorMessage);

        email = getIntent().getStringExtra(Constants.EMAIL);
    }

    public void confirm(View view)
    {
        UserPacket user = new UserPacket();
        user.type = UserPacketType.CONFIRM_CODE;
        user.email = email;
        user.confirm_code = etCode.getText().toString();

        new SendPacketThread(user).start();
    }

    public void resend(View view)
    {
        UserPacket user = new UserPacket();
        user.type = UserPacketType.RESEND_CODE;
        user.email = email;

        new SendPacketThread(user).start();
    }
}
