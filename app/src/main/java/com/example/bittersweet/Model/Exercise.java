package com.example.bittersweet.Model;

/**
 * Exercise Class
 * create Exercise model which contains exercise type, exercise time (minute and hour) and note for
 * other exercises
 *
 * @author JingJing
 */
public class Exercise {

    private String exerciseType;
    private int exerciseTimeM;
    private int exerciseTimeH;
    private String otherEx;

    public Exercise() {
    }

    public Exercise(String exerciseType, int exerciseTimeM, int exerciseTimeH, String otherEx) {
        this.exerciseType = exerciseType;
        this.exerciseTimeM = exerciseTimeM;
        this.exerciseTimeH = exerciseTimeH;
        this.otherEx = otherEx;
    }


    public Exercise(String exerciseType, String otherEx) {
        this.exerciseType = exerciseType;
        this.otherEx = otherEx;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public int getExerciseTimeM() {
        return exerciseTimeM;
    }

    public void setExerciseTimeM(int exerciseTimeM) {
        this.exerciseTimeM = exerciseTimeM;
    }

    public int getExerciseTimeH() {
        return exerciseTimeH;
    }

    public void setExerciseTimeH(int exerciseTimeH) {
        this.exerciseTimeH = exerciseTimeH;
    }

    public String getOtherEx() {
        return otherEx;
    }

    public void setOtherEx(String otherEx) {
        this.otherEx = otherEx;
    }
}
