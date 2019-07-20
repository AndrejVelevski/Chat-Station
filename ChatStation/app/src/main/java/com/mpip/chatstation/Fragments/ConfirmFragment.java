package com.mpip.chatstation.Fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.ConfirmUserPacket;
import com.mpip.chatstation.Packets.LoginUserPacket;
import com.mpip.chatstation.Packets.ResendCodePacket;
import com.mpip.chatstation.R;
import com.mpip.chatstation.Config.Constants;

import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

public class ConfirmFragment extends Fragment implements OnClickListener {
    private static View view;

    private EditText etConfirmCode;
    private Button btConfirm, btResend;
    private static LinearLayout confirmLayout;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;

    public static String userEmail;
    private static FragmentActivity context;

    public ConfirmFragment(String email) {
        userEmail = email;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_confirm_account, container, false);
        initViews();
        setListeners();
        context = getActivity();
        return view;
    }

    // Initiate Views
    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        etConfirmCode = view.findViewById(R.id.etConfirmAccountCode);
        btConfirm = view.findViewById(R.id.btnConfirmAccountConfirm);
        btResend = view.findViewById(R.id.btnConfirmAccountResend);
        confirmLayout = view.findViewById(R.id.confirmCodeLinerLayout);


        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);

    }

    // Set Listeners
    private void setListeners() {
        btConfirm.setOnClickListener(this);
        btResend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirmAccountResend:
                resend();
                break;


            case R.id.btnConfirmAccountConfirm:
                checkConfriamtionCode();
                break;
        }

    }

    private void checkConfriamtionCode() {
        String confirmationCode = etConfirmCode.getText().toString();

        if (confirmationCode.equals("") || confirmationCode.length() == 0) {
            showError("Enter confirmation code.");
        }
        else{
           confirm();
        }

    }

    private void confirm()
    {
        ConfirmUserPacket packet = new ConfirmUserPacket();
        packet.email = userEmail;
        packet.confirm_code = etConfirmCode.getText().toString();

        new SendPacketThread(packet).start();
    }

    private void resend()
    {
        ResendCodePacket packet = new ResendCodePacket();
        packet.email = userEmail;
        new SendPacketThread(packet).start();
    }

    public static void showError(String msg){
        context.runOnUiThread( () -> {
            confirmLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(context, view,
                    msg);
        });
    }
}