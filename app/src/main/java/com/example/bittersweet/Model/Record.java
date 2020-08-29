package com.example.bittersweet.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Record implements Comparable<Record>{

    @NonNull
    private static final String TAG = "BloodGlucoseModel";
    private double bloodGlucose;
    private String date, time;
    private ArrayList<String> labels     = new ArrayList<>();
    private Ketone ketone;
    private Medication medication;
    private BpHr bpHr;
    private Exercise exercise;
    private String food;
    private String notes;

    public Record() {
    }

    public Record(double bloodGlucose, String date, String time, ArrayList<String> labels, Ketone ketone, Medication medication, BpHr bpHr, Exercise exercise, String food, String notes) {
        this.bloodGlucose = bloodGlucose;
        this.date = date;
        this.time = time;
        this.labels = labels;
        this.ketone = ketone;
        this.medication = medication;
        this.bpHr = bpHr;
        this.exercise = exercise;
        this.food = food;
        this.notes = notes;
    }

    public Record(double bloodGlucose, String date, String time, ArrayList<String> labels, String notes) {
        this.bloodGlucose = bloodGlucose;
        this.date = date;
        this.time = time;
        this.labels = labels;
        this.notes = notes;
    }

    public double getBloodGlucose() {
        return bloodGlucose;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public ArrayList<String> getLabels() { return labels; }
    public Ketone getKetone() {
        return ketone;
    }
    public Medication getMedication() {
        return medication;
    }
    public BpHr getBpHr() {
        return bpHr;
    }
    public Exercise getActivity() {
        return exercise;
    }
    public String getFood() { return food; }
    public String getInputNotes() {
        return notes;
    }

    public void setBloodGlucose(double bloodGlucose) { this.bloodGlucose = bloodGlucose; }
    public void setDate(String date) {
        this.date = date;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }
    public void setKetone(Ketone ketone) { this.ketone = ketone; }
    public void setMedication(Medication medication) { this.medication = medication; }
    public void setBpHr(BpHr bpHr) { this.bpHr = bpHr; }
    public void setAcivity(Exercise exercise) { this.exercise = exercise; }
    public void setFood(String food) { this.food = food; }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean checkInput() {
        if (bloodGlucose == 0 || date == null || time == null) {
            return false;
        }
        else {return true;}
    }

    public void showBloodGlucose(String TAG) {
        Log.d(TAG, bloodGlucose+"\n"+date+"  , "+time+"\n"+labels.size()+"\n"+notes);
    }

    // convert String Date into Date Date and then sort by date within an arraylist
    @Override
    public int compareTo(Record bloodGlucose) {
        int result = 0;
        // check null exception
        if (getDate() != null && bloodGlucose.getDate() != null) {
            DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.UK);
            try{ // parse both date into Date format for comparison
                Date date1 = dateFormat.parse(getDate());
                Date date2 = dateFormat.parse(bloodGlucose.getDate());
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
