package com.lba.poc.viewholder;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lba.poc.reveal.R;

/**
 * Created by smohanthy on 1/3/18.
 */

public class TapViewHolder extends RecyclerView.ViewHolder {
    public TapViewHolder(View itemView) {
        super(itemView);
    }

    public static TapViewHolder getInstance(LayoutInflater inflater, ViewGroup parent, Activity context) {
        return new TapViewHolder(inflater.inflate(R.layout.activity_scratch_image, parent, false));
    }
}
