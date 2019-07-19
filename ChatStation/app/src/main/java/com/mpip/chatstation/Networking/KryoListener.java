package com.mpip.chatstation.Networking;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mpip.chatstation.Activities.ChatRoomActivity;
import com.mpip.chatstation.Activities.ConfirmAccountActivity;
import com.mpip.chatstation.Activities.HomeActivity;
import com.mpip.chatstation.Activities.MainActivity;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Fragments.CustomToast;
import com.mpip.chatstation.Fragments.LoginFragment;
import com.mpip.chatstation.Fragments.SignUpFragment;
import com.mpip.chatstation.Models.ChatMessage;
import com.mpip.chatstation.Packets.MessagePacket;
import com.mpip.chatstation.Packets.ReceiveRandomChatPacket;
import com.mpip.chatstation.Packets.ReceiveUserPacket;
import com.mpip.chatstation.Packets.SystemMessagePacket;

public class KryoListener
{
    public static Listener listener;
    public static AppCompatActivity currentActivity;
    private static Intent goToMainIntent;
    private static Intent goToHomeIntent;
    private static Intent goToLoginIntent;
    private static Intent goToChatRoomIntent;
    private static Intent goToConfirmAccountIntent;

    public static void createListener()
    {
        goToMainIntent = new Intent(currentActivity, MainActivity.class);
        goToHomeIntent = new Intent(currentActivity, HomeActivity.class);
        goToChatRoomIntent = new Intent(currentActivity, ChatRoomActivity.class);
        goToConfirmAccountIntent = new Intent(currentActivity, ConfirmAccountActivity.class);

        listener = new Listener()
        {
            public void received(Connection connection, Object object)
            {
                if (object instanceof SystemMessagePacket)
                {
                    SystemMessagePacket systemMessage = (SystemMessagePacket)object;

                    switch (systemMessage.type)
                    {
                        case REGISTER_SUCCESS:
                        {
                            goToConfirmAccountIntent.putExtra(Constants.EMAIL, SignUpFragment.emailuserEmail);
                            currentActivity.startActivity(goToConfirmAccountIntent);
                            break;
                        }
                        case REGISTER_FAILED:
                        {
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                   // SignUpFragment.signUpTitle.setText(systemMessage.message);
                                    SignUpFragment.showError(systemMessage.message);
                                }
                            });
                            break;
                        }
                        case LOGIN_SUCCESS:
                        {
                            goToHomeIntent.putExtra(Constants.EMAIL, LoginFragment.userEmail);
                            currentActivity.startActivity(goToHomeIntent);
                            break;
                        }
                        case LOGIN_FAILED:
                        {
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    //LoginFragment.loginTV.setText(systemMessage.message);
                                    LoginFragment.showError(systemMessage.message);
                                }
                            });
                            break;
                        }
                        case ACCOUNT_NOT_CONFIRMED:
                        {
                            goToConfirmAccountIntent.putExtra(Constants.EMAIL, LoginFragment.userEmail);
                            currentActivity.startActivity(goToConfirmAccountIntent);
                            break;
                        }
                        case CONFIRMATION_CODE_SUCCESS:
                        {
                            currentActivity.startActivity(goToLoginIntent);
                            break;
                        }
                        case CONFIRMATION_CODE_FAILED:
                        {
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    ConfirmAccountActivity.tvErrorMessage.setText(systemMessage.message);
                                }
                            });
                            break;
                        }
                        case SERVER_CLOSED:
                        {
                            goToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            currentActivity.startActivity(goToMainIntent);
                            break;
                        }
                    }
                }
                else if (object instanceof ReceiveUserPacket)
                {
                    ReceiveUserPacket packet = (ReceiveUserPacket)object;

                    HomeActivity.user.email = packet.email;
                    HomeActivity.user.username = packet.username;
                    HomeActivity.user.first_name = packet.first_name;
                    HomeActivity.user.last_name = packet.last_name;
                    HomeActivity.user.age = packet.age;
                    HomeActivity.user.registered_on = packet.registered_on;
                    HomeActivity.user.last_login = packet.last_login;

                    String txt = String.format(
                                    "Welcome %s.\n" +
                                    "Email: %s\n" +
                                    "First name: %s\n" +
                                    "Last name: %s\n" +
                                    "Age: %d\n" +
                                    "Registered on: %s\n" +
                                    "Last login: %s",
                            packet.username, packet. email, packet.first_name, packet.last_name,
                            packet.age, packet.registered_on, packet.last_login);
                    currentActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            HomeActivity.tvWelcome.setText(txt);
                        }
                    });
                }
                else if (object instanceof ReceiveRandomChatPacket)
                {
                    ReceiveRandomChatPacket packet = (ReceiveRandomChatPacket)object;

                    String msg;
                    if (packet.found)
                    {
                        msg = String.format("Found a chat room.\nRoom max users: %d", packet.maxUsers);
                    }
                    else
                    {
                        msg = String.format("Could not find a chat room.\nCreated empty chat room.\nRoom max users: %d", packet.maxUsers);
                    }
                    if (packet.roomTags.length() > 0)
                    {
                        msg += String.format("\nRoom tags: %s", packet.roomTags);
                    }

                    goToChatRoomIntent.putExtra(Constants.ROOM_TAGS, packet.roomTags);
                    goToChatRoomIntent.putExtra(Constants.MATCHING_TAGS, packet.matchingTags);
                    goToChatRoomIntent.putExtra(Constants.MESSAGE, msg);

                    currentActivity.startActivity(goToChatRoomIntent);
                }
                else if (object instanceof MessagePacket)
                {
                    MessagePacket packet = (MessagePacket)object;

                    currentActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            boolean belongsToMe = false;
                            if(packet.username.equals(HomeActivity.user.username)) belongsToMe = true;
                            ChatRoomActivity.messageAdapter.add(new ChatMessage(packet.message,packet.username,belongsToMe, packet.date, packet.type));
                            //notifyDataSetChanged();
                            ChatRoomActivity.lvMessageBox.setSelection(ChatRoomActivity.lvMessageBox.getCount() - 1);
                        }
                    });
                }
            }
        };
    }
}
