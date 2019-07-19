package com.mpip.chatstation.Models;

import com.mpip.chatstation.Packets.MessagePacket;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class ChatMessage {

    private String text; // message body
    private String userData; // username (data of the user)
    private String sentAt; // Date & Time
    private boolean belongsToCurrentUser;
    private MessagePacket.Type type;

    public ChatMessage(String text, String userData, boolean belongsToCurrentUser, String sentAt, MessagePacket.Type type) {
        this.text = text;
        this.userData = userData;
        this.belongsToCurrentUser = belongsToCurrentUser;
        this.sentAt = sentAt;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public String getUserData() {
        return userData;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

    public String getSentAt() {
        return sentAt;
    }

    public MessagePacket.Type getType() {
        return type;
    }
}

