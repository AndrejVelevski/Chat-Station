package com.mpip.chatstation.Packets;

public class SystemMessagePacket
{
    public enum Type
    {
        //Server
        REGISTER_SUCCESS,
        REGISTER_FAILED,
        LOGIN_SUCCESS,
        LOGIN_FAILED,
        ACCOUNT_NOT_CONFIRMED,
        CONFIRMATION_CODE_SUCCESS,
        CONFIRMATION_CODE_FAILED,
        FOUND_RANDOM_CHAT,
        SERVER_CLOSED,

        //Client
        LOGOUT,
        REQUEST_RANDOM_CHAT,
        STOP_RANDOM_CHAT
    }

    public Type type;
    public String message;
}