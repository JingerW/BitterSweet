package com.example.bittersweet;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bittersweet.Adapter.DiaryAdapter;
import com.example.bittersweet.Model.Record;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryActivity extends DrawerActivity{

    @NonNull
    private static final String TAG = "DiaryActivityDebug";
    private static final String COLLECTION_NAME = "User";
    private static final String SUB_COLLECTION_NAME = "BloodGlucoseRecord";
    private static final String FIELD_NAME = "dateTime";
    private static final String FORMAT = "dd/MM/yyyy";
    public static final String DOCUMENT_ID = "com.example.bittersweet.DOCUMENT_ID";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(FORMAT, Locale.CHINA);
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private String uid;
    private CollectionReference ref;
    private DiaryAdapter adapter;
    private RecyclerView recyclerView;
    private TextView dateFilterStart, dateFilterEnd;
    private DatePickerDialog datePickerDialogStart, datePickerDialogEnd;
    private DatePickerDialog.OnDateSetListener mdateSetListenerStart, mdateSetListenerEnd;
    private Button searchGo, searchClear;
    private LinearLayout searchClearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_diary,null,false);
        drawerLayout.addView(contentView,0);

        setFirebaseStore();

        setRecyclerViewAdapter();

        setSearchButton();

        setClearButton();
    }

    private void setFirebaseStore() {
        // set up fire store
        firebaseAuth = FirebaseAuth.getInstance();
        // get current user
        currentUser = firebaseAuth.getCurrentUser();
        // get database
        db = FirebaseFirestore.getInstance();
        // get user id
        uid = currentUser.getUid();
        // get reference for sub collection blood glucose
        ref = db.collection(COLLECTION_NAME).document(uid).collection(SUB_COLLECTION_NAME);
    }

    private void setRecyclerViewAdapter() {
        // set query
        Query query = ref.orderBy(FIELD_NAME);
        // set options
        FirestoreRecyclerOptions<Record> options = new FirestoreRecyclerOptions.Builder<Record>()
                .setQuery(query, Record.class).build();
        // get option to adapter
        adapter = new DiaryAdapter(options);
        // set adapter in recyclerview
        recyclerView = findViewById(R.id.diary_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // delete item
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnRecordClickListener((documentSnapshot, position, expan) -> {
            if (expan.isExpanded()){expan.collapse();}else {expan.expand();}
        });

        adapter.setOnEditClickListener(new DiaryAdapter.onEditClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Record record = documentSnapshot.toObject(Record.class);
                String documentID = documentSnapshot.getId();
                Intent intent = new Intent(DiaryActivity.this, EditRecordActivity.class);
                intent.putExtra(DOCUMENT_ID, documentID);
                Log.d(TAG,"documentID: "+documentID);
                startActivity(intent);
            }
        });
    }

    private void setSearchButton() {
        // set date picker dialog
        setDatePickerDialog();
        searchGo = (Button) findViewById(R.id.search_go_btn);
        searchGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get select dates
                String currentS = dateFilterStart.getText().toString();
                String currentE = dateFilterEnd.getText().toString();

                // if start and end date are both selected=
                try {
                    Date cs = FORMATTER.parse(currentS);
                    Date ce = FORMATTER.parse(currentE);

                    // check if start date is after end start, vice versa
                    if (ce.before(cs) || cs.after(ce)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select correct start and end date", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    } else {
                        // show clear button
                        searchClearLayout = (LinearLayout) findViewById(R.id.search_clear_btn_layout);
                        searchClearLayout.setVisibility(View.VISIBLE);

                        // update query
                        ce = getNextDay(ce);
                        Log.d(TAG, "start date : "+cs);
                        Log.d(TAG, "end date : "+ce);

                        Query query = ref.orderBy(FIELD_NAME);
                        Query updateQ = ref.orderBy(FIELD_NAME)
                                .whereGreaterThanOrEqualTo(FIELD_NAME, cs)
                                .whereLessThan(FIELD_NAME, ce);

                        FirestoreRecyclerOptions<Record> options = new FirestoreRecyclerOptions.Builder<Record>()
                                .setQuery(updateQ, Record.class).build();
                        // update option
                        adapter.updateOptions(options);
                    }
                } catch (ParseException parseError){
                    Log.d(TAG, "parse exception");
                }

            }
        });
    }

    private void setClearButton() {
        searchClear = (Button) findViewById(R.id.search_clear_btn);
        searchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide clear button
                searchClearLayout.setVisibility(View.GONE);
                dateFilterStart.setText(getApplicationContext().getResources().getString(R.string.start_date));
                dateFilterEnd.setText(getApplicationContext().getResources().getString(R.string.end_date));
                // clear query
                Query clearQuery = ref.orderBy(FIELD_NAME);
                FirestoreRecyclerOptions<Record> options = new FirestoreRecyclerOptions.Builder<Record>()
                        .setQuery(clearQuery, Record.class).build();
                adapter.updateOptions(options);
            }
        });
    }


    private void setDatePickerDialog() {
        // set date picker dialog start
        dateFilterStart = (TextView) findViewById(R.id.sort_by_date_start);
        dateFilterStart.setOnClickListener(view -> {
            // get current text
            String currentS = dateFilterStart.getText().toString();
            List<Integer> listS = getCurrentDate(currentS);
            datePickerDialogStart = new DatePickerDialog(DiaryActivity.this,
                    mdateSetListenerStart, listS.get(0), listS.get(1), listS.get(2));
            datePickerDialogStart.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialogStart.show();
        });
        mdateSetListenerStart = (datePicker, i, i1, i2) -> {
            // set text view
            String selectDate = i2+"/"+(i1+1)+"/"+i;
//            Log.d(TAG, "select date: "+selectDate);
            dateFilterStart.setText(selectDate);
        };

        // end date
        dateFilterEnd = (TextView) findViewById(R.id.sort_by_date_end);
        dateFilterEnd.setOnClickListener(view -> {
            // get current text
            String currentE = dateFilterEnd.getText().toString();
            List<Integer> listE = getCurrentDate(currentE);
            datePickerDialogEnd = new DatePickerDialog(DiaryActivity.this,
                    mdateSetListenerEnd, listE.get(0), listE.get(1), listE.get(2));
            datePickerDialogEnd.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialogEnd.show();
        });
        mdateSetListenerEnd = (datePicker, i, i1, i2) -> {
            // set text view
            String selectDate = i2+"/"+(i1+1)+"/"+i;
//            Log.d(TAG, "select date: "+selectDate);
            dateFilterEnd.setText(selectDate);
        };
    }

    private Date getNextDay(Date ce) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ce);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();

    }

    private List<Integer> getCurrentDate(String text) {
        List<Integer> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        try {
            Date c = FORMATTER.parse(text);
            calendar.setTime(c);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            list.add(year);
            list.add(month);
            list.add(day);
            return list;
        } catch (ParseException e) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            list.add(year);
            list.add(month);
            list.add(day);
            return list;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_button) {
            startActivity(new Intent(DiaryActivity.this ,AddRecordActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
