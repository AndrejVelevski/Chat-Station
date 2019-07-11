package com.mpip.chatstation.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mpip.chatstation.Adapters.MessageBoxAdapter;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.R;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity
{
    /*RecyclerView rvMessageBox;
    RecyclerView.Adapter mbAdapter;
    RecyclerView.LayoutManager layoutManager;

    EditText etMessage;
    Button btnSend;

    MessagePacket mp;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            username = extras.getString("username");
        }

        rvMessageBox = findViewById(R.id.rvMessageBox);
        layoutManager = new LinearLayoutManager(this);
        rvMessageBox.setLayoutManager(layoutManager);

        final List<String> data = new ArrayList<String>();
        mbAdapter = new MessageBoxAdapter(data);
        rvMessageBox.setAdapter(mbAdapter);

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        mp = new MessagePacket();

        MainActivity.client.addListener(new Listener()
        {
            public void received(Connection connection, Object object)
            {
                if (object instanceof MessagePacket)
                {
                    MessagePacket packet = (MessagePacket)object;
                    data.add(packet.text);

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mbAdapter.notifyDataSetChanged();
                            rvMessageBox.scrollToPosition(data.size()-1);
                        }
                    });
                }
            }
        });

        mp.text = username + " connected to the server.";
        new SendPacketThread(mp).start();
    }

    public void send(View view)
    {
        String message = etMessage.getText().toString();

        if (message.trim().length() != 0)
        {
            mp.text = username + ": " + message;
            new SendPacketThread(mp).start();
        }
        etMessage.setText("");
        //try
        //{
            //InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        //} catch (Exception e) {}
    }*/
}