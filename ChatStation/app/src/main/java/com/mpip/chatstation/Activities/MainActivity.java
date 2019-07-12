package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mpip.chatstation.Networking.ConnectToServerThread;
import com.mpip.chatstation.Packets.MessageType;
import com.mpip.chatstation.Packets.SystemMessage;
import com.mpip.chatstation.Packets.User;
import com.mpip.chatstation.R;

public class MainActivity extends AppCompatActivity
{
    Button btnRegisterMain;
    Button btnLoginMain;
    Button btnReconnect;

    public static Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegisterMain = findViewById(R.id.btnRegisterMain);
        btnLoginMain = findViewById(R.id.btnLogInMain);
        btnRegisterMain.setEnabled(false);
        btnLoginMain.setEnabled(false);
        btnReconnect = findViewById(R.id.btnReconnect);
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
            case 0:
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
        startActivityForResult(intent, 0);
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
                btnRegisterMain.setEnabled(true);
                btnLoginMain.setEnabled(true);
                btnReconnect.setVisibility(View.GONE);
            }
            else
            {
                Toast.makeText(this,"Failed to connect to server",Toast.LENGTH_LONG).show();
                btnRegisterMain.setEnabled(false);
                btnLoginMain.setEnabled(false);
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
