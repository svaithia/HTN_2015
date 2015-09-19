/*
 * Copyright (C) 2014 Thalmic Labs Inc.
 * Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
 */

package com.thalmic.android.sample.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

import java.util.ArrayList;
import java.util.List;

public class HelloWorldActivity extends Activity {

    private TextView mLockStateView;
    private TextView mTextView;
    List<Datapoint> data_list = new ArrayList<Datapoint>();
    List<Float> peak_pitch = new ArrayList<Float>();
    List<Float> dip_pitch = new ArrayList<Float>();

    long ts_interval = 100;
    int cal_num_reps = 0;
    long ts_begin = 0;
    long ts_prev = 0;

    float p_curr = 0;
    float p_prev = 0;
    float p_prevprev = 0;

    long ts_count = 0;
    boolean listdata = true;

    final static double[] dlist = { 18.524588, 19.580612, 16.52172, 11.724332, 3.2621462, -9.4709215, -26.468754, -41.036163, -50.80451, -50.98596, -42.90037, -26.978441, -9.855615, 3.772213, 11.313177, 3.9716103, -14.144719, 0.014285767, 6.570724, 3.438286, -9.297457, -24.61491, -44.020687, -53.023544, -54.29259, -51.721676, -43.92574, -36.236057, -25.067955, -8.515062, 2.1587827, 2.171175, -7.3301344, -24.340292, -36.819004, -46.151527, -49.122375, -46.875614, -36.59842, -25.150541, -8.381048,  3.39345, 10.194889, 10.559715, -11.624424, -27.953388, -43.099766, -52.23424, -53.29581, -49.35047, -36.064533, -20.837297, -4.5963764, 3.8120506, 6.553082, -0.0438216, -16.449749, -33.432564, -50.47874, -54.630085, -53.260944, -40.72143, -25.989126, -12.234187, -5.503456,  -5.3449, -18.764069, -31.827797, -39.053196, -54.333138, -55.142765, -53.77716, -50.84636, -40.912357 };




    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {

        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myo, long timestamp) {
            // Set the text color of the text view to cyan when a Myo connects.
            mTextView.setTextColor(Color.CYAN);
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            // Set the text color of the text view to red when a Myo disconnects.
            mTextView.setTextColor(Color.RED);
        }

        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            mTextView.setText(myo.getArm() == Arm.LEFT ? R.string.arm_left : R.string.arm_right);
        }

        // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
        // when Myo is moved around on the arm.
        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
            mTextView.setText(R.string.hello_world);
        }

        // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
        // policy, that means poses will now be delivered to the listener.
        @Override
        public void onUnlock(Myo myo, long timestamp) {
            mLockStateView.setText(R.string.unlocked);
        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp) {
            mLockStateView.setText(R.string.locked);
        }

        // onOrientationData() is called whenever a Myo provides its current orientation,
        // represented as a quaternion.
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
            float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
            float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
            float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));

            // Adjust roll and pitch for the orientation of the Myo on the arm.
            if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
                roll *= -1;
                pitch *= -1;
            }

            // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
            mTextView.setRotation(roll);
            mTextView.setRotationX(pitch);
            mTextView.setRotationY(yaw);

            //Datapoint dp = new Datapoint(roll, pitch, yaw);
            //data_list.add(dp);
            ts_count++;

            if(ts_count == 1){
                ts_begin = timestamp;
                Log.d("","TIMESTAMP INITIAL: " + ts_begin);
            }



            if(timestamp - ts_begin <= 10000){
                if(timestamp - ts_prev > ts_interval){

                    Log.d("","TIMESTAMP: " + timestamp);
                    //Log.d("Data   ","ROLL: " + String.valueOf(roll));// + " PITCH: " + String.valueOf(pitch) + " YAW: " + String.valueOf(yaw));
                    //Log.d("", String.valueOf(timestamp));
                    //Log.d("", "ROLL: " + String.valueOf(roll) + "    PITCH: " + String.valueOf(pitch) + "    YAW: " + String.valueOf(yaw));
                    //Log.d("Data   ","YAW: " + String.valueOf(roll));
                    Datapoint dp = new Datapoint(timestamp,roll, pitch, yaw);
                    //Log.d("", "ROLL: " + String.valueOf(roll) + "    PITCH: " + String.valueOf(pitch) + "    YAW: " + String.valueOf(yaw));
                    data_list.add(dp);



                    //if(pitch >= p_prev && p_prev >= p_prevprev){
                        //keep going
                    /*if (pitch < p_prev && p_prev >= p_prevprev) {
                        peak_pitch.add(pitch);
                        Log.d("adding peak", String.valueOf(pitch));
                    }
                    else if (pitch > p_prev && p_prev <= p_prevprev){
                        dip_pitch.add(pitch);
                        Log.d("adding dip", String.valueOf(pitch));
                    }


                    p_prevprev = p_prev;
                    p_prev = pitch;
                    */


                    ts_prev = timestamp;

                }

            }else if(timestamp - ts_begin > 10000){

                if(listdata){
                    Log.d("SIZE of data list:   ", String.valueOf(data_list.size()));
                    for (int i=0; i < data_list.size(); i++){

                        Log.d("", "TIMESTAMP:  " + String.valueOf(data_list.get(i).getTimestamp()) + "ROLL: " + String.valueOf(data_list.get(i).getRoll()) + "    PITCH: " + String.valueOf(data_list.get(i).getPitch()) + "    YAW: " + String.valueOf(data_list.get(i).getYaw()));
                    }

                    for (int i=0; i < peak_pitch.size(); i++){
                        Log.d("PEAK   ", String.valueOf(peak_pitch.get(i)));
                    }

                    for (int i=0; i < dip_pitch.size(); i++){
                        Log.d("DIP   ", String.valueOf(dip_pitch.get(i)));
                    }
                    listdata = false;

                    p_prevprev = (float) dlist[0];
                    p_prev = (float) dlist[1];

                    for(int i=2; i < dlist.length; i++){
                        pitch = (float) dlist[i];

                        if (pitch < p_prev && p_prev >= p_prevprev) {
                            peak_pitch.add(pitch);
                            Log.d("adding peak", String.valueOf(pitch));
                        }
                        else if (pitch > p_prev && p_prev <= p_prevprev){
                            dip_pitch.add(pitch);
                            Log.d("adding dip", String.valueOf(pitch));
                        }

                        p_prevprev = p_prev;
                        p_prev = pitch;
                    }





                }

            }
            //Log.d("Data   ","ROLL: " + String.valueOf(roll));// + " PITCH: " + String.valueOf(pitch) + " YAW: " + String.valueOf(yaw));
            //Log.d("", String.valueOf(timestamp));
            //Log.d("", "ROLL: " + String.valueOf(roll) + "    PITCH: " + String.valueOf(pitch) + "    YAW: " + String.valueOf(yaw));
            //Log.d("Data   ","YAW: " + String.valueOf(roll));



        }

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we rec qeive.
            switch (pose) {
                case UNKNOWN:
                    mTextView.setText(getString(R.string.hello_world));
                    break;
                case REST:
                case DOUBLE_TAP:
                    int restTextId = R.string.hello_world;
                    switch (myo.getArm()) {
                        case LEFT:
                            restTextId = R.string.arm_left;
                            break;
                        case RIGHT:
                            restTextId = R.string.arm_right;
                            break;
                    }
                    mTextView.setText(getString(restTextId));
                    break;
                case FIST:
                    mTextView.setText(getString(R.string.pose_fist));
                    break;
                case WAVE_IN:
                    mTextView.setText(getString(R.string.pose_wavein));
                    break;
                case WAVE_OUT:
                    mTextView.setText(getString(R.string.pose_waveout));
                    break;
                case FINGERS_SPREAD:
                    mTextView.setText(getString(R.string.pose_fingersspread));
                    break;
            }

            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
                // hold the poses without the Myo becoming locked.
                myo.unlock(Myo.UnlockType.HOLD);

                // Notify the Myo that the pose has resulted in an action, in this case changing
                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            } else {
                // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
                // stay unlocked while poses are being performed, but lock after inactivity.
                myo.unlock(Myo.UnlockType.TIMED);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        mLockStateView = (TextView) findViewById(R.id.lock_state);
        mTextView = (TextView) findViewById(R.id.text);

        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }
}
