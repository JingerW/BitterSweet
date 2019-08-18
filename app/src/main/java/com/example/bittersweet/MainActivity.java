package com.example.bittersweet;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


public class MainActivity extends DrawerActivity implements View.OnClickListener{

    private static final String TAG = "MainActivityDebug";
    private static final String COLLECTION_NAME = "User";

    private Button addUserinfo;
    private Button deleteUserinfo;
    private TextView dob;
    private DatePickerDialog.OnDateSetListener mdateSetListener;
    private RadioGroup gender_group;
    private RadioGroup.OnCheckedChangeListener mradioGroupListener;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        drawerLayout.addView(contentView, 0);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        addUserinfo = (Button) findViewById(R.id.add_userinfo);
        addUserinfo.setOnClickListener(this);

        deleteUserinfo = (Button) findViewById(R.id.delete_userinfo);
        deleteUserinfo.setOnClickListener(this);

        dob = (TextView) findViewById(R.id.dob_input);
        dob.setOnClickListener(this);

        mdateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                // i = year, i1 = month, i2 = day
                Log.d(TAG, "DateSet: date: "+i+"/"+i1+"/"+i2);
                dob.setText(i2+"/"+i1+"/"+i);
            }
        };

        gender_group = (RadioGroup) findViewById(R.id.gender_group);
        gender_group.setOnCheckedChangeListener(mradioGroupListener);


    }



    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.add_userinfo) {
            String uid = currentUser.getUid();
            Log.d(TAG, uid);

            User userinfo = new User();
            Log.d(TAG, userinfo.showUser());

            db.collection(COLLECTION_NAME).document(uid)
                    .set(userinfo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG,"\nadded");
                            }
                            else {
                                Log.d(TAG, task.getException().getMessage());
                            }
                        }
                    });
        } else if (i == R.id.delete_userinfo) {
            String uid = currentUser.getUid();
            Log.d(TAG, uid);

            db.collection(COLLECTION_NAME).document(uid)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG,"\ndeleted");
                            }
                            else {
                                Log.d(TAG, task.getException().getMessage());
                            }
                        }
                    });
        } else if (i == R.id.dob_input) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dateDialog = new DatePickerDialog(MainActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mdateSetListener, year, month, day);
            dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dateDialog.show();
        }
    }


}