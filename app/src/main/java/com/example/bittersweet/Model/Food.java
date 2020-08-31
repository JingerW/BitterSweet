package com.example.bittersweet.Model;

public class Food {

    private String mealTag;
    private String mealTagTime;
    private String note;

    public Food() {
    }

    public Food(String mealTag, String mealTagTime, String note) {
        this.mealTag = mealTag;
        this.mealTagTime = mealTagTime;
        this.note = note;
    }

    public String getMealTag() {
        return mealTag;
    }

    public void setMealTag(String mealTag) {
        this.mealTag = mealTag;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMealTagTime() {
        return mealTagTime;
    }

    public void setMealTagTime(String mealTagTime) {
        this.mealTagTime = mealTagTime;
    }
}
