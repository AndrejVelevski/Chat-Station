package com.mpip.chatstation.Models;

public class User
{
    public String email;
    public String username;
    public String first_name;
    public String last_name;
    public Integer age;
    public String registered_on;
    public String last_login;

    public User() {};

    public User(String email, String username)
    {
        this.email = email;
        this.username = username;
    }

    public User(String email, String username, String first_name, String last_name, Integer age, String registered_on, String last_login)
    {
        this.email = email;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.age = age;
        this.registered_on = registered_on;
        this.last_login = last_login;
    }

    public String getFullName()
    {
        return  String.format("%s %s", first_name, last_name);
    }

    public String getCharsForPic()
    {
        String result = "";
        if(first_name.length()>0)
        {
            result += first_name.charAt(0);
        }
        if(last_name.length()>0){
            result += last_name.charAt(0);
        }
        if(result.trim().length()<1)
        {
            result += username.charAt(0);
        }
        return result;
    }

}
