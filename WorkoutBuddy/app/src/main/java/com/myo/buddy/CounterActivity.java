package com.myo.buddy;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.myo.buddy.model.Workout;

public class CounterActivity extends Activity {
    private final Workout mWorkout;

    public CounterActivity(Workout workout) {
        super();
        mWorkout = workout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);


    }
}
