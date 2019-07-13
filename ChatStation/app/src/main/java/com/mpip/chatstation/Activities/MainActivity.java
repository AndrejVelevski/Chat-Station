package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Networking.ConnectToServerThread;
import com.mpip.chatstation.Config.MessageType;
import com.mpip.chatstation.Packets.LoginPacket;
import com.mpip.chatstation.Packets.ReceiveUserPacket;
import com.mpip.chatstation.Packets.RequestUserPacket;
import com.mpip.chatstation.Packets.SystemMessagePacket;
import com.mpip.chatstation.Packets.RegisterPacket;
import com.mpip.chatstation.R;

public class MainActivity extends AppCompatActivity
{
    Button btnRegister;
    Button btnLogin;
    Button btnReconnect;

    Listener listener;

    Intent backToMainIntent;
    public static Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegister = findViewById(R.id.btnMainRegister);
        btnLogin = findViewById(R.id.btnMainLogin);
        btnRegister.setEnabled(false);
        btnLogin.setEnabled(false);
        btnReconnect = findViewById(R.id.btnMainReconnect);
        btnReconnect.setVisibility(View.GONE);

        backToMainIntent = new Intent(this, MainActivity.class);

        if (client == null)
        {
            client = new Client();
            Kryo kryo = client.getKryo();
            kryo.register(MessageType.class);
            kryo.register(SystemMessagePacket.class);
            kryo.register(RegisterPacket.class);
            kryo.register(LoginPacket.class);
            kryo.register(RequestUserPacket.class);
            kryo.register(ReceiveUserPacket.class);
            client.start();
        }

        listener = new Listener()
        {
            public void received(Connection connection, Object object)
            {
                if (object instanceof SystemMessagePacket)
                {
                    SystemMessagePacket systemMessage = (SystemMessagePacket)object;

                    switch (systemMessage.type)
                    {
                        case SERVER_CLOSED:
                            backToMainIntent.putExtra("message", systemMessage.message);
                            startActivity(backToMainIntent);
                            break;
                    }
                }
            }
        };

        client.addListener(listener);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            Toast.makeText(this,extras.getString("message"),Toast.LENGTH_LONG).show();
            btnRegister.setEnabled(false);
            btnLogin.setEnabled(false);
            btnReconnect.setVisibility(View.VISIBLE);
        }
        else
        {
            connectToServer();
        }
    }

    public void registerMain(View view)
    {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginMain(View view)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void reconnect(View view)
    {
        connectToServer();
    }

    public void connectToServer()
    {
        try
        {
            ConnectToServerThread thread = new ConnectToServerThread(client, "78.157.30.124", 54555, 1000);
            thread.start();
            try
            {
                thread.join();
            }
            catch (InterruptedException e) {}
            if (thread.connectionSuccessful)
            {
                Toast.makeText(this,"Connected to server",Toast.LENGTH_LONG).show();
                btnRegister.setEnabled(true);
                btnLogin.setEnabled(true);
                btnReconnect.setVisibility(View.GONE);
            }
            else
            {
                Toast.makeText(this,"Failed to connect to server",Toast.LENGTH_LONG).show();
                btnRegister.setEnabled(false);
                btnLogin.setEnabled(false);
                btnReconnect.setVisibility(View.VISIBLE);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void testButtonClick(View view){
        Intent intent = new Intent(this, NavUiMainActivity.class);
        startActivityForResult(intent, 0);
    }
}
