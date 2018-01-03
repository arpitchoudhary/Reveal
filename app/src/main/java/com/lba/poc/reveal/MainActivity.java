package com.lba.poc.reveal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private CustomAdapter adapter;
    private ArrayList<String> items = new ArrayList<>();
    private RecyclerView itemRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    int scrollPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new CustomAdapter(this);
        mLayoutManager = new LinearLayoutManager(this);
        // If a layout manager has already been set, get current scroll position.

        items.add("sratch");
        items.add("tap");
        items.add("shake");
        itemRecyclerView = findViewById(R.id.itemRecyclerView);
        itemRecyclerView.setAdapter(adapter);
        itemRecyclerView.setLayoutManager(mLayoutManager);

        if (itemRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) itemRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        itemRecyclerView.scrollToPosition(scrollPosition);
        adapter.setItems(items);
    }

    public void onImageViewDemoClick(View v) {
        startActivity(new Intent(this, ScratchImageActivity.class));
    }

}
