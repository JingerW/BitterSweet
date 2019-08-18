package com.example.bittersweet;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;


public class UserInfo1 extends Fragment {

    private static final String TAG = "Userinfo1";

    private Button start;
    private FragmentTransaction ft;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info_1, container, false);

        start = (Button) view.findViewById(R.id.userinfo_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment userinfo2 = new UserInfo2();
                ft = getFragmentManager().beginTransaction();
                ft.add(R.id.userinfo_container, userinfo2);
                ft.commit();
            }
        });


        return view;
    }
}
