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
        RESEND_CONFIRMATION_CODE,
        FRIEND_REQUEST_SUCCESS,
        FRIEND_REQUEST_FAILED,
        FRIEND_REQUEST,
        MESSAGE,
        SERVER_CLOSED,

        //Client
        LOGOUT
    }

    public Type type;
    public String message;
}