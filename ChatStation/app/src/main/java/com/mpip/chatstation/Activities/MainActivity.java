package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Config.SystemMessagePacketType;
import com.mpip.chatstation.Config.UserPacketType;
import com.mpip.chatstation.Networking.ConnectToServerThread;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Packets.SystemMessagePacket;
import com.mpip.chatstation.Packets.UserPacket;
import com.mpip.chatstation.R;

public class MainActivity extends AppCompatActivity
{
    private static Button btnRegister;
    private static Button btnLogin;
    private static Button btnReconnect;

    public static Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KryoListener.currentActivity = this;

        btnRegister = findViewById(R.id.btnMainRegister);
        btnLogin = findViewById(R.id.btnMainLogin);
        btnRegister.setEnabled(false);
        btnLogin.setEnabled(false);
        btnReconnect = findViewById(R.id.btnMainReconnect);
        btnReconnect.setVisibility(View.GONE);

        if (client == null)
        {
            client = new Client();
            Kryo kryo = client.getKryo();
            kryo.register(SystemMessagePacketType.class);
            kryo.register(SystemMessagePacket.class);
            kryo.register(UserPacketType.class);
            kryo.register(UserPacket.class);
            client.start();
        }

        if (KryoListener.listener == null)
        {
            KryoListener.createListener();
            client.addListener(KryoListener.listener);
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            Toast.makeText(this,extras.getString(Constants.MESSAGE),Toast.LENGTH_LONG).show();
            btnRegister.setEnabled(false);
            btnLogin.setEnabled(false);
            btnReconnect.setVisibility(View.VISIBLE);
        }
        else
        {
            connectToServer();
        }
    }

    public void register(View view)
    {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void reconnect(View view)
    {
        connectToServer();
    }

    private void connectToServer()
    {
        try
        {
            ConnectToServerThread thread = new ConnectToServerThread(client, "78.157.30.102", 54555, 1000);
            thread.start();
            try
            {
                thread.join();
            }
            catch (InterruptedException e) {e.printStackTrace();}
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
        catch(Exception e) { e.printStackTrace();}
    }

    public void testButtonClick(View view){
        Intent intent = new Intent(this, NavUiMainActivity.class);
        startActivityForResult(intent, 0);
    }

    public void testNewLoginButtonClick(View view){
        Intent intent = new Intent(this, TestLoginActivity.class);
        startActivityForResult(intent, 0);
    }
}
