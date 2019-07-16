package com.mpip.chatstation.Networking;

import android.os.AsyncTask;

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
        try
        {
            MainActivity.client.sendTCP(packet);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void test(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try
                {
                    MainActivity.client.sendTCP(packet);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}