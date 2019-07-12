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
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Networking.ConnectToServerThread;
import com.mpip.chatstation.Config.MessageType;
import com.mpip.chatstation.Packets.SystemMessage;
import com.mpip.chatstation.Packets.User;
import com.mpip.chatstation.R;

public class MainActivity extends AppCompatActivity
{
    Button btnRegister;
    Button btnLogin;
    Button btnReconnect;

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

        client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(MessageType.class);
        kryo.register(SystemMessage.class);
        kryo.register(User.class);
        client.start();

        connectToServer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        switch (requestCode)
        {
            case Constants.REGISTER:
                if (resultCode == Activity.RESULT_OK)
                {
                    Toast.makeText(this,intent.getStringExtra("message"),Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void registerMain(View view)
    {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, Constants.REGISTER);
    }

    public void loginMain(View view)
    {

    }

    public void reconnect(View view)
    {
        connectToServer();
    }

    public void connectToServer()
    {
        try
        {
            ConnectToServerThread thread = new ConnectToServerThread(client, "78.157.30.115", 54555, 1000);
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
}
