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
import com.mpip.chatstation.Packets.LoginPacket;
import com.mpip.chatstation.R;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity
{
    EditText etUsernameEmail;
    EditText etPassword;
    TextView tvErrorMessage;

    Listener listener;

    Intent goToHomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsernameEmail = findViewById(R.id.etLoginUsernameEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        tvErrorMessage = findViewById(R.id.tvLoginErrorMessage);

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
                        case LOGIN_SUCCESS:
                            MainActivity.client.removeListener(listener);

                            goToHomeActivity.putExtra("usernameEmail", etUsernameEmail.getText().toString());
                            startActivity(goToHomeActivity);

                            break;

                        case LOGIN_FAILED:
                            tvErrorMessage.setText(systemMessage.message);
                            break;
                    }
                }
            }
        };

        MainActivity.client.addListener(listener);
    }

    public void login(View view)
    {
        LoginPacket user = new LoginPacket();

        user.usernameEmail = etUsernameEmail.getText().toString();
        user.password = BCrypt.hashpw(etPassword.getText().toString(), Constants.SALT);

        new SendPacketThread(user).start();
    }
}
