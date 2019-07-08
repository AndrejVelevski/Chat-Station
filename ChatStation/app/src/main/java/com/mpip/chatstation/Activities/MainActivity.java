package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.mpip.chatstation.Networking.ConnectToServerThread;
import com.mpip.chatstation.Packets.MessagePacket;
import com.mpip.chatstation.R;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity
{
    EditText etUsername;
    public static Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);

        client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(MessagePacket.class);
        client.start();
    }

    public void logIn(View view)
    {
        String username = etUsername.getText().toString();

        if (username.trim().length() == 0)
        {
            Toast.makeText(this,"Please enter a username",Toast.LENGTH_LONG).show();
        }
        else
        {
            ConnectToServerThread thread = new ConnectToServerThread(client, "78.157.30.94", 54555, 1000);
            thread.start();
            try
            {
                thread.join();
            }
            catch (InterruptedException e) {}

            if (thread.connectionSuccessful)
            {
                Intent intent = new Intent(this, ChatRoomActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this,"Failed to connect to server",Toast.LENGTH_LONG).show();
            }
        }
    }
}
