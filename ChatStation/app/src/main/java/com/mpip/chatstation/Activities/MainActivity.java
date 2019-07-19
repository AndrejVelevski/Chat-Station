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
import com.mpip.chatstation.Packets.ConfirmUserPacket;
import com.mpip.chatstation.Packets.LoginUserPacket;
import com.mpip.chatstation.Packets.MessagePacket;
import com.mpip.chatstation.Networking.ConnectToServerThread;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Packets.ReceiveRandomChatPacket;
import com.mpip.chatstation.Packets.ReceiveUserPacket;
import com.mpip.chatstation.Packets.RegisterUserPacket;
import com.mpip.chatstation.Packets.RequestRandomChatPacket;
import com.mpip.chatstation.Packets.RequestUserPacket;
import com.mpip.chatstation.Packets.ResendCodePacket;
import com.mpip.chatstation.Packets.SystemMessagePacket;
import com.mpip.chatstation.R;

import static com.mpip.chatstation.Config.Constants.serverIP;
import static com.mpip.chatstation.Config.Constants.serverPort;

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
            kryo.register(SystemMessagePacket.Type.class);
            kryo.register(SystemMessagePacket.class);
            kryo.register(RegisterUserPacket.class);
            kryo.register(LoginUserPacket.class);
            kryo.register(ConfirmUserPacket.class);
            kryo.register(ResendCodePacket.class);
            kryo.register(RequestUserPacket.class);
            kryo.register(ReceiveUserPacket.class);
            kryo.register(RequestRandomChatPacket.class);
            kryo.register(ReceiveRandomChatPacket.class);
            kryo.register(MessagePacket.Type.class);
            kryo.register(MessagePacket.class);
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
//        Intent intent = new Intent(this, RegisterActivity.class);
//        startActivity(intent);

        Intent i = new Intent(this, LoginRegisterActivity.class);
        i.putExtra("REGISTERorLOGIN","Register");
        startActivity(i);
    }

    public void login(View view)
    {
        Intent intent = new Intent(this, LoginRegisterActivity.class);
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
            ConnectToServerThread thread = new ConnectToServerThread(client, serverIP, serverPort, 1000);

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
}
