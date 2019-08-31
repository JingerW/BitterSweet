package com.example.bittersweet;

import android.util.Log;

public class User {

    String username = null;
    String gender = null;
    String dob = null;
    double height = 0;
    double weight = 0;
    String diaType = null;
    int diaTime = 0;
    String pill = null;
    String insulin = null;

    User() {}

    public User(String username, String gender, String dob, double height, double weight,
                String diaType, int diaTime, String pill, String insulin) {
        this.username = username;
        this.gender = gender;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.diaType = diaType;
        this.diaTime = diaTime;
        this.pill = pill;
        this.insulin = insulin;
    }

    public String showUser() {
        return"signupMessage: "+username+"\n"
                +gender+"\n"+dob+"\n"+height+"\n"+weight+
                "\n"+diaTime+"\n"+pill+"\n"+insulin;
    }

    public String getUsername() {
        return username;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public String getDiaType() {
        return diaType;
    }

    public int getDiaTime() {
        return diaTime;
    }

    public String getPill() {
        return pill;
    }

    public String getInsulin() {
        return insulin;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setDiaType(String diaType) {
        this.diaType = diaType;
    }

    public void setDiaTime(int diaTime) {
        this.diaTime = diaTime;
    }

    public void setPill(String pill) {
        this.pill = pill;
    }

    public void setInsulin(String insulin) {
        this.insulin = insulin;
    }
}
