package com.mpip.chatstation.Packets;

import com.mpip.chatstation.Config.UserPacketType;

public class UserPacket
{
    public UserPacketType type;
    public Integer id;
    public String email;
    public String username;
    public String password;
    public String first_name;
    public String last_name;
    public Integer age;
    public Boolean confirmed;
    public String confirm_code;
    public String registered_on;
    public String last_login;
}
