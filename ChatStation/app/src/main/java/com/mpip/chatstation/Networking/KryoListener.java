package com.mpip.chatstation.Networking;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mpip.chatstation.Activities.ChatRoomActivity;
import com.mpip.chatstation.Activities.MainActivity;
import com.mpip.chatstation.Activities.NavUiMainActivity;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Config.UserLoginDetails;
import com.mpip.chatstation.Fragments.ConfirmFragment;
import com.mpip.chatstation.Fragments.FriendRequestsFragment;
import com.mpip.chatstation.Fragments.FriendsListFragment;
import com.mpip.chatstation.Fragments.HomeFragment;
import com.mpip.chatstation.Fragments.LoginFragment;
import com.mpip.chatstation.Fragments.SignUpFragment;
import com.mpip.chatstation.Models.ChatMessage;
import com.mpip.chatstation.Models.User;
import com.mpip.chatstation.Packets.MessagePacket;
import com.mpip.chatstation.Packets.ReceiveFriendRequestsPacket;
import com.mpip.chatstation.Packets.ReceiveFriendsPacket;
import com.mpip.chatstation.Packets.ReceiveRandomChatPacket;
import com.mpip.chatstation.Packets.ReceiveUserPacket;
import com.mpip.chatstation.Packets.SystemMessagePacket;
import com.mpip.chatstation.R;

import org.mindrot.jbcrypt.BCrypt;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KryoListener
{
    public static Listener listener;
    public static AppCompatActivity currentActivity;
    private static Intent goToMainIntent;
    private static Intent goToChatRoomIntent;
    private static Intent goToNAVuiIntent;

    public static void createListener()
    {
        goToMainIntent = new Intent(currentActivity, MainActivity.class);
        goToChatRoomIntent = new Intent(currentActivity, ChatRoomActivity.class);
        goToNAVuiIntent = new Intent(currentActivity, NavUiMainActivity.class);

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
                            MainActivity.replaceConfirmFragment(SignUpFragment.emailuserEmail);
                            break;
                        }
                        case REGISTER_FAILED:
                        {
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    SignUpFragment.showError(systemMessage.message);
                                }
                            });
                            break;
                        }
                        case LOGIN_SUCCESS:
                        {
                            if (LoginFragment.cbRememberMe.isChecked() && MainActivity.uld == null)
                            {
                                UserLoginDetails uld = new UserLoginDetails();
                                uld.username_email = LoginFragment.emailid.getText().toString();
                                uld.password = LoginFragment.password.getText().toString();
                                MainActivity.uld = uld;

                                FileOutputStream outputStream;
                                ObjectOutputStream objectOutputStream;
                                try
                                {
                                    outputStream = currentActivity.openFileOutput("loginDetails.ld", Context.MODE_PRIVATE);
                                    objectOutputStream = new ObjectOutputStream(outputStream);
                                    objectOutputStream.writeObject(uld);
                                    outputStream.close();
                                    objectOutputStream.close();
                                }
                                catch (Exception e) {}
                            }
                            goToNAVuiIntent.putExtra(Constants.USERNAMEEMAIL, LoginFragment.emailid.getText().toString());
                            currentActivity.startActivity(goToNAVuiIntent);
                            break;
                        }
                        case LOGIN_FAILED:
                        {
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    LoginFragment.showError(systemMessage.message);
                                }
                            });
                            break;
                        }
                        case ACCOUNT_NOT_CONFIRMED:
                        {
                            MainActivity.replaceConfirmFragment(LoginFragment.emailid.getText().toString());
                            break;
                        }
                        case CONFIRMATION_CODE_SUCCESS:
                        {
                            MainActivity.replaceLoginFragment();
                            break;
                        }
                        case CONFIRMATION_CODE_FAILED:
                        {
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    ConfirmFragment.showError(systemMessage.message);
                                }
                            });
                            break;
                        }
                        case RESEND_CONFIRMATION_CODE:
                        {
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    ConfirmFragment.showError(systemMessage.message);
                                }
                            });
                            break;
                        }
                        case FRIEND_REQUEST_SUCCESS:
                        {
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    HomeFragment.showSuccess(systemMessage.message);
                                }
                            });
                            break;
                        }
                        case FRIEND_REQUEST_FAILED:
                        {
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    HomeFragment.showError(systemMessage.message);
                                }
                            });
                            break;
                        }
                        case FRIEND_REQUEST:
                        {
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    HomeFragment.showSuccess(systemMessage.message);
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

                    if (packet.toSelf)
                    {
                        NavUiMainActivity.user = new User();
                        NavUiMainActivity.user.email = packet.email;
                        NavUiMainActivity.user.username = packet.username;
                        NavUiMainActivity.user.first_name = packet.first_name;
                        NavUiMainActivity.user.last_name = packet.last_name;
                        NavUiMainActivity.user.age = packet.age;
                        NavUiMainActivity.user.registered_on = packet.registered_on;
                        NavUiMainActivity.user.last_login = packet.last_login;
                    }
                    else
                    {
                        //ako gledas profil na nekoj drug
                    }
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
                            boolean belongsToMe = packet.username.equals(NavUiMainActivity.user.username);
                            ChatRoomActivity.messageAdapter.add(new ChatMessage(packet.message,packet.username,belongsToMe, packet.date, packet.type));
                            ChatRoomActivity.lvMessageBox.setSelection(ChatRoomActivity.lvMessageBox.getCount() - 1);
                        }
                    });
                }
                else if (object instanceof ReceiveFriendRequestsPacket)
                {
                    ReceiveFriendRequestsPacket packet = (ReceiveFriendRequestsPacket)object;

                    currentActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            FriendRequestsFragment.friendRequestsAdapter.updateData(Arrays.asList(packet.usernames));
                        }
                    });
                }
                else if (object instanceof ReceiveFriendsPacket)
                {
                    ReceiveFriendsPacket packet = (ReceiveFriendsPacket)object;
                    List<User> users = new ArrayList<User>();
                    for (ReceiveUserPacket user : packet.users)
                    {
                        User u = new User();
                        u.email = user.email;
                        u.username = user.username;
                        u.first_name = user.first_name;
                        u.last_name = user.last_name;
                        u.age = user.age;
                        u.registered_on = user.registered_on;
                        u.last_login = user.last_login;
                        users.add(u);
                    }

                    currentActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            FriendsListFragment.friendListAdapter.updateData(users);
                        }
                    });
                }
            }
        };
    }
}
