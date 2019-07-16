package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Packets.ConfirmUserPacket;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.ResendCodePacket;
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
        ConfirmUserPacket packet = new ConfirmUserPacket();
        packet.email = email;
        packet.confirm_code = etCode.getText().toString();

        new SendPacketThread(packet).start();
    }

    public void resend(View view)
    {
        ResendCodePacket packet = new ResendCodePacket();
        packet.email = email;
        new SendPacketThread(packet).start();
    }
}
