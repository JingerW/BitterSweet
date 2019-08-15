package com.example.bittersweet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInfoActivity extends AppCompatActivity {

    private FirebaseDatabase  fdatabase;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private User userInfo;

    private Button skip;
    private EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

//        fdatabase = FirebaseDatabase.getInstance();

    }

//    @Override
//    public void onClick(View view) {
//        int i = view.getId();
//        if (i == R.id.userinfo_skip) {
//             save data and then transfer to home page
//            mDatabase.setValue(userInfo);
//            startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
//        }
//
//    }

}
