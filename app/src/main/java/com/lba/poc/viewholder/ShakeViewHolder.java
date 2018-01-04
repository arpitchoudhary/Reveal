package com.lba.poc.viewholder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lba.poc.reveal.R;
import com.lba.poc.widgets.ShakeDetector;

/**
 * Created by smohanthy on 1/3/18.
 */


public class ShakeViewHolder extends RecyclerView.ViewHolder implements ShakeDetector.OnShakeListener {
    private Context context;
    private View itemView;
    private boolean alreadyShaked;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private ImageView shakeImageView;

    public ShakeViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        this.itemView = itemView;
    }

    public static ShakeViewHolder getInstance(LayoutInflater inflater, ViewGroup parent, Activity context) {
        return new ShakeViewHolder(inflater.inflate(R.layout.model_shake_image, parent, false), context);
    }

    public void bindHolder() {
        shakeImageView = itemView.findViewById(R.id.scratch_view);
        // ShakeDetector initialization
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(this);

        AsyncTask<Void, Void, BitmapDrawable> asyncTask = new AsyncTask<Void, Void, BitmapDrawable>() {
            @Override
            protected BitmapDrawable doInBackground(Void... voids) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Bitmap scratchBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.marble);
                // scratchBitmap = getResizedBitmap(scratchBitmap, scratchImageView.getWidth(), scratchImageView.getHeight());
                BitmapDrawable drawable = new BitmapDrawable(context.getResources(), scratchBitmap);
                return drawable;
            }

            @Override
            protected void onPostExecute(BitmapDrawable drawable) {
                if (drawable != null) {
                    shakeImageView.setImageDrawable(drawable);
                }
            }
        };
        asyncTask.execute();
    }

    @Override
    public void onShake() {

        int colorFrom = context.getResources().getColor(android.R.color.white);
        int colorTo = context.getResources().getColor(android.R.color.transparent);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(800);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                shakeImageView.setColorFilter((int) animator.getAnimatedValue());
            }

        });
        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(shakeImageView, "alpha", 1f, 0f);
        fadeAnim.setDuration(500);

        fadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                shakeImageView.setVisibility(View.GONE);
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(colorAnimation).with(fadeAnim);
        animatorSet.start();
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
