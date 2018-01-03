package com.lba.poc.viewholder;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.lba.poc.reveal.R;
import com.lba.poc.widgets.ScratchImageView;
import com.lba.poc.widgets.ShakeDetector;

/**
 * Created by smohanthy on 1/3/18.
 */


public class ShakeViewHolder extends RecyclerView.ViewHolder implements ShakeDetector.OnShakeListener {
    private Context context;
    private View itemView;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private ScratchImageView scratchImageView;

    public ShakeViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        this.itemView = itemView;
    }

    public static ShakeViewHolder getInstance(LayoutInflater inflater, ViewGroup parent, Activity context) {
        return new ShakeViewHolder(inflater.inflate(R.layout.activity_scratch_image, parent, false), context);
    }

    public void bindHolder() {
        // ShakeDetector initialization
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(this);
    }

    @Override
    public void onShake() {
        scratchImageView = itemView.findViewById(R.id.scratch_view);
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(1000);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        scratchImageView.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                scratchImageView.clear();
            }

            @Override
            public void onAnimationEnd(Animation arg0) {

            }
        });
    }

    public void onResume() {
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
    }
}
