package com.example.bittersweet.Helper;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberInputFilter implements InputFilter {

    private int min, max;
    private Boolean decimalPlace = false;
    private Pattern mPattern;

    public NumberInputFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public NumberInputFilter(int min, int max, int digitsBeforeZero, int digitsAfterZero, Boolean decimalPlace) {
        this.min = min;
        this.max = max;
        this.mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        this.decimalPlace = decimalPlace;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        if (decimalPlace) {
            Matcher matcher=mPattern.matcher(dest);
            try {

                double input = Double.parseDouble(dest.toString() + source.toString());
                if (isInRangeDouble(((double) min), ((double) max), input) && matcher.matches())
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        } else {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

    private boolean isInRangeDouble(double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
