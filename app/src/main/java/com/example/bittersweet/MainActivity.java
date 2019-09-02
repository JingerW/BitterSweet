package com.example.bittersweet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.bittersweet.Model.User;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import com.example.bittersweet.Model.User;


public class MainActivity extends DrawerActivity {

    private static final String TAG = "MainActivityDebug";
    private static final String COLLECTION_NAME = "User";

    private Button addUserinfo;
    private Button deleteUserinfo;
    private Button updateUserinfo;

    private Button addRecord;

    private LineChart mChart;

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

        addUserinfo();

        deleteUserinfo();

//        updateUserinfo();

        addRecord = (Button) findViewById(R.id.add_record);
        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddRecordActivity.class));
            }
        });

    }


    private void addUserinfo() {
        addUserinfo = (Button) findViewById(R.id.add_userinfo);
        addUserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

    }

    private void deleteUserinfo() {
        deleteUserinfo = (Button) findViewById(R.id.delete_userinfo);
        deleteUserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
    }

    private void updateUserinfo(final Map<String, Object> updateinfo) {
        updateUserinfo = (Button) findViewById(R.id.update_userinfo);
        updateUserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = currentUser.getUid();
                Log.d(TAG, uid);

                db.collection(COLLECTION_NAME).document(uid)
                        .update(updateinfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG,"\nupdated");
                                }
                                else {
                                    Log.d(TAG, task.getException().getMessage());
                                }
                            }
                        });
            }
        });
    }

}