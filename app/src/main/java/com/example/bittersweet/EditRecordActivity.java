package com.example.bittersweet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bittersweet.Model.Record;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditRecordActivity extends AppCompatActivity {

    private static final String TAG = "EditRecordActivityDebug";
    private static final String COLLECTION_NAME = "User";
    private final static String SUB_COLLECTION_NAME = "BloodGlucoseRecord";
    private final static String KEY_GLUCOSE = "bloodGlucose";
    private final static String KEY_DATETIME = "dateTime";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid = FirebaseAuth.getInstance().getUid();
    private CollectionReference ref = db.collection(COLLECTION_NAME).document(uid).collection(SUB_COLLECTION_NAME);
    private String documentID;
    private DateFormat formatter;

    private boolean isEditClicked = false;
    private Toolbar toolbar;
    private TextView glucose, glucoseEdit, dateTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        formatter = new SimpleDateFormat(getResources().getString(R.string.date_to_format));
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        documentID = intent.getStringExtra(DiaryActivity.DOCUMENT_ID);
        Log.d(TAG, "documentID in Edit: "+documentID);

        setToolbar();

        setInputFields();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (documentID != null) {
            ref.document(documentID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(EditRecordActivity.this, "Error while loading!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, error.toString());
                        return;
                    }

                    if (documentSnapshot.exists()) {
                        Date dt = documentSnapshot.getDate(KEY_DATETIME);
                        String dateTimeString = formatter.format(dt);
                        double glucoseString = documentSnapshot.getDouble(KEY_GLUCOSE);

                        if (glucoseString != 0){glucose.setText(String.valueOf(glucoseString));}
                        dateTime.setText(dateTimeString);
                    }
                }
            });
        }

    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.edit_record_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItemID = item.getItemId();
        // if edit button clicked, change to save title
        if (menuItemID == R.id.edit_button) {
            if (isEditClicked) {
                isEditClicked = false;
                item.setTitle("Edit");
            } else {
                isEditClicked = true;
                item.setTitle("Save");

                setEditTextVisible();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setInputFields() {
        glucose = (TextView) findViewById(R.id.edit_glucose);
        glucoseEdit = (EditText) findViewById(R.id.edit_glucose_editable);
        dateTime = (TextView) findViewById(R.id.edit_date);

        if (documentID != null) {
            ref.document(documentID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            Record r = task.getResult().toObject(Record.class);
                            Date dt = r.getDateTime();
                            String dateTimeString = formatter.format(dt);
                            double glucoseString = r.getBloodGlucose();

                            if (glucoseString != 0){glucose.setText(String.valueOf(glucoseString));}
                            dateTime.setText(dateTimeString);
                        } else {
                            Log.d(TAG,task.getException().getMessage());
                            Toast.makeText(EditRecordActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT);
                        }
                    }
                }
            });
        }
    }

    private void setEditTextVisible() {
        glucoseEdit.setVisibility(View.VISIBLE);
        glucoseEdit.setText(glucose.getText());
        glucose.setVisibility(View.INVISIBLE);
    }
}
