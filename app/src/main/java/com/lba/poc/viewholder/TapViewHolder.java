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
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lba.poc.reveal.R;

/**
 * Created by smohanthy on 1/3/18.
 */

public class TapViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context context;
    private View itemView;
    private ImageView tapImageView;

    public TapViewHolder(View itemView, Context context) {
        super(itemView);
        this.itemView = itemView;
        this.context = context;
    }

    public static TapViewHolder getInstance(LayoutInflater inflater, ViewGroup parent, Activity context) {
        return new TapViewHolder(inflater.inflate(R.layout.model_shake_image, parent, false), context);
    }

    public void bindHolder() {
        tapImageView = itemView.findViewById(R.id.scratch_view);
        ((TextView) itemView.findViewById(R.id.percentage_revealed)).setText("Tap View");
        tapImageView.setOnClickListener(this);
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
                    tapImageView.setImageDrawable(drawable);
                    itemView.findViewById(R.id.image_view).setVisibility(View.VISIBLE);
                }
            }
        };
        asyncTask.execute();
    }

    public void registerListener() {
        tapImageView.setEnabled(true);
    }

    public void unRegisterListener() {
        tapImageView.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scratch_view) {
            int colorFrom = context.getResources().getColor(android.R.color.white);
            int colorTo = context.getResources().getColor(android.R.color.transparent);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(800);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    tapImageView.setColorFilter((int) animator.getAnimatedValue());
                }

            });
            ValueAnimator fadeAnim = ObjectAnimator.ofFloat(tapImageView, "alpha", 1f, 0f);
            fadeAnim.setDuration(500);

            fadeAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tapImageView.setVisibility(View.GONE);
                }
            });
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(colorAnimation).with(fadeAnim);
            animatorSet.start();
        }

    }
}
