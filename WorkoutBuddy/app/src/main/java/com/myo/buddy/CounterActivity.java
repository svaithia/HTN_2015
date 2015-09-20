package com.myo.buddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.myo.buddy.view.CounterFrameLayout;
import com.myo.buddy.workout.Workout;
import com.thalmic.myo.Hub;

import java.util.Locale;

public class CounterActivity extends Activity implements Workout.WorkoutCallback {
    public static final String TAG = CounterActivity.class.getSimpleName();

    private Workout mWorkout;
    private TextView tvCounter;

    private Hub mHub;

    private float mRepCounter = 0;
    private CounterFrameLayout cflCounter;
    private TextToSpeech mTextToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter_activity);
        tvCounter = (TextView) findViewById(R.id.tvCounter);
        cflCounter = (CounterFrameLayout) findViewById(R.id.cflCounter);
        tvCounter.setText("" + mRepCounter);

        mHub = Hub.getInstance();
        if (!mHub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mTextToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) { }
            }
        );
        mTextToSpeech.setLanguage(Locale.US);

        Intent intent = getIntent();
        mWorkout = intent.getParcelableExtra(Workout.TAG);

        mHub.setLockingPolicy(Hub.LockingPolicy.NONE);
        mHub.addListener(mWorkout);

        float peak = intent.getFloatExtra(CalibrationActivity.KEY_PEAK, 0);
        float dip = intent.getFloatExtra(CalibrationActivity.KEY_DIP, 0);
        mWorkout.setRange(dip, peak);
        mWorkout.setCallback(this);

        Log.e(TAG, "PEAK: " + dip);
        Log.e(TAG, "DIP: " + peak);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHub.removeListener(mWorkout);
    }


    @Override
    public void increment() {
        mRepCounter += 0.5;
        tvCounter.setText("" + mRepCounter);

        if (mRepCounter%1 == 0) {
            mTextToSpeech.speak("" + (int) mRepCounter, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void progress(float progress) {
        cflCounter.setProgress(progress);
    }
}
