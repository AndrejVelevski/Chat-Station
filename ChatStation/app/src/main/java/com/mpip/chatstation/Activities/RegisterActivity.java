package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.SystemMessage;
import com.mpip.chatstation.Packets.User;
import com.mpip.chatstation.R;

public class RegisterActivity extends AppCompatActivity
{
    EditText etEmail;
    EditText etUsername;
    EditText etPassword;
    EditText etConfirmPassword;
    EditText etFirstName;
    EditText etLastName;
    EditText etAge;

    TextView tvErrorMessage;

    Listener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etRegisterEmail);
        etUsername = findViewById(R.id.etRegisterUsername);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);
        etFirstName = findViewById(R.id.etRegisterFirstName);
        etLastName = findViewById(R.id.etRegisterLastName);
        etAge = findViewById(R.id.etRegisterAge);

        tvErrorMessage = findViewById(R.id.tvErrorMessage);

        listener = new Listener()
        {
            public void received(Connection connection, Object object)
            {
                if (object instanceof SystemMessage)
                {
                    SystemMessage systemMessage = (SystemMessage)object;

                    switch (systemMessage.type)
                    {
                        case REGISTER_SUCCESS:
                            MainActivity.client.removeListener(listener);

                            Intent intent = new Intent();
                            intent.putExtra("message", systemMessage.message);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                            break;

                        case ERROR:
                            tvErrorMessage.setText(systemMessage.message);
                            break;
                    }
                }
            }
        };

        MainActivity.client.addListener(listener);
    }

    @Override
    public void onBackPressed()
    {
        MainActivity.client.removeListener(listener);
    }

    public void register(View view)
    {
        User user = new User();
        user.id = -1;
        user.email = etEmail.getText().toString();
        user.username = etUsername.getText().toString();
        user.password = etPassword.getText().toString();
        user.fullname = String.format("%s %s", etFirstName.getText().toString(), etLastName.getText().toString());
        if (etAge.getText().toString().length() > 0)
            user.age = Integer.valueOf(etAge.getText().toString());
        user.location = null;

        boolean error = false;
        if (user.email.trim().length() == 0)
        {
            error = true;
            tvErrorMessage.setText("Email field can't be empty.");
        }
        else if (!user.email.contains("@"))
        {
            error = true;
            tvErrorMessage.setText("Email must contain '@'.");
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
