package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.SystemMessagePacket;
import com.mpip.chatstation.Packets.RegisterPacket;
import com.mpip.chatstation.R;

import org.mindrot.jbcrypt.BCrypt;

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

    Intent goToHomeActivity;

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

        tvErrorMessage = findViewById(R.id.tvRegisterErrorMessage);

        goToHomeActivity = new Intent(this, HomeActivity.class);

        listener = new Listener()
        {
            public void received(Connection connection, Object object)
            {
                if (object instanceof SystemMessagePacket)
                {
                    SystemMessagePacket systemMessage = (SystemMessagePacket)object;

                    switch (systemMessage.type)
                    {
                        case REGISTER_SUCCESS:
                            MainActivity.client.removeListener(listener);

                            goToHomeActivity.putExtra("usernameEmail", etUsername.getText().toString());
                            startActivity(goToHomeActivity);

                            break;

                        case REGISTER_FAILED:
                            tvErrorMessage.setText(systemMessage.message);
                            break;
                    }
                }
            }
        };

        MainActivity.client.addListener(listener);
    }

    public void register(View view)
    {
        RegisterPacket user = new RegisterPacket();
        user.email = etEmail.getText().toString();
        user.username = etUsername.getText().toString();
        user.password = BCrypt.hashpw(etPassword.getText().toString(), Constants.SALT);
        user.firstName = etFirstName.getText().toString();
        user.lastName = etLastName.getText().toString();
        if (etAge.getText().toString().length() > 0)
            user.age = Integer.valueOf(etAge.getText().toString());

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
