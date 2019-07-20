package Packets;

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
        FRIEND_REQUEST_SUCCESS,
        FRIEND_REQUEST_FAILED,
        SERVER_CLOSED,

        //Client
        LOGOUT
    }

    public Type type;
    public String message;
}