package com.example.bittersweet.Model;

/**
 * Ketone Class
 * create ketone model which contains ketone State and ketone Number
 *
 * @author JingJing
 */
public class Ketone {

    private boolean ketoneState;
    private double ketoneNumber;

    // if no param, set ketone state to false
    public Ketone() {
        ketoneState = false;
    }

    // if pass ketone number, set ketone state to true
    public Ketone(double ketoneNumber) {
        this.ketoneState = true;
        this.ketoneNumber = ketoneNumber;
    }

    public boolean getKetoneState() {
        return ketoneState;
    }

    public void setKetoneState(Boolean ketoneState) {
        this.ketoneState = ketoneState;
    }

    public double getKetoneNumber() {
        return ketoneNumber;
    }

    public void setKetoneNumber(double ketoneNumber) {
        this.ketoneNumber = ketoneNumber;
        // set ketone state to true when ketone number is set
        this.ketoneState = true;
    }
}
