package com.example.bittersweet.Model;

/**
 * BpHr Class
 * create BpHr model which contains blood pressure and heart rate
 *
 * @author JingJing
 */
public class BpHr {

    private int upperBp;
    private int lowerBp;
    private int heartRate;

    public BpHr() {
    }

    public BpHr(int upperBp, int lowerBp, int heartRate) {
        this.upperBp = upperBp;
        this.lowerBp = lowerBp;
        this.heartRate = heartRate;
    }

    public BpHr(int upperBp, int lowerBp) {
        this.upperBp = upperBp;
        this.lowerBp = lowerBp;
    }

    public BpHr(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getUpperBp() {
        return upperBp;
    }

    public void setUpperBp(int upperBp) {
        this.upperBp = upperBp;
    }

    public int getLowerBp() {
        return lowerBp;
    }

    public void setLowerBp(int lowerBp) {
        this.lowerBp = lowerBp;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }
}
