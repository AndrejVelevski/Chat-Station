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

public class RegisterActivity extends AppCompatActivity
{
    public static EditText etEmail;
    private static EditText etUsername;
    private static EditText etPassword;
    private static EditText etConfirmPassword;
    private static EditText etFirstName;
    private static EditText etLastName;
    private static EditText etAge;

    public static TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        KryoListener.currentActivity = this;

        etEmail = findViewById(R.id.etRegisterEmail);
        etUsername = findViewById(R.id.etRegisterUsername);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);
        etFirstName = findViewById(R.id.etRegisterFirstName);
        etLastName = findViewById(R.id.etRegisterLastName);
        etAge = findViewById(R.id.etRegisterAge);
        tvErrorMessage = findViewById(R.id.tvRegisterErrorMessage);
    }

    public void register(View view)
    {
        UserPacket user = new UserPacket();
        user.type = UserPacketType.REGISTER_USER;
        user.id = -1;
        user.email = etEmail.getText().toString();
        user.username = etUsername.getText().toString();
        user.password = BCrypt.hashpw(etPassword.getText().toString(), Constants.SALT);
        user.first_name = etFirstName.getText().toString();
        user.last_name = etLastName.getText().toString();

        if (etAge.getText().toString().length() > 0)
            user.age = Integer.valueOf(etAge.getText().toString());

        boolean error = false;
        if (!Pattern.compile(String.valueOf(Patterns.EMAIL_ADDRESS)).matcher(user.email).matches())
        {
            error = true;
            tvErrorMessage.setText("Not a valid email address.");
        }
        else if (user.username.trim().length() == 0)
        {
            error = true;
            tvErrorMessage.setText("Username field can't be empty.");
        }
        else if (etPassword.getText().toString().length() == 0)
        {
            error = true;
            tvErrorMessage.setText("Password field can't be empty.");
        }
        else if (!(etPassword.getText().toString().equals(etConfirmPassword.getText().toString())))
        {
            error = true;
            tvErrorMessage.setText("Passwords do not match.");
        }

        if (!error)
        {
            new SendPacketThread(user).start();
        }
    }

}
