package com.example.bittersweet.Model;

/**
 * Medication Class
 * create Medication model which contains insulin usage, insulin type and note for other medication
 *
 * @author JingJing
 */
public class Medication {

    private int insulin;
    private String insulinType;
    private String otherMeds;

    public Medication() {
    }

    public Medication(int insulin, String insulinType, String otherMeds) {
        this.insulin = insulin;
        this.insulinType = insulinType;
        this.otherMeds = otherMeds;
    }

    public Medication(String otherMeds) {
        this.otherMeds = otherMeds;
    }

    public int getInsulin() {
        return insulin;
    }

    public void setInsulin(int insulin) {
        this.insulin = insulin;
    }

    public String getInsulinType() {
        return insulinType;
    }

    public void setInsulinType(String insulinType) {
        this.insulinType = insulinType;
    }

    public String getOtherMeds() {
        return otherMeds;
    }

    public void setOtherMeds(String otherMeds) {
        this.otherMeds = otherMeds;
    }

    public boolean isEmpty(){
        if (getInsulin() == 0 && getInsulinType() == null && getOtherMeds() == null) {
            return true;
        }else {return false;}
    }
}
