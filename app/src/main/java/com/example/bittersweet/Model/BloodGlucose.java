package com.example.bittersweet.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BloodGlucose implements Comparable<BloodGlucose>{

    @NonNull
    private static final String TAG = "BloodGlucoseModel";
    private double bloodGlucose = 0;
    private String inputDate = null, inputTime = null;
    private ArrayList<String> inputLabels = new ArrayList<String>();
    private String inputNotes = null;

    public BloodGlucose() {
    }

    public BloodGlucose(double bloodGlucose, String inputDate, String inputTime, ArrayList<String> inputLabels, String inputNotes) {
        this.bloodGlucose = bloodGlucose;
        this.inputDate = inputDate;
        this.inputTime = inputTime;
        this.inputLabels = inputLabels;
        this.inputNotes = inputNotes;
    }

    public double getBloodGlucose() {
        return bloodGlucose;
    }
    public String getInputDate() {
        return inputDate;
    }
    public String getInputTime() {
        return inputTime;
    }
    public ArrayList<String> getInputLabels() {
        return inputLabels;
    }
    public String getInputNotes() {
        return inputNotes;
    }

    public void setBloodGlucose(double bloodGlucose) {
        this.bloodGlucose = bloodGlucose;
    }
    public void setInputDate(String inputDate) {
        this.inputDate = inputDate;
    }
    public void setInputTime(String inputTime) {
        this.inputTime = inputTime;
    }
    public void setInputLabels(ArrayList<String> inputLabels) {
        this.inputLabels = inputLabels;
    }
    public void setInputNotes(String inputNotes) {
        this.inputNotes = inputNotes;
    }


    public boolean checkInput() {
        if (bloodGlucose == 0 || inputDate == null || inputTime == null) {
            return false;
        }
        else {return true;}
    }

    public void showBloodGlucose(String TAG) {
        Log.d(TAG, bloodGlucose+"\n"+inputDate+"  , "+inputTime+"\n"+inputLabels.size()+"\n"+inputNotes);
    }

    @Override
    public int compareTo(BloodGlucose bloodGlucose) {
        int result = 0;
        // check null exception
        if (getInputDate() != null && bloodGlucose.getInputDate() != null) {
            DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.UK);
            try{ // parse both date into Date format for comparison
                Date date1 = dateFormat.parse(getInputDate());
                Date date2 = dateFormat.parse(bloodGlucose.getInputDate());
                result = date1.compareTo(date2);
            } catch (ParseException pe) {
                pe.printStackTrace();
                Log.d(TAG,"input dates are null");
            }
            return result;
        }
        else {
            Log.d(TAG,"input dates are null");
            return result;
        }
    }
}
