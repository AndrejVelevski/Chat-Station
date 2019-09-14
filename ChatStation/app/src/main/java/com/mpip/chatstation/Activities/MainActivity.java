package com.mpip.chatstation.Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Config.UserLoginDetails;
import com.mpip.chatstation.Fragments.ConfirmFragment;
import com.mpip.chatstation.Fragments.LoginFragment;
import com.mpip.chatstation.Fragments.SignUpFragment;
import com.mpip.chatstation.Packets.LastMessagePacket;
import com.mpip.chatstation.Networking.ConnectToServerThread;
import com.mpip.chatstation.Networking.KryoListener;
import com.mpip.chatstation.Packets.ConfirmUserPacket;
import com.mpip.chatstation.Packets.FriendRequestPacket;
import com.mpip.chatstation.Packets.FriendResponsePacket;
import com.mpip.chatstation.Packets.LoginUserPacket;
import com.mpip.chatstation.Packets.MessagePacket;
import com.mpip.chatstation.Packets.PrivateMessagePacket;
import com.mpip.chatstation.Packets.ReceiveFriendRequestsPacket;
import com.mpip.chatstation.Packets.ReceiveFriendsPacket;
import com.mpip.chatstation.Packets.ReceiveLastMessagesPacket;
import com.mpip.chatstation.Packets.ReceiveMessagesHistoryPacket;
import com.mpip.chatstation.Packets.ReceiveRandomChatPacket;
import com.mpip.chatstation.Packets.ReceiveUserPacket;
import com.mpip.chatstation.Packets.RegisterUserPacket;
import com.mpip.chatstation.Packets.RequestFriendRequestsPacket;
import com.mpip.chatstation.Packets.RequestFriendsPacket;
import com.mpip.chatstation.Packets.RequestLastMessagesPacket;
import com.mpip.chatstation.Packets.RequestMessagesHistoryPacket;
import com.mpip.chatstation.Packets.RequestRandomChatPacket;
import com.mpip.chatstation.Packets.RequestUserPacket;
import com.mpip.chatstation.Packets.ResendCodePacket;
import com.mpip.chatstation.Packets.SystemMessagePacket;
import com.mpip.chatstation.R;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import static com.mpip.chatstation.Config.Constants.serverIP;
import static com.mpip.chatstation.Config.Constants.serverPort;

public class MainActivity extends AppCompatActivity
{
    public static FragmentManager fragmentManager;
    public static Client client = null;
    public static UserLoginDetails uld;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KryoListener.currentActivity = this;

        fragmentManager = getSupportFragmentManager();

        if (client == null)
        {
            client = new Client();
            Kryo kryo = client.getKryo();
            kryo.register(SystemMessagePacket.Type.class);
            kryo.register(SystemMessagePacket.class);
            kryo.register(RegisterUserPacket.class);
            kryo.register(LoginUserPacket.class);
            kryo.register(ConfirmUserPacket.class);
            kryo.register(ResendCodePacket.class);
            kryo.register(RequestUserPacket.class);
            kryo.register(ReceiveUserPacket.class);
            kryo.register(RequestRandomChatPacket.class);
            kryo.register(ReceiveRandomChatPacket.class);
            kryo.register(MessagePacket.Type.class);
            kryo.register(MessagePacket.class);
            kryo.register(FriendRequestPacket.class);
            kryo.register(FriendResponsePacket.Type.class);
            kryo.register(FriendResponsePacket.class);
            kryo.register(String[].class);
            kryo.register(List.class);
            kryo.register(ArrayList.class);
            kryo.register(RequestFriendRequestsPacket.class);
            kryo.register(ReceiveFriendRequestsPacket.class);
            kryo.register(RequestFriendsPacket.class);
            kryo.register(ReceiveFriendsPacket.class);
            kryo.register(RequestMessagesHistoryPacket.class);
            kryo.register(ReceiveMessagesHistoryPacket.class);
            kryo.register(PrivateMessagePacket.class);
            kryo.register(RequestLastMessagesPacket.class);
            kryo.register(LastMessagePacket.class);
            kryo.register(ReceiveLastMessagesPacket.class);
            client.start();
            KryoListener.createListener();
            client.addListener(KryoListener.listener);
        }

        connectToServer();

        FileInputStream inputStream;
        ObjectInputStream objectInputStream;
        try
        {
            inputStream = openFileInput("loginDetails.ld");
            objectInputStream = new ObjectInputStream(inputStream);
            uld = (UserLoginDetails) objectInputStream.readObject();
            inputStream.close();
            objectInputStream.close();
        }
        catch(Exception e){}

        replaceLoginFragment();

        findViewById(R.id.close_activity).setOnClickListener((v) -> finish());
    }

    // Replace Login Fragment with animation
    public static void replaceLoginFragment()
    {
        fragmentManager.beginTransaction()
                       .setCustomAnimations(R.anim.left_enter, R.anim.right_exit)
                       .replace(R.id.frameContainer, new LoginFragment(), Constants.Login_Fragment)
                       .commit();
    }

    public static void replaceSignUpFragment()
    {
        fragmentManager.beginTransaction()
                       .setCustomAnimations(R.anim.left_enter, R.anim.right_exit)
                       .replace(R.id.frameContainer, new SignUpFragment(), Constants.SignUp_Fragment)
                       .commit();
    }

    public static void replaceConfirmFragment(String value)
    {
        fragmentManager.beginTransaction()
                       .setCustomAnimations(R.anim.left_enter, R.anim.right_exit)
                       .replace(R.id.frameContainer, new ConfirmFragment(value), Constants.Confirm_Fragment)
                       .commit();
    }

    @Override
    public void onBackPressed() {

        // Find the tag of signup and forgot password fragment
        Fragment SignUp_Fragment = fragmentManager
                .findFragmentByTag(Constants.SignUp_Fragment);
        Fragment ForgotPassword_Fragment = fragmentManager
                .findFragmentByTag(Constants.ForgotPassword_Fragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if (SignUp_Fragment != null)
            replaceLoginFragment();
        else if (ForgotPassword_Fragment != null)
            replaceLoginFragment();
        else
            super.onBackPressed();
    }

    public static void connectToServer()
    {
        try
        {
            ConnectToServerThread thread = new ConnectToServerThread(client, serverIP, serverPort, 1000);

            thread.start();
            try
            {
                thread.join();
            }
            catch (InterruptedException e) {e.printStackTrace();}
            /*if (thread.connectionSuccessful)
            {
                Toast.makeText(this,"Connected to server",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this,"Failed to connect to server",Toast.LENGTH_LONG).show();
            }*/
        }
        catch(Exception e) { e.printStackTrace();}
    }
}
