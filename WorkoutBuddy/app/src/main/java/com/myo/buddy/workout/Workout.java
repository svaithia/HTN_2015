package com.myo.buddy.workout;

import android.os.Parcel;
import android.os.Parcelable;

import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;

public abstract class Workout implements Parcelable, DeviceListener {
    public static final String TAG = "workout";

    public interface WorkoutCallback {
        void increment();
        void progress(float progress);
    }

    protected WorkoutCallback mCallback = null;

    public void setCallback(WorkoutCallback workoutCallback) {
        mCallback = workoutCallback;
    }

    protected long ts_interval = 100L;
    protected float mDip;
    protected float mPeak;
    protected long ts_prev = Long.MAX_VALUE;
    protected boolean lookingForPeak = false;


    public void setRange(float dip, float peak) {
        mDip = dip;
        mPeak = peak;
    }

    public Workout(Parcel in) {
        ts_interval = in.readLong();
        mDip = in.readFloat();
        mPeak = in.readFloat();
        ts_prev = in.readLong();
        lookingForPeak = in.readInt() == 1;
    }

    public Workout() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(ts_interval);
        parcel.writeFloat(mDip);
        parcel.writeFloat(mPeak);
        parcel.writeLong(ts_prev);
        parcel.writeInt(lookingForPeak ? 1 : 0);
    }


    @Override
    public void onAttach(Myo myo, long l) {

    }

    @Override
    public void onDetach(Myo myo, long l) {

    }

    @Override
    public void onConnect(Myo myo, long l) {

    }

    @Override
    public void onDisconnect(Myo myo, long l) {

    }

    @Override
    public void onArmSync(Myo myo, long l, Arm arm, XDirection xDirection) {

    }

    @Override
    public void onArmUnsync(Myo myo, long l) {

    }

    @Override
    public void onUnlock(Myo myo, long l) {

    }

    @Override
    public void onLock(Myo myo, long l) {

    }

    @Override
    public void onPose(Myo myo, long l, Pose pose) {

    }

    @Override
    public void onOrientationData(Myo myo, long l, Quaternion quaternion) {

    }

    @Override
    public void onAccelerometerData(Myo myo, long l, Vector3 vector3) {

    }

    @Override
    public void onGyroscopeData(Myo myo, long l, Vector3 vector3) {

    }

    @Override
    public void onRssi(Myo myo, long l, int i) {

    }

    public abstract String getWorkoutName();

}
