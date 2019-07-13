package com.mpip.chatstation.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.mpip.chatstation.R;

public class HomeFragment extends Fragment {

    int count = 0;
    Button btn_count;
    AHBottomNavigation navBar;

    public HomeFragment(AHBottomNavigation navBar){
        this.navBar = navBar;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        btn_count = rootView.findViewById(R.id.btn_count);

        btn_count.setOnClickListener(v->{
            count++;
            navBar.setNotification(""+count,1);
        });


        //return super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }
}
