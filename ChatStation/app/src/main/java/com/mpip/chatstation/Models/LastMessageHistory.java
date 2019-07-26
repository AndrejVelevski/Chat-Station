package com.mpip.chatstation.Models;

public class LastMessageHistory {
    private String username;
    private String firstName;
    private String lastName;
    private String message;
    private String dateAt;

    public LastMessageHistory(String username, String firstName, String lastName, String message, String dateAt) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateAt = dateAt;

        if(message.length()>45){
            this.message = message.substring(0,42) + "...";
        }else{
            this.message = message;
        }

    }

    public String getFullName(){
        return firstName + " " + lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateAt() {
        return dateAt;
    }

    public void setDateAt(String dateAt) {
        this.dateAt = dateAt;
    }
}
