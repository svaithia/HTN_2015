package com.myo.buddy.workout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.myo.buddy.R;

public class WorkoutAdapter extends BaseAdapter{
    private final Context mContext;
    private Workout[] mWorkouts;

    public WorkoutAdapter(Context context, Workout[] workouts) {
        mContext = context;
        mWorkouts = workouts;
    }

    @Override
    public int getCount() {
        return mWorkouts.length;
    }

    @Override
    public Object getItem(int i) {
        return mWorkouts[i];
    }

    @Override
    public long getItemId(int i) {
        return mWorkouts[i].hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_workout, null);
        }

        final Workout workout = mWorkouts[i];

        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText(workout.getWorkoutName());

        return view;
    }

}
