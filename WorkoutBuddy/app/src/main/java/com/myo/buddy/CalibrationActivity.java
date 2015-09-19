package com.myo.buddy;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.myo.buddy.workout.Workout;

public class CalibrationActivity extends MyoActivity implements View.OnClickListener{
    private static final String TAG = CalibrationActivity.class.getSimpleName();
    private Workout mWorkout;
    private Button bStartStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibration_activity);
        Intent intent = getIntent();
        mWorkout = intent.getParcelableExtra(Workout.TAG);
        bStartStop = (Button) findViewById(R.id.bStartStop);
        bStartStop.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bStartStop: {
                startStopLogic();
            } break;

        }
    }

    private void startStopLogic() {
        boolean notStarted = bStartStop.getText().equals("Start");

        if (notStarted) {
            // do calibration logic here
            bStartStop.setText("Stop");
        } else {
            // TODO stop logic
            Intent intent = new Intent(CalibrationActivity.this, CounterActivity.class);
            intent.putExtra(Workout.TAG, mWorkout);
            intent.putExtra("DATA", "DATA");
            startActivity(intent);
            bStartStop.setText("Start");
        }
    }
}
