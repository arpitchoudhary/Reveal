package com.lba.poc.reveal;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.lba.poc.model.ModuleItem;
import com.lba.poc.viewholder.ScratchViewHolder;
import com.lba.poc.viewholder.ShakeViewHolder;
import com.lba.poc.viewholder.TapViewHolder;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private CustomAdapter adapter;
    private ArrayList<ModuleItem> items = new ArrayList<>();
    private RecyclerView itemRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    int scrollPosition = 0;
    boolean firstTrackFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new CustomAdapter(this);
        mLayoutManager = new LinearLayoutManager(this);
        // If a layout manager has already been set, get current scroll position.
        itemRecyclerView = findViewById(R.id.itemRecyclerView);
        itemRecyclerView.setAdapter(adapter);
        itemRecyclerView.setLayoutManager(mLayoutManager);

        if (itemRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) itemRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        itemRecyclerView.scrollToPosition(scrollPosition);
        adapter.setItems(getModuleItem());
        startTracking();
    }

    public void findViewVisibility(RecyclerView recyclerView) {
        int firstVisibleItemPosition = ((LinearLayoutManager)
                recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        int lastVisibleItemPosition = ((LinearLayoutManager)
                recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        analyzeAndAddViewData(firstVisibleItemPosition, lastVisibleItemPosition);
    }

    public void onImageViewDemoClick(View v) {
        startActivity(new Intent(this, ScratchImageActivity.class));
    }

    public void startTracking() {

        itemRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                findViewVisibility(recyclerView);
            }
        });
    }

    private void analyzeAndAddViewData(int firstVisibleItemPosition,
                                       int lastVisibleItemPosition) {

        // Analyze all the views
        for (int viewPosition = firstVisibleItemPosition;
             viewPosition <= lastVisibleItemPosition; viewPosition++) {

            Log.i("View being considered", String.valueOf(viewPosition));

            // Get the view from its position.
            View itemView = itemRecyclerView.getLayoutManager()
                    .findViewByPosition(viewPosition);

            // Check if the visibility of the view is more than or equal
            // to the threshold provided. If it falls under the desired limit,
            // add it to the tracking data.

            double minimumVisibleHeightThreshold = 60.0f;
            boolean isVisible = getVisibleHeightPercentage(itemView) >= minimumVisibleHeightThreshold;
            boolean isHide = getVisibleHeightPercentage(itemView) < minimumVisibleHeightThreshold;
            RecyclerView.ViewHolder viewHolder = itemRecyclerView.findViewHolderForAdapterPosition(viewPosition);
            if (viewHolder instanceof ShakeViewHolder && isVisible) {
                adapter.updateViewHolderListener("shake");
            } else if (viewHolder instanceof ShakeViewHolder && isHide) {
                adapter.disableViewHolderListener("shake");
            }

            if (viewHolder instanceof TapViewHolder && isVisible) {
                adapter.updateViewHolderListener("tap");
            } else if (viewHolder instanceof TapViewHolder && isHide) {
                adapter.disableViewHolderListener("tap");
            }

            if (viewHolder instanceof ScratchViewHolder && isVisible) {
                adapter.updateViewHolderListener("scratch");
            } else if (viewHolder instanceof ScratchViewHolder && isHide) {
                adapter.disableViewHolderListener("scratch");
            }

        }
    }

    private double getVisibleHeightPercentage(View view) {
        Rect itemRect = new Rect();
        view.getLocalVisibleRect(itemRect);
        // Find the height of the item.
        double visibleHeight = itemRect.height();
        double height = view.getMeasuredHeight();
        Log.i("Visible Height", String.valueOf(visibleHeight));
        Log.i("Measured Height", String.valueOf(height));
        double viewVisibleHeightPercentage = ((visibleHeight / height) * 100);
        Log.i("Percentage", String.valueOf(viewVisibleHeightPercentage));
        Log.i("___", "___");
        return viewVisibleHeightPercentage;
    }

    private ArrayList<ModuleItem> getModuleItem() {
        ArrayList<ModuleItem> moduleItems = new ArrayList<>();
        ModuleItem moduleItem = new ModuleItem();
        moduleItem.imagePath = "//image/path";
        moduleItem.overlayImagePath = "//overlay/image/path";
        moduleItem.template_type = "scratch";

        ModuleItem moduleItem1 = new ModuleItem();
        moduleItem1.imagePath = "//image/path";
        moduleItem1.overlayImagePath = "//overlay/image/path";
        moduleItem1.template_type = "tap";


        ModuleItem moduleItem2 = new ModuleItem();
        moduleItem2.imagePath = "//image/path";
        moduleItem2.overlayImagePath = "//overlay/image/path";
        moduleItem2.template_type = "shake";

        ModuleItem moduleItem3 = new ModuleItem();
        moduleItem3.imagePath = "//image/path";
        moduleItem3.overlayImagePath = "//overlay/image/path";
        moduleItem3.template_type = "tap";

        moduleItems.add(moduleItem);
        moduleItems.add(moduleItem1);
        moduleItems.add(moduleItem2);
        moduleItems.add(moduleItem3);

        return moduleItems;
    }

}
