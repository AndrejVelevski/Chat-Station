package com.mpip.chatstation.Packets;

public class MessagePacket
{
    public enum Type
    {
        JOIN,
        LEAVE,
        MESSAGE
    }

    public Type type;
    public String username;
    public String message;
    public String date;
}
