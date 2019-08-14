package com.example.bittersweet;

public class UserInfoModal {

    String username = null;
    String gender = null;
    String dob = null;
    double height = 0;
    double weight = 0;
    int diaType = 0;
    int diaTime = 0;
    String pill = null;
    String insulin = null;

    UserInfoModal() {}

    public UserInfoModal(String username, String gender, String dob, double height, double weight,
                         int diaType, int diaTime, String pill, String insulin) {
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

    public int getDiaType() {
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

    public void setDiaType(int diaType) {
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
