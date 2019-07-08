package com.mpip.chatstation.Networking;

import com.mpip.chatstation.Activities.MainActivity;

public class SendPacketThread extends Thread
{
    Object packet;

    public SendPacketThread(Object packet)
    {
        this.packet = packet;
    }

    @Override
    public void run()
    {
        MainActivity.client.sendTCP(packet);
    }
}