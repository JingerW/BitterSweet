package com.example.bittersweet;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bittersweet.Adapter.DiarySection;
import com.example.bittersweet.Model.Record;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class DiaryActivity extends DrawerActivity{

    @NonNull
    private static final String TAG = "DiaryActivityDebug";
    private static final String COLLECTION_NAME = "User";
    private static final String SUB_COLLECTION_NAME = "BloodGlucoseRecord";

    private ArrayList<Record> records;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private String uid;
    private RecyclerView recyclerView;
    private SectionedRecyclerViewAdapter diaryAdapter;
    private CheckBox bglucose, bpressure, medication, activity;
    private String sortTypes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_diary,null,false);
        drawerLayout.addView(contentView,0);

        recyclerView = (RecyclerView) findViewById(R.id.diary_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        setFirebaseAuth();

        getRecords();
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.delete_button, menu);
        return true;
    }

    private void getRecords() {
        // create new blood glucose level record list
        records = new ArrayList<Record>();
        db.collection(COLLECTION_NAME).document(uid).collection(SUB_COLLECTION_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // get all data and save them into array list records
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Record record = document.toObject(Record.class);
                                records.add(record);
                                // show info in logcat
                                // record.showBloodGlucose(TAG);
                            }

                            // sort data and set adapter
                            diaryAdapter = new SectionedRecyclerViewAdapter();
                            diaryAdapter = setSectionAdapter(diaryAdapter, records);
                            recyclerView.setAdapter(diaryAdapter);
                        }
                    }
                });
    }

    private SectionedRecyclerViewAdapter setSectionAdapter(SectionedRecyclerViewAdapter sectionAdapter, List<Record> data) {
        // first sort data by date
        Collections.sort(data, Collections.reverseOrder());
        int len = records.size();
        Log.d(TAG, "collection sort end. size: "+len);

        List<Record>  itemList  = new ArrayList<>();
        String date = "";
        for (Record record: data) {
            // first header record
            if (date.equals("")) {
                date = record.getDate();
                itemList.add(record);
                Log.d(TAG, "sort data first date: "+date);
                Log.d(TAG, "child: "+record.getTime());
            }
            // other header record
            else if (!record.getDate().equals(date)) {
                // first add previous section to the adapter
                sectionAdapter.addSection(new DiarySection(date, itemList));
                Log.d(TAG, date+" has finished");
                // then override current date
                date = record.getDate();
                Log.d(TAG, "start next header: "+date);
                // clear previous childList
                itemList  = new ArrayList<>();
                itemList.add(record);
                Log.d(TAG, "child: "+record.getTime());
            }
            // same header record
            else if (record.getDate().equals(date)) {
                // save current record into child list
                itemList.add(record);
                Log.d(TAG, "child: "+record.getTime());
            }
            // last record
            else if (record == records.get(-1)) {
                itemList.add(record);
                // add section to the adapter
                sectionAdapter.addSection(new DiarySection(date, itemList));
                Log.d(TAG, "Last child: "+record.getTime());

            }
        }
        return sectionAdapter;
    }
}
