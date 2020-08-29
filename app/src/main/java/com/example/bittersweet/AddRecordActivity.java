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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.bittersweet.Helper.NumberInputFilter;
import com.example.bittersweet.Model.BpHr;
import com.example.bittersweet.Model.Exercise;
import com.example.bittersweet.Model.Ketone;
import com.example.bittersweet.Model.Medication;
import com.example.bittersweet.Model.Record;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Calendar;


public class AddRecordActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "AddRecordActivityDebug";
    private final static String COLLECTION_NAME = "User";
    private final static String SUB_COLLECTION_NAME = "BloodGlucoseRecord";

    private String dateString, timeString, label1, label2;
    private double bgLevel;
    private boolean ketoneState;
    private ArrayList<String> labels;

    private Toolbar toolbar;
    private Button bloodGlucoseLevel;
    private RelativeLayout bloodGlucoseBG;
    private LinearLayout ketoneLayout, ketoneInputLayout;
    private TextView time,date;
    private RadioGroup labelMeal1, labelMeal2, ketoneToggle, exGroup;
    private RadioButton exRun, exSwim, exBike, exFitness;
    private Spinner insulinType;
    // number edit text
    private EditText ketoneInput, insulinInput, bpUpperInupt, bpLowerInput, heartRateInput, exTimeM, exTimeH;
    // long note edit text
    private EditText note, otherMed, food, otherExercise;

    private DatePickerDialog datePicker;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog timePicker;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    //expandable layouts
    private TextView medicationELButton, bpHrELButton, foodELButton, acivityELButton, noteELButton;
    private ExpandableLayout medicationEL, bpHrEL, foodEL, acivityEL, noteEL;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_record);

        setFireStore();

        setToolbar();

        setExpandable();

        setBloodGlucoseLevel();

        setInputDateTime();

        setInputLabel();

        setInputMedication();

        setInputBPandHR();

        setInputExercise();

        setInputFood();

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

    private void setExpandable() {
        // expandable buttons
        medicationELButton = (TextView) findViewById(R.id.medication_title);
        bpHrELButton       = (TextView) findViewById(R.id.bp_hr_title);
        foodELButton       = (TextView) findViewById(R.id.food_title);
        acivityELButton    = (TextView) findViewById(R.id.activity_title);
        noteELButton       = (TextView) findViewById(R.id.blood_glucose_note_title);
        // set listener
        medicationELButton.setOnClickListener(this);
        bpHrELButton.setOnClickListener(this);
        foodELButton.setOnClickListener(this);
        acivityELButton.setOnClickListener(this);
        noteELButton.setOnClickListener(this);
        // expandable layouts
        medicationEL = (ExpandableLayout) findViewById(R.id.medication_expandable_layout);
        bpHrEL       = (ExpandableLayout) findViewById(R.id.bp_hr_expandable_layout);
        foodEL       = (ExpandableLayout) findViewById(R.id.food_expandable_layout);
        acivityEL    = (ExpandableLayout) findViewById(R.id.activity_expandable_layout);
        noteEL       = (ExpandableLayout) findViewById(R.id.note_expandable_layout);
        // set listener
        medicationEL.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d(TAG, "Medication layout State: "+state);
            }
        });
        bpHrEL.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d(TAG, "Blood pressure and heart rate layout State: "+state);
            }
        });
        foodEL.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d(TAG, "Food layout State: "+state);
            }
        });
        acivityEL.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d(TAG, "Activity layout State: "+state);
            }
        });
        noteEL.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d(TAG, "Note layout State: "+state);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_button, menu);
        return true;
    }

    /**
     * This function override onOptionsItemSelected.
     * If save button is pressed, call
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // check save button id and check blood glucose level is not empty
        if (id == R.id.save_button ) {
            // get current user id
            String uid = currentUser.getUid();
            Log.d(TAG, uid);

            // get current inputs and check if valid
            Record record = collectInputs();
            if (record.checkInput()) {
                //upload to firestore
                db.collection(COLLECTION_NAME).document(uid).collection(SUB_COLLECTION_NAME)
                        .add(record)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "added new record");
                                    startActivity(new Intent(AddRecordActivity.this, MainActivity.class));
                                } else {
                                    Log.d(TAG, task.getException().getMessage());
                                    Toast.makeText(AddRecordActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
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

        // ketone
        setInputKetone();

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

                        Drawable red_bg    = getResources().getDrawable(R.drawable.drop_red);
                        Drawable red_bt    = getResources().getDrawable(R.drawable.glucose_button_red);
                        Drawable green_bg  = getResources().getDrawable(R.drawable.drop_green);
                        Drawable green_bt  = getResources().getDrawable(R.drawable.glucose_button_green);
                        Drawable yellow_bg = getResources().getDrawable(R.drawable.drop_yellow);
                        Drawable yellow_bt = getResources().getDrawable(R.drawable.glucose_button_yellow);

                        if (bgLevel <= 0) {
                            // not a valid input, show warning
                            bloodGlucoseLevel.setText("");
                            Toast.makeText(AddRecordActivity.this, R.string.valid_bg_level_warning, Toast.LENGTH_SHORT).show();
                        } else if (bgLevel >= 13) {
                            // blood glucose level is too high show red alert
                            bloodGlucoseLevel.setBackground(red_bt);
                            bloodGlucoseBG.setBackground(red_bg);
                            // show ketone option
                            ketoneLayout.setVisibility(View.VISIBLE);
                        } else if (bgLevel <= 4) {
                            // blood glucose level is too low, show red alert
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

    private void setInputKetone() {
        ketoneLayout      = (LinearLayout) findViewById(R.id.ketone_layout);
        ketoneToggle      = (RadioGroup) findViewById(R.id.ketone_toggle);
        ketoneInputLayout = (LinearLayout) findViewById(R.id.ketone_input_layout);
        ketoneInput     = (EditText) findViewById(R.id.ketone_input);
        // set listener for radio button group
        ketoneToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int ketoneState) {
                if (ketoneState == R.id.ketone_yes) {ketoneInputLayout.setVisibility(View.VISIBLE);}
                else {ketoneInputLayout.setVisibility(View.INVISIBLE);}
            }
        });
        // set filter for ketone input
        ketoneInput.setFilters(new InputFilter[]{new NumberInputFilter(0,10,2,1,true)});
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
        date = (TextView) findViewById(R.id.blood_glucose_date);
        dateString = String.format(months[month] + " %02d, %d", day, year);
        date.setText(" " + dateString);
        date.setOnClickListener(new View.OnClickListener() {
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
                date.setText(months[i1] + " " + dateString);
            }
        };

        // time input
        time = (TextView) findViewById(R.id.blood_glucose_time);
        timeString = String.format("%02d:%02d", hour, mintues);
        time.setText(timeString);
        time.setOnClickListener(new View.OnClickListener() {
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
                time.setText(timeString);
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

    private void setInputMedication() {
        insulinInput = (EditText) findViewById(R.id.insulin);
        insulinType  = (Spinner) findViewById(R.id.insulin_type);
        otherMed     = (EditText) findViewById(R.id.other_medication);

        // add spinner list item
        insulinInput.setFilters(new InputFilter[]{new NumberInputFilter(0,100)});
        ArrayAdapter<CharSequence> insulinTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.insulin_type_list, R.layout.spinner_dropdown_item);
        insulinType.setAdapter(insulinTypeAdapter);
    }

    private void setInputBPandHR() {
        bpUpperInupt   = (EditText) findViewById(R.id.bp_upper);
        bpLowerInput   = (EditText) findViewById(R.id.bp_lower);
        heartRateInput = (EditText) findViewById(R.id.heart_rate);

        // set max value
        bpUpperInupt.setFilters(new InputFilter[]{new NumberInputFilter(0,300)});
        bpLowerInput.setFilters(new InputFilter[]{new NumberInputFilter(0,200)});
        heartRateInput.setFilters(new InputFilter[]{new NumberInputFilter(0,300)});

        // if user has fill in both upper and lower bp, check if they are invalid
        bpUpperInupt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (!bpLowerInput.getText().toString().isEmpty() && !bpLowerInput.getText().toString().isEmpty()) {
                    int upper = Integer.parseInt(bpUpperInupt.getText().toString());
                    int lower = Integer.parseInt(bpLowerInput.getText().toString());
                    if (upper <= lower) {
                        Toast toast = Toast.makeText(getApplicationContext(),"Upper bp cannot be smaller than lower bp.",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        bpUpperInupt.setText("");
                    }
                }
            }
        });
        bpLowerInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (!bpUpperInupt.getText().toString().isEmpty() && !bpLowerInput.getText().toString().isEmpty()) {
                    int upper = Integer.parseInt(bpUpperInupt.getText().toString());
                    int lower = Integer.parseInt(bpLowerInput.getText().toString());
                    if (lower >= upper) {
                        Toast toast = Toast.makeText(getApplicationContext(),"Lower bp cannot be greater than upper bp.",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        bpLowerInput.setText("");
                    }
                }
            }
        });

    }

    private void setInputExercise() {
        exGroup = (RadioGroup)findViewById(R.id.exercise_group);
        exTimeM = (EditText) findViewById(R.id.exercise_time_m);
        exTimeH = (EditText) findViewById(R.id.exercise_time_h);
        otherExercise = (EditText) findViewById(R.id.other_exercise_input);

        exTimeM.setFilters(new InputFilter[]{new NumberInputFilter(0,60)});
        exTimeH.setFilters(new InputFilter[]{new NumberInputFilter(0,100)});

    }

    private String getSelectedExercise() {
        int selectedEx = exGroup.getCheckedRadioButtonId();
        String s = "";
        switch (selectedEx) {
            case R.id.exercise_running: {s = "Running"; break;}
            case R.id.exercise_swimming: {s = "Swimming"; break;}
            case R.id.exercise_biking: {s = "Biking"; break;}
            case R.id.exercise_fitness: {s = "Fitness"; break;}
        }
        return s;
    }

    private void setInputFood() {
        food = (EditText) findViewById(R.id.food_input);
        food.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) return true;
                return false;
            }
        });
    }

    private void setInputNotes() {
        note = (EditText) findViewById(R.id.blood_glucose_note_input);
        note.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) return true;
                return false;
            }
        });
    }

    /**
     * This function collects all current inputs.
     * Create a new Record class variable and save all inputs into this variable
     *
     * @return Record a record contain current inputs
     */
    private Record collectInputs() {
        Record bg = new Record();
        // collect current inputs
        String inputBG = bloodGlucoseLevel.getText().toString();
        String inputKe = ketoneInput.getText().toString();
        String inputIn = insulinInput.getText().toString();
        String inputInTy = insulinType.getSelectedItem().toString();
        String inputOthM = otherMed.getText().toString();
        String inputUpBp = bpUpperInupt.getText().toString();
        String inputLoBp = bpLowerInput.getText().toString();
        String inputHr = heartRateInput.getText().toString();
        String inputEx = getSelectedExercise();
        String inputExTM = exTimeM.getText().toString();
        String inputExTh = exTimeH.getText().toString();
        String inputOthEx = otherExercise.getText().toString();
        String inputFood = food.getText().toString();
        String inputNote = note.getText().toString();

        // check all number edit text before parse them

        bg.setBloodGlucose(Double.parseDouble(inputBG));

        // if ketone state is true, store ketone level
        if (!inputKe.isEmpty()) {bg.setKetone(new Ketone(Double.parseDouble(inputKe)));}
        else {bg.setKetone(new Ketone());}

        bg.setDate(dateString);
        bg.setTime(timeString);
        bg.setLabels(getSelectedLabel());

        // if insulin option is not empty, store insulin usage and type. Else store only other med
        if (!inputIn.isEmpty()) {
            bg.setMedication(new Medication(Integer.parseInt(inputIn), inputInTy, inputOthM));
        } else {bg.setMedication(new Medication(inputOthM));}
        // if bp and hr are both not empty, store them
        if (!inputUpBp.isEmpty() && !inputLoBp.isEmpty() && !inputHr.isEmpty()) {
            bg.setBpHr(new BpHr(Integer.parseInt(inputUpBp),Integer.parseInt(inputLoBp),Integer.parseInt(inputHr)));
        } else if (!inputUpBp.isEmpty() && !inputLoBp.isEmpty()) {// store only bp
            bg.setBpHr(new BpHr(Integer.parseInt(inputUpBp),Integer.parseInt(inputLoBp)));
        } else if (!inputHr.isEmpty()) {// store only hr
            bg.setBpHr(new BpHr(Integer.parseInt(inputHr)));
        } else {// store nothing
            bg.setBpHr(new BpHr());
        }

        // if exercise time is not empty store all, else only store type and other exercise note
        if (!inputExTM.isEmpty() && !inputExTh.isEmpty()) {
            bg.setAcivity(new Exercise(inputEx, Integer.parseInt(inputExTM),Integer.parseInt(inputExTh),inputOthEx));
        } else if (!inputExTM.isEmpty()) { // minute set 0
            bg.setAcivity(new Exercise(inputEx, Integer.parseInt(inputExTM),0,inputOthEx));
        } else if (!inputExTh.isEmpty()) { // hour set 0
            bg.setAcivity(new Exercise(inputEx, 0,Integer.parseInt(inputExTh),inputOthEx));
        } else {
            bg.setAcivity(new Exercise(inputEx, inputOthEx));
        }

        bg.setFood(inputFood);
        bg.setNotes(inputNote);

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

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            // expandable layouts
            case R.id.medication_title:
                if (medicationEL.isExpanded()) {medicationEL.collapse();} else {medicationEL.expand();}
                break;
            case R.id.bp_hr_title:
                if (bpHrEL.isExpanded()) {bpHrEL.collapse();} else {bpHrEL.expand();}
                break;
            case R.id.food_title:
                if (foodEL.isExpanded()) {foodEL.collapse();} else {foodEL.expand();}
                break;
            case R.id.activity_title:
                if (acivityEL.isExpanded()) {acivityEL.collapse();} else {acivityEL.expand();}
                break;
            case R.id.blood_glucose_note_title:
                if (noteEL.isExpanded()) {noteEL.collapse();} else {noteEL.expand();}
                break;
        }
    }
}
