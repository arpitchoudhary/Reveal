package com.lba.poc.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.lba.poc.reveal.R;
import com.lba.poc.widgets.ScratchImageView;

/**
 * Created by smohanthy on 1/3/18.
 */

public class ScratchViewHolder extends RecyclerView.ViewHolder implements ScratchImageView.IRevealListener {
    private final Context context;
    private TextView revealText;
    private ScratchImageView scratchImageView;
    private View itemView;

    public static ScratchViewHolder getInstance(LayoutInflater inflater, ViewGroup parent, Context context) {
        return new ScratchViewHolder(inflater.inflate(R.layout.activity_scratch_image, parent, false), context);
    }

    public ScratchViewHolder(View itemView, Context context) {
        super(itemView);
        this.itemView = itemView;
        this.context = context;
    }

    public void bindHolder() {
        scratchImageView = itemView.findViewById(R.id.scratch_view);
        scratchImageView.setRevealListener(this);

        revealText = itemView.findViewById(R.id.percentage_revealed);
        revealText.setText(context.getString(R.string.reveal_percentage, 0F));
        scratchImageView.setmEnabled(false);

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
                    scratchImageView.setDrawable(drawable);
                    scratchImageView.setmEnabled(true);
                }
            }
        };
        asyncTask.execute();
    }

    @Override
    public void onRevealed(ScratchImageView view) {
        revealText.setText(context.getString(R.string.revealed));
        view.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        animation.setDuration(500L);
        view.setAnimation(animation);
    }

    @Override
    public void onRevealPercentChangedListener(ScratchImageView siv, float percent) {
        revealText.setText(context.getString(R.string.reveal_percentage, percent * 100));
    }

    @Override
    public void onTouchUp() {

    }
}
