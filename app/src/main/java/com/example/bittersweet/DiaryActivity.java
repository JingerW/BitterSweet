package com.example.bittersweet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bittersweet.Model.BloodGlucose;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class DiaryActivity extends DrawerActivity {

    @NonNull
    private static final String TAG = "DiaryActivityDebug";
    private static final String COLLECTION_NAME = "User";
    private static final String SUB_COLLECTION_NAME = "BloodGlucoseRecord";

    private ArrayList<BloodGlucose> records;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private String uid;

    TextView test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_diary,null,false);
        drawerLayout.addView(contentView, 0);

        testButton();

        setFirebaseAuth();
    }

    private void setFirebaseAuth() {
        // set up fire store
        firebaseAuth = FirebaseAuth.getInstance();
        // get current user
        currentUser = firebaseAuth.getCurrentUser();
        // get this user's database
        db = FirebaseFirestore.getInstance();
        // get user id
        uid = currentUser.getUid();

        // create new blood glucose level record list
        records = new ArrayList<BloodGlucose>();
        db.collection(COLLECTION_NAME).document(uid).collection(SUB_COLLECTION_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // get all data and save them into array list records
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                BloodGlucose record = document.toObject(BloodGlucose.class);
                                records.add(record);
                                // show info in logcat
                                record.showBloodGlucose(TAG);
                            }

                            // sort data
                            Collections.sort(records);
//                            int len = records.size();
//                            Log.d(TAG, "collection size: "+len);
//                            for (BloodGlucose record: records) {
//                                Log.d(TAG,"DATE: "+record.getInputDate());
//                            }
//                            test.setText("collection sort end");

                            // set adapter
                        }
                    }
                });
    }

    private void testButton() {
        test = (TextView) findViewById(R.id.test_diary);
    }



}
