package com.mpip.chatstation.Networking;

import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

public class ConnectToServerThread extends Thread
{
    private Client client;
    private String ipAddress;
    private int port;
    private int timeout;

    public boolean connectionSuccessful;

    public ConnectToServerThread(Client client, String ipAddress, int port, int timeout)
    {
        this.client = client;
        this.ipAddress = ipAddress;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public void run()
    {
        try
        {
            client.connect(timeout, ipAddress, port);
            connectionSuccessful = true;
        }
        catch (IOException e)
        {
            connectionSuccessful = false;
            e.printStackTrace();
        }
    }
}
