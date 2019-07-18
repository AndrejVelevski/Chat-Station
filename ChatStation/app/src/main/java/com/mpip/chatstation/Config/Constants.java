package com.mpip.chatstation.Config;

public class Constants
{
    public static final String SALT = "$2a$10$UOgN5bHS3OvVSeexnyz3hu";

    public static final String EMAIL = "email";
    public static final String MESSAGE = "message";
    public static final String ROOM_TAGS = "room_tags";
    public static final String MATCHING_TAGS = "matching_tags";

    public static double map(double fromX, double toX, double fromY, double toY, double value)
    {
        return fromY + ((toY - fromY) / (toX - fromX)) * (value - fromX);
    }
}
