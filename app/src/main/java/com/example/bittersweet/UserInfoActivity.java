package com.example.bittersweet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase  fdatabase;
    private DatabaseReference mDatabase;
    private UserInfoModal userInfo;

    private Button skip;
    private EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // get database reference
        fdatabase = FirebaseDatabase.getInstance();
        mDatabase = fdatabase.getReference("userInfo");
        userInfo = new UserInfoModal();

        skip = findViewById(R.id.userinfo_skip);
        skip.setOnClickListener(this);

        username = findViewById(R.id.userinfo_username_input);
        if (username != null) {
            userInfo.setUsername(username.getText().toString());
        }

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.userinfo_skip) {
            // save data and then transfer to home page
            mDatabase.setValue(userInfo);
            startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
        }

    }

}
