package com.myo.buddy.workout;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;

public class Curls extends Workout {
    private boolean lookingForPeak = false;

    public Curls(){

    }

    public Curls(Parcel in) {
        super(in);
        lookingForPeak = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(lookingForPeak ? 1 : 0);
    }

    @Override
    public String getWorkoutName() {
        return "Curls";
    }

    @Override
    public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
        if (Math.abs(timestamp - ts_prev) < ts_interval) {
            return;
        }

        // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
        float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
        float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
        float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));

        // Adjust roll and pitch for the orientation of the Myo on the arm.
        if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
            roll *= -1;
            pitch *= -1;
        }

        if (lookingForPeak && pitch > mPeak) {
            lookingForPeak = false;
            if (mCallback != null) {
                mCallback.increment();
            }
        } else if (!lookingForPeak && pitch < mDip) {
            lookingForPeak = true;
            if (mCallback != null) {
                mCallback.increment();
            }
        }

        pitch = Math.min(mPeak, Math.max(mDip, pitch));
        float percentage = Math.abs( (pitch - mDip) / (mPeak - mDip));

        mCallback.progress(percentage);

        ts_prev = timestamp;
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Curls createFromParcel(Parcel in) {
            return new Curls(in);
        }

        public Curls[] newArray(int size) {
            return new Curls[size];
        }
    };

}
