package com.example.bittersweet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.bittersweet.Model.BloodGlucose;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;


public class AddRecordActivity extends AppCompatActivity {

    private final static String TAG = "AddRecordActivityDebug";
    private final static String COLLECTION_NAME = "User";
    private final static String SUB_COLLECTION_NAME = "BloodGlucoseRecord";

    private Toolbar toolbar;

    private Button bloodGlucoseLevel;
    private RelativeLayout bloodGlucoseBG;
    private double bgLevel;

    private TextView inputTime;
    private TimePickerDialog timePicker;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private TextView inputDate;
    private DatePickerDialog datePicker;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private String dateString, timeString;

    private RadioGroup labelMeal1, labelMeal2;
    private String label1, label2;
    private ArrayList<String> labels;

    private EditText inputNotes;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_record);

        setFireStore();

        setToolbar();

        setBloodGlucoseLevel();

        setInputDateTime();

        setInputLabel();

        setInputNotes();

    }

    private void setFireStore() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.blood_glucose_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.simple_save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // check save button id and check blood glucose level is not empty
        if (id == R.id.save_button ) {
            // get current user id
            String uid = currentUser.getUid();
            Log.d(TAG, uid);

            // get current inputs and check if valid
            BloodGlucose bloodGlucose = getInputs();
            if (bloodGlucose.checkInput() == true) {
                //upload to firestore
                db.collection(COLLECTION_NAME).document(uid).collection(SUB_COLLECTION_NAME)
                        .add(bloodGlucose)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "added new record");
                                    startActivity(new Intent(AddRecordActivity.this, MainActivity.class));
                                } else {
                                    Log.d(TAG, task.getException().getMessage());
                                }
                                Toast.makeText(AddRecordActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                return true;
                } else {
                    //if user has not enter a valid glucose level and try to save, this warning will pop up
                    Toast.makeText(AddRecordActivity.this, R.string.save_warning,Toast.LENGTH_SHORT).show();
                }
            }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setBloodGlucoseLevel() {
        bloodGlucoseLevel = (Button) findViewById(R.id.blood_glucose);
        bloodGlucoseBG = (RelativeLayout) findViewById(R.id.blood_glucose_bg);

        // get layout inflater
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // set onclick listener onto glucose level number picker
        bloodGlucoseLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set view for the number picker
                final AlertDialog.Builder dialog = new AlertDialog.Builder(AddRecordActivity.this);
                final View picker_view = inflater.from(getApplicationContext()).inflate(R.layout.glucose_level_number_picker,null);
                dialog.setView(picker_view);

                // set up min and max number for two number pickers
                final NumberPicker numberpicker1 = (NumberPicker) picker_view.findViewById(R.id.number_before_decimal);
                numberpicker1.setMinValue(0);
                numberpicker1.setMaxValue(99);
                numberpicker1.setWrapSelectorWheel(false);
                final NumberPicker numberpicker2 = (NumberPicker) picker_view.findViewById(R.id.number_after_decimal);
                numberpicker2.setMinValue(0);
                numberpicker2.setMaxValue(9);
                numberpicker2.setWrapSelectorWheel(false);

                // set cancel and done button for the alert dialog
                dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = numberpicker1.getValue()+"."+numberpicker2.getValue();
                        bloodGlucoseLevel.setText(input);

                        // change background colour corresponding with the input glucose level
                        bgLevel = Double.parseDouble(input);

                        Drawable red_bg = getResources().getDrawable(R.drawable.drop_red);
                        Drawable red_bt = getResources().getDrawable(R.drawable.glucose_drop_button_red);
                        Drawable green_bg = getResources().getDrawable(R.drawable.drop_green);
                        Drawable green_bt = getResources().getDrawable(R.drawable.glucose_drop_button_green);
                        Drawable yellow_bg = getResources().getDrawable(R.drawable.drop_yellow);
                        Drawable yellow_bt = getResources().getDrawable(R.drawable.glucose_drop_button_yellow);

                        if (bgLevel <= 0) {
                            // not a valid input, show warning
                            bloodGlucoseLevel.setText("");
                            Toast.makeText(AddRecordActivity.this, R.string.valid_bg_level_warning, Toast.LENGTH_SHORT).show();
                        } else if (bgLevel <= 4 || bgLevel >= 13) {
                            // blood glucose level is not in a healthy range, show red alert
                            bloodGlucoseLevel.setBackground(red_bt);
                            bloodGlucoseBG.setBackground(red_bg);
                        } else if (bgLevel <= 7) {
                            // show green for healthy glucose level
                            bloodGlucoseLevel.setBackground(green_bt);
                            bloodGlucoseBG.setBackground(green_bg);
                        } else if (bgLevel <= 10) {
                            // show yellow for just above healthy range
                            bloodGlucoseLevel.setBackground(yellow_bt);
                            bloodGlucoseBG.setBackground(yellow_bg);
                        }
                        Log.d(TAG, "Selected number "+input);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void setInputDateTime() {
        final Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        final int mintues = c.get(Calendar.MINUTE);
        final String[] months = getResources().getStringArray(R.array.months);

        // date input
        inputDate = (TextView) findViewById(R.id.blood_glucose_date);
        dateString = String.format(months[month] + " %02d, %d", day, year);
        inputDate.setText(" " + dateString);
        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker = new DatePickerDialog(AddRecordActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener, year, month, day);
                datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ;
                datePicker.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                dateString = String.format(months[i1] + " %02d, %d", i2, i);
                Log.d(TAG, "DateSet: date: " + months[i1] + " " + dateString);
                inputDate.setText(months[i1] + " " + dateString);
            }
        };

        // time input
        inputTime = (TextView) findViewById(R.id.blood_glucose_time);
        timeString = String.format("%02d:%02d", hour, mintues);
        inputTime.setText(timeString);
        inputTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker = new TimePickerDialog(AddRecordActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog,
                        mTimeSetListener, hour, mintues, true);
                timePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePicker.show();
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                timeString = String.format("%02d:%02d", i, i1);
                Log.d(TAG, "Time select: " + timeString);
                inputTime.setText(timeString);
            }
        };
    }

    private void setInputLabel() {
        labelMeal1 = (RadioGroup) findViewById(R.id.label_meal1);
        labelMeal2 = (RadioGroup) findViewById(R.id.label_meal2);
    }

    private ArrayList<String> getSelectedLabel() {
        int selectedLabelId1 = labelMeal1.getCheckedRadioButtonId();
        switch (selectedLabelId1) {
            case (R.id.breakfast):
                label1 = "Breakfast";
                break;
            case (R.id.lunch):
                label1 = "Lunch";
                break;
            case (R.id.dinner):
                label1 = "Dinner";
                break;
        }
        Log.d(TAG, "SELECTED1: " + label1);

        int selectedLabelId2 = labelMeal2.getCheckedRadioButtonId();
        switch (selectedLabelId2) {
            case (R.id.before_meal):
                label2 = "Before meal";
                break;
            case (R.id.after_meal):
                label2 = "After meal";
                break;
        }
        Log.d(TAG, "SELECTED2: " + label2);

        labels = new ArrayList<String>();
        labels.add(label1);
        labels.add(label2);
        return labels;
    }

    private void setInputNotes() {
        inputNotes = (EditText) findViewById(R.id.blood_glucose_note_input);
        inputNotes.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) return true;
                return false;
            }
        });
    }

    private BloodGlucose getInputs() {
        BloodGlucose bg = new BloodGlucose();
        bg.setBloodGlucose(Double.parseDouble(bloodGlucoseLevel.getText().toString()));
        bg.setInputDate(dateString);
        bg.setInputTime(timeString);
        bg.setInputLabels(getSelectedLabel());
        bg.setInputNotes(inputNotes.getText().toString());
        bg.showBloodGlucose(TAG);
        return bg;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


}
