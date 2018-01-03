package com.lba.poc.reveal;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lba.poc.viewholder.ScratchViewHolder;
import com.lba.poc.viewholder.ShakeViewHolder;
import com.lba.poc.viewholder.TapViewHolder;

import java.util.ArrayList;

/**
 * Created by smohanthy on 1/3/18.
 */

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SCRATCHVIEW = 100;
    private static final int TAPVIEW = 101;
    private static final int SHAKEVIEW = 102;
    private Activity context;
    private ArrayList<String> items = new ArrayList<>();

    public CustomAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case SCRATCHVIEW:
                return ScratchViewHolder.getInstance(LayoutInflater.from(context), parent, context);
            case TAPVIEW:
                return TapViewHolder.getInstance(LayoutInflater.from(context), parent, context);
            case SHAKEVIEW:
                return ShakeViewHolder.getInstance(LayoutInflater.from(context), parent, context);
        }
        return ScratchViewHolder.getInstance(LayoutInflater.from(context), parent, context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ScratchViewHolder) {
            ((ScratchViewHolder) holder).bindHolder();
        }

        if (holder instanceof ShakeViewHolder) {
            ((ShakeViewHolder) holder).bindHolder();
            ((ShakeViewHolder) holder).onResume();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return SCRATCHVIEW;
            case 1:
                return TAPVIEW;
            case 2:
                return SHAKEVIEW;
        }
        return SCRATCHVIEW;
    }

    public void setItems(ArrayList<String> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        this.notifyDataSetChanged();
    }

}
