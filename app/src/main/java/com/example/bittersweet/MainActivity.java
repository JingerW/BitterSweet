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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends DrawerActivity {

    private static final String TAG = "MainActivityDebug";
    private static final String COLLECTION_NAME = "User";

    private Button addUserinfo;
    private Button deleteUserinfo;
    private Button updateUserinfo;
    private TextView dob, yod;
    private DatePickerDialog.OnDateSetListener mdateSetListener;
    private RadioGroup genderGroup;
    private Spinner diabetesType;
    private Button userinfoUpdate;
    private String dateOfBirth = null;
    private String dtype = null;
    private String gender = null;
    private int yearOfDiagnose = 0;
    private User user;

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
//
//        selectDoB();
//
//        genderGroup = (RadioGroup) findViewById(R.id.gender_group);
//
//        selectDiabetesType();
//
//        createYearOfDiagnosePicker();
//
//        userInfoUpdate();

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

//    private void updateUserinfo(final Map<String, Object> updateinfo) {
//        updateUserinfo = (Button) findViewById(R.id.update_userinfo);
//        updateUserinfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String uid = currentUser.getUid();
//                Log.d(TAG, uid);
//
//                db.collection(COLLECTION_NAME).document(uid)
//                        .update(updateinfo)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d(TAG,"\nupdated");
//                                }
//                                else {
//                                    Log.d(TAG, task.getException().getMessage());
//                                }
//                            }
//                        });
//            }
//        });
//    }
//
//    private void selectDoB() {
//        dob = (TextView) findViewById(R.id.dob_input);
//        dob.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Calendar c = Calendar.getInstance();
//                int year = c.get(Calendar.YEAR);
//                int month = c.get(Calendar.MONTH);
//                int day = c.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog dateDialog = new DatePickerDialog(MainActivity.this,
//                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mdateSetListener, year, month, day);
//                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dateDialog.show();
//            }
//        });
//
//        mdateSetListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//                // i = year, i1 = month, i2 = day
//                Log.d(TAG, "DateSet: date: "+i+"/"+i1+"/"+i2);
//                dob.setText(i2+"/"+i1+"/"+i);
//            }
//        };
//    }
//
//    private void selectDiabetesType() {
//        diabetesType = (Spinner) findViewById(R.id.diabetes_type_dropdown);
//        ArrayAdapter<CharSequence> adaptor = ArrayAdapter.createFromResource(this, R.array.diabetes_type, android.R.layout.simple_spinner_item);
//        adaptor.setDropDownViewResource(R.layout.spinner_view);
//        diabetesType.setAdapter(adaptor);
//        diabetesType.setSelection(0);
////        diabetesType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
////            @Override
////            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
////                dtype = adapterView.getItemAtPosition(i).toString();
////            }
////
////            @Override
////            public void onNothingSelected(AdapterView<?> adapterView) {
////            }
////        });
//    }
//
//    private void createYearOfDiagnosePicker() {
//
//        yod = (TextView) findViewById(R.id.date_of_diag_input);
//        yod.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openYearPicker();
//            }
//        });
//    }
//
//    private void openYearPicker() {
//        YearPickerDialog yearPickerDialog = new YearPickerDialog();
//        yearPickerDialog.show(getSupportFragmentManager(), "year picker");
//    }
//
//    @Override
//    public void applyText(int year) {
//        yod.setText(Integer.toString(year));
//    }
//
//    private void userInfoUpdate() {
//        userinfoUpdate = (Button) findViewById(R.id.userinfo_button);
//        userinfoUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dateOfBirth = dob.getText().toString();
//                dtype = diabetesType.getSelectedItem().toString();
////                RadioButton selectedButton = (RadioButton) diabetesType.getChildAt(diabetesType.getSelectedItemPosition());
////                String gender = selectedButton.getText().toString();
//                int selectedButtonId = genderGroup.getCheckedRadioButtonId();
//                RadioButton selectedButton = (RadioButton) genderGroup.findViewById(selectedButtonId);
//                gender = selectedButton.getText().toString();
//                yearOfDiagnose = Integer.parseInt(yod.getText().toString());
//
//                Log.d(TAG, dateOfBirth+", \n"+dtype+", \n"+gender+", \n"+yearOfDiagnose);
//
//                Map<String, Object> docData = new HashMap<>();
//                docData.put("dob",dateOfBirth);
//                docData.put("diaType",dtype);
//                docData.put("gender",gender);
//                docData.put("diaTime",yearOfDiagnose);
//
//                String uid = currentUser.getUid();
//                Log.d(TAG, uid);
//
//                db.collection(COLLECTION_NAME).document(uid)
//                        .update(docData)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d(TAG,"\nupdated");
//                                }
//                                else {
//                                    Log.d(TAG, task.getException().getMessage());
//                                }
//                            }
//                        });
//            }
//        });
//    }

}