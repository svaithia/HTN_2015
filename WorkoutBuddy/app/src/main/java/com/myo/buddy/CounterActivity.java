package com.myo.buddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.myo.buddy.workout.Workout;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;

public class CounterActivity extends Activity {
    public static final String TAG = CounterActivity.class.getSimpleName();

    private Workout mWorkout;
    private TextView tvCounter;

    private float mPeak;
    private float mDip;
    private Hub mHub;

    public long ts_prev = Long.MAX_VALUE;

    private float mRepCounter = 0;

    private boolean lookingForPeek = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter_activity);
        tvCounter = (TextView) findViewById(R.id.tvCounter);
        tvCounter.setText("" + mRepCounter);

        mHub = Hub.getInstance();
        if (!mHub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mHub.setLockingPolicy(Hub.LockingPolicy.NONE);
        mHub.addListener(mListener);

        Intent intent = getIntent();
        mWorkout = intent.getParcelableExtra(Workout.TAG);

        mPeak = intent.getFloatExtra(CalibrationActivity.KEY_PEAK, 0);
        mDip = intent.getFloatExtra(CalibrationActivity.KEY_DIP, 0);

        Log.e(TAG, "PEAK: " + mPeak);
        Log.e(TAG, "DIP: " + mDip);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHub.removeListener(mListener);
    }

    private long ts_interval = 100L;

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

            if (lookingForPeek && pitch > mPeak) {
                mRepCounter += 0.5;
                lookingForPeek = false;
                tvCounter.setText("" + mRepCounter);
            } else if (!lookingForPeek && pitch < mDip) {
                lookingForPeek = true;
                mRepCounter += 0.5;
                tvCounter.setText("" + mRepCounter);
            }

            ts_prev = timestamp;
        }
    };

}
