package com.example.bittersweet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bittersweet.Helper.YearPickerDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity implements YearPickerDialog.YearPickerListener {

    private static final String TAG = "UserInfoActivityDebug";
    private static final String COLLECTION_NAME = "User";

    private TextView dob, yod;
    private DatePickerDialog.OnDateSetListener mdateSetListener;
    private RadioGroup genderGroup;
    private Spinner diabetesType;
    private Button userinfoUpdate;
    private EditText weightInput, heightInput;
    private String dateOfBirth = null;
    private String dtype = null;
    private String gender = null;
    private int yearOfDiagnose = 0;
    private double height = 0, weight = 0;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        selectDoB();

        genderGroup = (RadioGroup) findViewById(R.id.gender_group);

        selectDiabetesType();

        createYearOfDiagnosePicker();

        heightInput = (EditText) findViewById(R.id.hw_height_input);
        weightInput = (EditText) findViewById(R.id.hw_weight_input);

        userInfoUpdate();
    }

    private void selectDoB() {
        dob = (TextView) findViewById(R.id.dob_input);
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dateDialog = new DatePickerDialog(UserInfoActivity.this,
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
                dob.setText(i2+"/"+i1+"/"+i);
            }
        };
    }

    private void selectDiabetesType() {
        diabetesType = (Spinner) findViewById(R.id.diabetes_type_dropdown);
        ArrayAdapter<CharSequence> adaptor = ArrayAdapter.createFromResource(this, R.array.diabetes_type, android.R.layout.simple_spinner_item);
        adaptor.setDropDownViewResource(R.layout.spinner_view);
        diabetesType.setAdapter(adaptor);
        diabetesType.setSelection(0);
    }

    private void createYearOfDiagnosePicker() {

        yod = (TextView) findViewById(R.id.year_of_diag_input);
        yod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openYearPicker();
            }
        });
    }

    private void openYearPicker() {
        YearPickerDialog yearPickerDialog = new YearPickerDialog();
        yearPickerDialog.show(getSupportFragmentManager(), "year picker");
    }

    @Override
    public void applyText(int year) {
        yod.setText(Integer.toString(year));
    }

    private Map<String, Object> getData() {

        dateOfBirth = dob.getText().toString();

        dtype = diabetesType.getSelectedItem().toString();

        int selectedButtonId = genderGroup.getCheckedRadioButtonId();
        RadioButton selectedButton = (RadioButton) genderGroup.findViewById(selectedButtonId);
        gender = selectedButton.getText().toString();

        yearOfDiagnose = yod.getText().toString().isEmpty() ? 0 : Integer.parseInt(yod.getText().toString());

        height = heightInput.getText().toString().isEmpty() ? 0 : Double.parseDouble(heightInput.getText().toString());
        weight = weightInput.getText().toString().isEmpty() ? 0 : Double.parseDouble(weightInput.getText().toString());
//        height = Double.parseDouble(heightInput.getText().toString());
//        weight = Double.parseDouble(weightInput.getText().toString());

        Log.d(TAG, dateOfBirth+", \n"+dtype+", \n"+gender+", \n"+yearOfDiagnose+", \n"+height+", \n"+weight);

        Map<String, Object> docData = new HashMap<>();
        docData.put("dob",dateOfBirth);
        docData.put("diaType",dtype);
        docData.put("gender",gender);
        docData.put("diaTime",yearOfDiagnose);
        docData.put("height",height);
        docData.put("weight",weight);

        return docData;

    }

    private void userInfoUpdate() {
        userinfoUpdate = (Button) findViewById(R.id.userinfo_button);
        userinfoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> docData = getData();

                String uid = currentUser.getUid();
                Log.d(TAG, uid);

                db.collection(COLLECTION_NAME).document(uid)
                        .update(docData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG,"\nupdated");
                                    startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
                                }
                                else {
                                    Log.d(TAG, task.getException().getMessage());
                                }
                            }
                        });
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }


}
