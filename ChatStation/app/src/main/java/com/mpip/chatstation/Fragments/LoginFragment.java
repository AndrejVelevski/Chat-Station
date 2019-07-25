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

import com.mpip.chatstation.Activities.MainActivity;
import com.mpip.chatstation.Config.UserLoginDetails;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.LoginUserPacket;
import com.mpip.chatstation.R;
import com.mpip.chatstation.Config.Constants;

import org.mindrot.jbcrypt.BCrypt;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment implements OnClickListener {
    private static View view;

    public static EditText emailid;
    public static EditText password;
    private Button loginButton;
    private TextView forgotPassword, signUp;
    private CheckBox show_hide_password;
    public static CheckBox cbRememberMe;
    private static LinearLayout loginLayout;
    public static TextView loginTV;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;

    private static FragmentActivity context;

    public LoginFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews();
        setListeners();
        context = getActivity();
        return view;
    }

    // Initiate Views
    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        emailid = view.findViewById(R.id.login_email);
        password = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.loginBtn);
        forgotPassword = view.findViewById(R.id.forgot_password);
        signUp = view.findViewById(R.id.createAccount);
        show_hide_password = view.findViewById(R.id.show_hide_password);
        cbRememberMe = view.findViewById(R.id.cbRememberMe);
        loginLayout = view.findViewById(R.id.login_layout);
        loginTV = view.findViewById(R.id.loginTitle);

        if (MainActivity.uld != null)
        {
            emailid.setText(MainActivity.uld.username_email);
            password.setText(MainActivity.uld.password);
            cbRememberMe.setChecked(true);
            checkValidation();
        }


        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);

        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(R.xml.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            forgotPassword.setTextColor(csl);
            show_hide_password.setTextColor(csl);
            signUp.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);

        cbRememberMe.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (!isChecked)
                {
                    try
                    {
                        context.deleteFile("loginDetails.ld");
                        MainActivity.uld = null;
                    }
                    catch (Exception e){}
                }
            }
        });

        // Set check listener over checkbox for showing and hiding password
        show_hide_password
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            show_hide_password.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            password.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            password.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                checkValidation();
                break;


            case R.id.forgot_password:

                // Replace forgot password fragment with animation
                /*fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_exit)
                        .replace(R.id.frameContainer,
                                new ForgotPassword_Fragment(),
                                Utils.ForgotPassword_Fragment).commit(); */
                break;
            case R.id.createAccount:

                // Replace signup frgament with animation
                MainActivity.replaceSignUpFragment();
                break;
        }

    }

    // Check Validation before login
    private void checkValidation()
    {
        // Get email id and password
        String getEmailId = emailid.getText().toString();
        String getPassword = password.getText().toString();

        // Check for both field is empty or not
        if (getEmailId.equals("") || getEmailId.length() == 0 || getPassword.equals("") || getPassword.length() == 0)
        {
            showError("Enter both credentials.");
        }
        else
            {
            LoginUserPacket packet = new LoginUserPacket();
            packet.username_email = getEmailId;
            packet.password = BCrypt.hashpw(getPassword, Constants.SALT);

            new SendPacketThread(packet).start();
        }
    }

    public static void showError(String msg){
        context.runOnUiThread( () -> {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(context, view,
                    msg);
        });
    }
}