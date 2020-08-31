package com.example.bittersweet.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;

public class Record {

    @NonNull
    private static final String TAG = "BloodGlucoseModel";
    private double record;
    private String date, time;
    private Date dateTime;
    private Ketone ketone;
    private Medication medication;
    private BpHr bpHr;
    private Exercise exercise;
    private Food food;
    private String notes;

    private String documentID;

    public Record() {
    }

    public Record(double record, String date, String time, Date dateTime, Ketone ketone, Medication medication, BpHr bpHr, Exercise exercise, Food food, String notes) {
        this.record = record;
        this.date = date;
        this.time = time;
        this.dateTime = dateTime;
        this.ketone = ketone;
        this.medication = medication;
        this.bpHr = bpHr;
        this.exercise = exercise;
        this.food = food;
        this.notes = notes;
    }

    public Record(Medication medication) {
        this.medication = medication;
    }

    public Record(BpHr bpHr) {
        this.bpHr = bpHr;
    }

    public Record(Exercise exercise) {
        this.exercise = exercise;
    }

    public Record(Food food) {
        this.food = food;
    }

    public double getBloodGlucose() {
        return record;
    }

    public void setBloodGlucose(double record) {
        this.record = record;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Ketone getKetone() {
        return ketone;
    }

    public void setKetone(Ketone ketone) {
        this.ketone = ketone;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    public BpHr getBpHr() {
        return bpHr;
    }

    public void setBpHr(BpHr bpHr) {
        this.bpHr = bpHr;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public String getNotes() {
        return notes;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public boolean checkInput() {
        if (record == 0 || date == null || time == null) {
            return false;
        } else {
            return true;
        }
    }

    public void showBloodGlucose(String TAG) {
        Log.d(TAG, record + "\n" + date + "  , " + time);
    }

}
