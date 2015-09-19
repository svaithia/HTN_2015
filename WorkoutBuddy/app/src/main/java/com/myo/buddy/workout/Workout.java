package com.myo.buddy.workout;

import android.os.Parcelable;

public abstract class Workout implements Parcelable {
    public static final String TAG = "workout";
    public abstract void detect();
    public abstract String getWorkoutName();
}
