package com.example.bittersweet;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class DiaryActivity extends DrawerActivity implements View.OnClickListener {

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
    private Spinner sortByType;
    private TextView sortByDate;
    private CheckBox openDeleteButton;
    private String sortTypes;
    private DatePickerDialog.OnDateSetListener mdateSetListener;


    boolean isDeleteButtonPressed = false;

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

        setSortByType();

        setSortByDate();

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

    public void setSortByType() {
        sortByType = (Spinner) findViewById(R.id.sort_by_type_spinner);
        // set spinner
        ArrayAdapter<CharSequence> sortTypeList = ArrayAdapter.createFromResource(this,
                R.array.sort_type_list, R.layout.spinner_dropdown_item);
        sortByType.setAdapter(sortTypeList);
    }

    public void setSortByDate() {
        sortByDate = (TextView) findViewById(R.id.sort_by_date);
        // set date picker
        sortByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dateDialog = new DatePickerDialog(DiaryActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mdateSetListener, year, month, day);
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dateDialog.show();
            }
        });

        mdateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                // i = year, i1 = month, i2 = day
                Log.d(TAG, "DateSet: date: "+i+"/"+i1+"/"+i2);
                sortByDate.setText(i2+"/"+i1+"/"+i);
            }
        };
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
                                // get document id for each record and store them in the model
                                // easier for later deleting record
                                String documentID = document.getId();
                                Record record = document.toObject(Record.class);
                                record.setDocumentID(documentID);
                                records.add(record);
                                // show info in logcat
                                // record.showBloodGlucose(TAG);
                            }

                            // sort data and set adapter
                            diaryAdapter = new SectionedRecyclerViewAdapter();
                            diaryAdapter = setSectionAdapter(diaryAdapter, records);
                            Log.d(TAG, "button select: "+isDeleteButtonPressed);
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
            // same header record
            // if current record's date equals last record's date AND this is not last record
            // add record to itemList and move to next one
            else if (record.getDate().equals(date)) {
                // save current record into child list
                itemList.add(record);
                Log.d(TAG, "child: "+record.getTime());
                // if this record is the last record in the list, store it now
                if (records.indexOf(record) == records.size()-1) {
                    sectionAdapter.addSection(new DiarySection(date, itemList));
                    Log.d(TAG, "Last child: "+record.getTime());
                }
            }
            // different header record
            // if current record's date does not equals to last record's date, create new section
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
                // if this record is the last record in the list, store it now
                if (records.indexOf(record) == records.size()-1) {
                    sectionAdapter.addSection(new DiarySection(date, itemList));
                    Log.d(TAG, "Last child: "+record.getTime());
                }
            }
        }
        return sectionAdapter;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

    }
}
