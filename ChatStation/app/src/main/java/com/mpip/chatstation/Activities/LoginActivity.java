package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Config.UserPacketType;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.UserPacket;
import com.mpip.chatstation.R;

import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity
{
    public static EditText etEmail;
    private static EditText etPassword;
    public static TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        KryoListener.currentActivity = this;

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        tvErrorMessage = findViewById(R.id.tvLoginErrorMessage);
    }

    public void login(View view)
    {
        UserPacket user = new UserPacket();
        user.type = UserPacketType.LOGIN_USER;
        user.email = etEmail.getText().toString();
        user.password = BCrypt.hashpw(etPassword.getText().toString(), Constants.SALT);

        if (!Pattern.compile(String.valueOf(Patterns.EMAIL_ADDRESS)).matcher(user.email).matches())
        {
            tvErrorMessage.setText("Not a valid email address.");
        }
        else
        {
            new SendPacketThread(user).start();
        }

    }
}
