package com.myo.buddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.myo.buddy.workout.Workout;

public class CounterActivity extends MyoActivity {
    private Workout mWorkout;
    private TextView tvCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter_activity);
        Intent intent = getIntent();
        mWorkout = intent.getParcelableExtra(Workout.TAG);

        tvCounter = (TextView) findViewById(R.id.tvCounter);
    }




}
