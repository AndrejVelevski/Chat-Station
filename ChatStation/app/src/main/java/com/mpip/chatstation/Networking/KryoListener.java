package com.mpip.chatstation.Networking;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mpip.chatstation.Activities.ChatRoomActivity;
import com.mpip.chatstation.Activities.HomeActivity;
import com.mpip.chatstation.Activities.LoginRegisterActivity;
import com.mpip.chatstation.Activities.MainActivity;
import com.mpip.chatstation.Activities.NavUiMainActivity;
import com.mpip.chatstation.Config.Constants;
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

import java.util.Arrays;

public class KryoListener
{
    public static Listener listener;
    public static AppCompatActivity currentActivity;
    private static Intent goToMainIntent;
    private static Intent goToHomeIntent;
    private static Intent goToChatRoomIntent;
    private static Intent goToConfirmAccountIntent;
    private static Intent goToNAVuiIntent;

    public static void createListener()
    {
        goToMainIntent = new Intent(currentActivity, MainActivity.class);
        goToHomeIntent = new Intent(currentActivity, HomeActivity.class);
        goToChatRoomIntent = new Intent(currentActivity, ChatRoomActivity.class);
        goToConfirmAccountIntent = new Intent(currentActivity, LoginRegisterActivity.class);
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
                                    SignUpFragment.showError(systemMessage.message);
                                }
                            });
                            break;
                        }
                        case LOGIN_SUCCESS:
                        {
//                            goToHomeIntent.putExtra(Constants.EMAIL, LoginFragment.userEmail);
//                            currentActivity.startActivity(goToHomeIntent);
                            goToNAVuiIntent.putExtra(Constants.EMAIL, LoginFragment.userEmail);
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
                            goToConfirmAccountIntent.putExtra(Constants.EMAIL, LoginFragment.userEmail);
                            currentActivity.startActivity(goToConfirmAccountIntent);
                            break;
                        }
                        case CONFIRMATION_CODE_SUCCESS:
                        {
                            //currentActivity.startActivity(goToConfirmAccountIntent);
                            LoginRegisterActivity.fragmentManager
                                    .beginTransaction()
                                    .replace(R.id.frameContainer, new LoginFragment(),
                                            Constants.Login_Fragment).commit();
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
                        case FRIEND_REQUEST_SUCCESS:
                        {
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
//                                    HomeActivity.tvMessage.setTextColor(Color.rgb(0,255,0));
//                                    HomeActivity.tvMessage.setText(systemMessage.message);
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
//                                    HomeActivity.tvMessage.setTextColor(Color.rgb(255,0,0));
//                                    HomeActivity.tvMessage.setText(systemMessage.message);
                                    HomeFragment.showError(systemMessage.message);
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

                    User.email = packet.email;
                    User.username = packet.username;
                    User.first_name = packet.first_name;
                    User.last_name = packet.last_name;
                    User.age = packet.age;
                    User.registered_on = packet.registered_on;
                    User.last_login = packet.last_login;

                    //HomeActivity.updateInfo();
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
                            boolean belongsToMe = packet.username.equals(User.username);
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

                    StringBuilder sb = new StringBuilder();

                    for (String username : packet.usernames)
                    {
                        sb.append(String.format("%s\n", username));
                    }

                    currentActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //FriendsActivity.tvFriends.setText(sb.toString());
                            FriendsListFragment.friendListAdapter.updateData(Arrays.asList(packet.usernames));
                        }
                    });
                }
            }
        };
    }
}
