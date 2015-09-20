package com.myo.buddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.myo.buddy.workout.Workout;
import com.myo.buddy.workout.Datapoint;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalibrationActivity extends Activity implements View.OnClickListener{
    private static final String TAG = CalibrationActivity.class.getSimpleName();

    public static final String KEY_PEAK = "MEDIAN_PEAK";
    public static final String KEY_DIP = "MEDIAN_DIP";

    private Workout mWorkout;
    private Button bStartStop;
    private boolean mCalStarted;

    private List<Float> peak_pitch = new ArrayList<>();
    private List<Float> dip_pitch = new ArrayList<>();
    private Hub mHub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibration_activity);
        Intent intent = getIntent();
        mWorkout = intent.getParcelableExtra(Workout.TAG);
        bStartStop = (Button) findViewById(R.id.bStartStop);
        bStartStop.setOnClickListener(this);

        mHub = Hub.getInstance();
        if (!mHub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mHub.setLockingPolicy(Hub.LockingPolicy.NONE);
        mHub.addListener(mListener);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bStartStop: {
                startStopLogic();
            } break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHub.removeListener(mListener);
    }

    private List<Datapoint> mDataList;
    private float p_prevprev;
    private float   p_prev;
    private long ts_prev;

    private void startStopLogic() {
        boolean notStarted = bStartStop.getText().equals("Start");

        if (notStarted) {
            // do calibration logic here
            mCalStarted = true;
            bStartStop.setText("Stop");

            mDataList = new ArrayList<>();
            p_prevprev = Float.MAX_VALUE;
            p_prev = Float.MAX_VALUE;
            ts_prev = Long.MAX_VALUE;

        } else {
            // TODO stop logic
            mCalStarted = false;

            if (peak_pitch.size() > 0 || dip_pitch.size() > 0) {
                peak_pitch.remove(0);
                peak_pitch.remove(peak_pitch.size() - 1);

                dip_pitch.remove(0);
                dip_pitch.remove(peak_pitch.size() - 1);

                for (int i = 0; i < peak_pitch.size(); i++) {
                    Log.e(TAG, "" + peak_pitch.get(i));
                }

                for (int i = 0; i < dip_pitch.size(); i++) {
                    Log.e(TAG, "" + (dip_pitch.get(i)));
                }

                Collections.sort(peak_pitch);
                Collections.sort(dip_pitch);

                float medianOfPeak = peak_pitch.get(peak_pitch.size()/2);
                float medianOfDip = dip_pitch.get(dip_pitch.size()/2);

                Intent intent = new Intent(CalibrationActivity.this, CounterActivity.class);
                intent.putExtra(Workout.TAG, mWorkout);
                intent.putExtra(KEY_PEAK, medianOfPeak);
                intent.putExtra(KEY_DIP, medianOfDip);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please try again", Toast.LENGTH_LONG).show();
            }
            bStartStop.setText("Start");
        }
    }

    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {

        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myo, long timestamp) {
            // Set the text color of the text view to cyan when a Myo connects.
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            // Set the text color of the text view to red when a Myo disconnects.
        }

        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
        }
        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
        }

        @Override
        public void onUnlock(Myo myo, long timestamp) { }

        @Override
        public void onLock(Myo myo, long timestamp) { }

        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) { }

        private long ts_interval = 100L;

        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            if (!mCalStarted || Math.abs(timestamp - ts_prev) < ts_interval) {
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

            Datapoint dp = new Datapoint(timestamp,roll, pitch, yaw);
            mDataList.add(dp);
            Log.e(TAG + "WTF", "" + pitch);

            if (p_prevprev == Float.MAX_VALUE) {
                p_prevprev = pitch;
                return;
            }

            if (p_prev == Float.MAX_VALUE) {
                p_prev = pitch;
                return;
            }

            if (pitch < p_prev && p_prev >= p_prevprev) {
                peak_pitch.add(p_prev);
                Log.e(TAG, "adding peak" + String.valueOf(p_prev));
            }
            else if (pitch > p_prev && p_prev <= p_prevprev){
                dip_pitch.add(p_prev);
                Log.e(TAG, "adding dip" + String.valueOf(p_prev));
            }

            p_prevprev = p_prev;
            p_prev = pitch;
            ts_prev = timestamp;
        }
    };
}
