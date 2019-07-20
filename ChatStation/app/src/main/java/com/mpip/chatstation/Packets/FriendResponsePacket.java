package com.mpip.chatstation.Packets;

public class FriendResponsePacket
{
    public enum Type
    {
        ACCEPT,
        DECLINE
    }

    public Type type;
    public String user_from;
    public String user_to;
}
