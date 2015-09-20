package com.myo.buddy.workout;

/**
 * Created by Abhijit on 9/19/2015.
 */
public class Datapoint {
    private float _roll, _pitch, _yaw;
    private long _timestamp;

    public Datapoint(long timestamp, float roll, float pitch, float yaw){
        _roll = roll;
        _pitch = pitch;
        _yaw = yaw;
        _timestamp = timestamp;
    }

    public float getRoll(){
        return _roll;
    }

    public float getPitch(){
        return _pitch;
    }

    public float getYaw(){
        return _yaw;
    }

    public float getTimestamp(){
        return _timestamp;
    }



    public void set(long timestamp, float roll, float pitch, float yaw){
        _timestamp = timestamp;
        _roll = roll;
        _pitch = pitch;
        _yaw = yaw;
    }
}
