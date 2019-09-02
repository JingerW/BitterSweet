package com.example.bittersweet.Model;

import android.util.Log;

import java.util.ArrayList;

public class BloodGlucose {

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

    public void showBloodGlucose(String TAG) {
        Log.d(TAG, bloodGlucose+"\n"+inputDate+"  , "+inputTime+"\n"+inputLabels.size()+"\n"+inputNotes);
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
}
