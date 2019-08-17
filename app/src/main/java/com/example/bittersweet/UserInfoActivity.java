package com.example.bittersweet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Button start;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Fragment userinfo1 = new UserInfo1();
        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.userinfo_container, userinfo1);
        ft.commit();

        toolbar = (Toolbar) findViewById(R.id.userinfo_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.inflateMenu(R.menu.user_info_menu);

        // show back button if it's not the first fragment of user info

        start = (Button) findViewById(R.id.userinfo_start);

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.userinfo_start) {

        }

    }

}
