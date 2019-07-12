package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Config.MessageType;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.LoginPacket;
import com.mpip.chatstation.Packets.ReceiveUserPacket;
import com.mpip.chatstation.Packets.RequestUserPacket;
import com.mpip.chatstation.Packets.SystemMessagePacket;
import com.mpip.chatstation.R;

import org.mindrot.jbcrypt.BCrypt;

public class HomeActivity extends AppCompatActivity
{
    Listener listener;

    TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvHomeWelcome);

        RequestUserPacket requestUser = new RequestUserPacket();
        requestUser.usernameEmail = getIntent().getStringExtra("usernameEmail");
        new SendPacketThread(requestUser).start();

        listener = new Listener()
        {
            public void received(Connection connection, Object object)
            {
                if (object instanceof ReceiveUserPacket)
                {
                    ReceiveUserPacket user = (ReceiveUserPacket)object;0

                    String txt = String.format(
                                    "Welcome %s.\n" +
                                    "Id: %d\n" +
                                    "Email: %s\n" +
                                    "First name: %s\n" +
                                    "Last name: %s\n" +
                                    "Age: %d",
                            user.username, user.id, user. email, user.firstName, user.lastName, user.age);
                    tvWelcome.setText(txt);
                }
            }
        };

        MainActivity.client.addListener(listener);
    }
}
