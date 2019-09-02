package com.example.bittersweet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import github.hellocsl.cursorwheel.CursorWheelLayout;

public class AddRecordActivity extends AppCompatActivity {

    private final static String TAG = "AddRecordActivityDebug";

    private EditText bloodGlucoseLevel;
    private RelativeLayout bloodGlucoseBG;
    private TextView inputTime;
    private TimePickerDialog timePicker;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private TextView inputDate;
    private DatePickerDialog datePicker;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        setBloodGlucoseLevel();

        setInputTime();

    }

    private void setBloodGlucoseLevel() {
        bloodGlucoseLevel = (EditText) findViewById(R.id.blood_glucose);
        bloodGlucoseBG = (RelativeLayout) findViewById(R.id.blood_glucose_bg);

        bloodGlucoseLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double input = Double.parseDouble(bloodGlucoseLevel.getText().toString());

                Drawable red_bg = getResources().getDrawable(R.drawable.drop_red);
                Drawable red_bt = getResources().getDrawable(R.drawable.glucose_drop_button_red);
                Drawable green_bg = getResources().getDrawable(R.drawable.drop_green);
                Drawable green_bt = getResources().getDrawable(R.drawable.glucose_drop_button_green);
                Drawable yellow_bg = getResources().getDrawable(R.drawable.drop_yellow);
                Drawable yellow_bt = getResources().getDrawable(R.drawable.glucose_drop_button_yellow);

                if (input <= 4 || input >= 13) {
                    bloodGlucoseLevel.setBackground(red_bt);
                    bloodGlucoseBG.setBackground(red_bg);
                } else if (input <= 7) {
                    bloodGlucoseLevel.setBackground(green_bt);
                    bloodGlucoseBG.setBackground(green_bg);
                } else if (input <= 10) {
                    bloodGlucoseLevel.setBackground(yellow_bt);
                    bloodGlucoseBG.setBackground(yellow_bg);
                }
            }
        });
    }

    private void setInputTime() {
        final Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        final int mintues = c.get(Calendar.MINUTE);

        // date input
        inputDate = (TextView) findViewById(R.id.blood_glucose_date);
        inputDate.setText(day+"/"+month+"/"+year);
        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker = new DatePickerDialog(AddRecordActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener, year, month, day);
                datePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));;
                datePicker.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Log.d(TAG,"DateSet: date: "+i+"/"+i1+"/"+i2);
                inputDate.setText(i2+"/"+i1+"/"+i);
            }
        };

        // time input
        inputTime = (TextView) findViewById(R.id.blood_glucose_time);
        inputTime.setText(hour+":"+mintues);
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
                Log.d(TAG, "Time select: "+i+":"+i1);
                inputTime.setText(i+":"+i1);
            }
        };
    }


}
