package com.lba.poc.reveal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.lba.poc.widgets.ScratchImageView;
import com.lba.poc.widgets.ShakeDetector;

public class ScratchImageActivity extends AppCompatActivity implements ScratchImageView.IRevealListener, ShakeDetector.OnShakeListener {

    private TextView revealText;
    private ScratchImageView scratchImageView;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_image);

        scratchImageView = findViewById(R.id.scratch_view);
        scratchImageView.setRevealListener(this);

        revealText = findViewById(R.id.percentage_revealed);
        revealText.setText(getString(R.string.reveal_percentage, 0F));
        scratchImageView.setmEnabled(false);

        AsyncTask<Void, Void, BitmapDrawable> asyncTask = new AsyncTask<Void, Void, BitmapDrawable>() {
            @Override
            protected BitmapDrawable doInBackground(Void... voids) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Bitmap scratchBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marble);
                // scratchBitmap = getResizedBitmap(scratchBitmap, scratchImageView.getWidth(), scratchImageView.getHeight());
                BitmapDrawable drawable = new BitmapDrawable(getResources(), scratchBitmap);
                return drawable;
            }

            @Override
            protected void onPostExecute(BitmapDrawable drawable) {
                if (drawable != null) {
                    scratchImageView.setDrawable(drawable);
                    scratchImageView.setmEnabled(true);
                }
            }
        };
        asyncTask.execute();

// ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @Override
    public void onRevealed(ScratchImageView view) {
        revealText.setText(getString(R.string.revealed));
        view.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animation.setDuration(500L);
        view.setAnimation(animation);
    }

    @Override
    public void onRevealPercentChangedListener(ScratchImageView view, float percent) {
        revealText.setText(getString(R.string.reveal_percentage, percent * 100));
    }

    @Override
    public void onTouchUp() {
        revealText.setText(getString(R.string.revealed));
        scratchImageView.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animation.setDuration(500L);
        scratchImageView.setAnimation(animation);
    }

    @Override
    public void updateScrollLock() {

    }

    @Override
    public void updateScrollRelease() {

    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    public void onShake() {
        revealText.setText(getString(R.string.revealed));
        scratchImageView.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animation.setDuration(500L);
        scratchImageView.setAnimation(animation);
    }
}
