package com.mpip.chatstation.Networking;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mpip.chatstation.Activities.ConfirmAccountActivity;
import com.mpip.chatstation.Activities.HomeActivity;
import com.mpip.chatstation.Activities.LoginActivity;
import com.mpip.chatstation.Activities.MainActivity;
import com.mpip.chatstation.Activities.RegisterActivity;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Packets.SystemMessagePacket;
import com.mpip.chatstation.Packets.UserPacket;

public class KryoListener
{
    public static Listener listener;
    public static AppCompatActivity currentActivity;
    private static Intent goToMainIntent;
    private static Intent goToHomeIntent;
    private static Intent goToLoginIntent;
    private static Intent goToConfirmAccountIntent;

    public static void createListener()
    {
        goToMainIntent = new Intent(currentActivity, MainActivity.class);
        goToHomeIntent = new Intent(currentActivity, HomeActivity.class);
        goToLoginIntent = new Intent(currentActivity, LoginActivity.class);
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
                            goToConfirmAccountIntent.putExtra(Constants.EMAIL, RegisterActivity.etEmail.getText().toString());
                            currentActivity.startActivity(goToConfirmAccountIntent);
                            break;
                        case REGISTER_FAILED:
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    RegisterActivity.tvErrorMessage.setText(systemMessage.message);
                                }
                            });
                            break;
                        case LOGIN_SUCCESS:
                            goToHomeIntent.putExtra(Constants.EMAIL, LoginActivity.etEmail.getText().toString());
                            currentActivity.startActivity(goToHomeIntent);
                            break;
                        case LOGIN_FAILED:
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    LoginActivity.tvErrorMessage.setText(systemMessage.message);
                                }
                            });
                            break;
                        case ACCOUNT_NOT_CONFIRMED:
                            goToConfirmAccountIntent.putExtra(Constants.EMAIL, LoginActivity.etEmail.getText().toString());
                            currentActivity.startActivity(goToConfirmAccountIntent);
                            break;
                        case CONFIRMATION_CODE_SUCCESS:
                            currentActivity.startActivity(goToLoginIntent);
                            break;
                        case CONFIRMATION_CODE_FAILED:
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    ConfirmAccountActivity.tvErrorMessage.setText(systemMessage.message);
                                }
                            });
                            break;
                        case SERVER_CLOSED:
                            goToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            currentActivity.startActivity(goToMainIntent);
                            break;

                    }
                }
                else if (object instanceof UserPacket)
                {
                    UserPacket user = (UserPacket)object;

                    switch (user.type)
                    {
                        case RECEIVE_USER:
                            String txt = String.format(
                                    "Welcome %s.\n" +
                                            "Id: %d\n" +
                                            "Email: %s\n" +
                                            "First name: %s\n" +
                                            "Last name: %s\n" +
                                            "Age: %d\n" +
                                            "Registered on: %s\n" +
                                            "Last login: %s",
                                    user.username, user.id, user. email, user.first_name, user.last_name, user.age, user.registered_on, user.last_login);
                            currentActivity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    HomeActivity.tvWelcome.setText(txt);
                                }
                            });
                            break;
                    }
                }
            }
        };
    }
}
