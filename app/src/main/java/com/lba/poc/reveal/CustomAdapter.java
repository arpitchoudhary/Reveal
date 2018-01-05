package com.lba.poc.reveal;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lba.poc.model.ModuleItem;
import com.lba.poc.viewholder.ScratchViewHolder;
import com.lba.poc.viewholder.ShakeViewHolder;
import com.lba.poc.viewholder.TapViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by smohanthy on 1/3/18.
 */

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SCRATCHVIEW = 100;
    private static final int TAPVIEW = 101;
    private static final int SHAKEVIEW = 102;
    private Activity context;
    private List<ModuleItem> items;
    private ScrollListener scrollListener;

    public CustomAdapter(Activity context) {
        this.context = context;
        items = Collections.synchronizedList(new ArrayList<ModuleItem>());
    }

    public void setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
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
            ((ScratchViewHolder) holder).setScrollListener(scrollListener);

        }

        if (holder instanceof ShakeViewHolder) {
            ((ShakeViewHolder) holder).bindHolder();
        }

        if (holder instanceof TapViewHolder) {
            ((TapViewHolder) holder).bindHolder();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (items.get(position).template_type) {
            case "scratch":
                return SCRATCHVIEW;
            case "tap":
                return TAPVIEW;
            case "shake":
                return SHAKEVIEW;
            default:
                return SCRATCHVIEW;
        }
    }

    public void setItems(ArrayList<ModuleItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        this.notifyDataSetChanged();
    }

    public void updateViewHolderListener(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ScratchViewHolder) {
            ((ScratchViewHolder) viewHolder).registerListener();
        }

        if (viewHolder instanceof ShakeViewHolder) {
            ((ShakeViewHolder) viewHolder).onResume();
        }

        if (viewHolder instanceof TapViewHolder) {
            ((TapViewHolder) viewHolder).registerListener();
        }
    }

    public void disableViewHolderListener(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ScratchViewHolder) {
            ((ScratchViewHolder) viewHolder).unRegisterListener();
        }

        if (viewHolder instanceof ShakeViewHolder) {
            ((ShakeViewHolder) viewHolder).onPause();
        }

        if (viewHolder instanceof TapViewHolder) {
            ((TapViewHolder) viewHolder).unRegisterListener();
        }
    }
}
