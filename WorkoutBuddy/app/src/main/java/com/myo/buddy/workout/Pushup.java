package com.myo.buddy.workout;

import android.os.Parcel;
import android.os.Parcelable;

public class Pushup extends Workout {

    public Pushup(Parcel in) {

    }

    public Pushup() {

    }

    @Override
    public void detect() {

    }

    @Override
    public String getWorkoutName() {
        return "Push-up";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Pushup createFromParcel(Parcel in) {
            return new Pushup(in);
        }

        public Pushup[] newArray(int size) {
            return new Pushup[size];
        }
    };

}
