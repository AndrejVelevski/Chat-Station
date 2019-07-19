package com.mpip.chatstation.Fragments;

import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.mpip.chatstation.Activities.LoginRegisterActivity;
import com.mpip.chatstation.Config.Constants;
import com.mpip.chatstation.Networking.SendPacketThread;
import com.mpip.chatstation.Packets.RegisterUserPacket;
import com.mpip.chatstation.R;
import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

public class SignUpFragment extends Fragment implements OnClickListener {
    private View view;
    private EditText firstName, lastName, emailId, age, username,
            password, confirmPassword;
    private TextView login;
    private Button signUpButton;
    private CheckBox terms_conditions;

    public static TextView signUpTitle;
    public static String emailuserEmail;

    public SignUpFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        initViews();
        setListeners();
        return view;
    }

    // Initialize all views
    private void initViews() {
        firstName = view.findViewById(R.id.regFirstName);
        lastName = view.findViewById(R.id.regLastName);
        emailId = view.findViewById(R.id.regEmail);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        signUpButton = view.findViewById(R.id.signUpBtn);
        login = view.findViewById(R.id.already_user);
        terms_conditions = view.findViewById(R.id.terms_conditions);
        age = view.findViewById(R.id.regAge);
        username = view.findViewById(R.id.regUsername);
        signUpTitle = view.findViewById(R.id.signUpTitle);

        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(R.xml.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            login.setTextColor(csl);
            terms_conditions.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn:

                // Call checkValidation method
                checkValidation();
                break;

            case R.id.already_user:

                // Replace login fragment
                new LoginRegisterActivity().replaceLoginFragment();
                break;
        }

    }

    // Check Validation Method
    private void checkValidation() {

        // Get all edittext texts
        String getFirstName = firstName.getText().toString();
        String getLastName = lastName.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();
        String getAge = age.getText().toString();
        String getUsername = username.getText().toString();

        // Check if all strings are null or not
        if (getUsername.equals("") || getUsername.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0)

            new CustomToast().Show_Toast(getActivity(), view,
                    " Fields with red underline are required.");

            // Check if email id valid or not
        else if (!Pattern.compile(String.valueOf(Patterns.EMAIL_ADDRESS)).matcher(getEmailId).matches())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Email Id is Invalid.");

            // Check if both password should be equal
        else if (!getConfirmPassword.equals(getPassword))
            new CustomToast().Show_Toast(getActivity(), view,
                    "Both password doesn't match.");

            // Make sure user should check Terms and Conditions checkbox
        else if (!terms_conditions.isChecked())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Please select Terms and Conditions.");

            // Else do signup or do your stuff
        else{
//            Toast.makeText(getActivity(), "Do SignUp.", Toast.LENGTH_SHORT)
//                    .show();

            RegisterUserPacket user = new RegisterUserPacket();
            user.email = getEmailId;
            user.username = getUsername;
            user.password = BCrypt.hashpw(getPassword, Constants.SALT);
            user.first_name = getFirstName;
            user.last_name = getLastName;

            if (getAge.length() > 0)
                user.age = Integer.valueOf(getAge);

            emailuserEmail = getEmailId;
            new SendPacketThread(user).start();

        }

    }


}