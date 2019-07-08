package com.mpip.chatstation.Networking;

import android.content.Context;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Client;
import com.mpip.chatstation.Activities.MainActivity;

import java.io.IOException;

public class ConnectToServerThread extends Thread
{
    Client client;
    String ipAddress;
    int port;
    int waitForResponse;

    public boolean connectionSuccessful;

    public ConnectToServerThread(Client client, String ipAddress, int port, int waitForResponse)
    {
        this.client = client;
        this.ipAddress = ipAddress;
        this.port = port;
        this.waitForResponse = waitForResponse;
    }

    @Override
    public void run()
    {
        try
        {
            client.connect(waitForResponse, ipAddress, port);
            connectionSuccessful = true;
        }
        catch (IOException e)
        {
            connectionSuccessful = false;
        }
    }
}
