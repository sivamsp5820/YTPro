package com.google.android.youtube.pro.nativefeed;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.youtube.pro.R;
import com.google.android.youtube.pro.adapters.VideoAdapter;
import com.google.android.youtube.pro.models.VideoItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class NativeFeedActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_feed);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        recyclerView = findViewById(R.id.recycler_videos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(this);
        recyclerView.setAdapter(videoAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadFeedData);

        loadFeedData();
    }

    private void loadFeedData() {
        swipeRefreshLayout.setRefreshing(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Fetch trending/home feed videos
                List<VideoItem> items = fetchSampleFeedData();

                runOnUiThread(() -> {
                    videoAdapter.setVideos(items);
                    swipeRefreshLayout.setRefreshing(false);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(NativeFeedActivity.this, "Error loading feed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private List<VideoItem> fetchSampleFeedData() {
        List<VideoItem> sampleItems = new ArrayList<>();
        sampleItems.add(new VideoItem(
                "dQw4w9WgXcQ",
                "Rick Astley - Never Gonna Give You Up (Official Music Video)",
                "Rick Astley",
                "https://yt3.ggpht.com/a/AATXAJz-Fm9FvC1wVbZJ_F9WzZ9gZ1Z9Z9Z9Z9Z9=s88-c-k-c0x00ffffff-no-rj",
                "https://i.ytimg.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
                "3:33",
                "1.5B views",
                "14 years ago"
        ));
        sampleItems.add(new VideoItem(
                "L_LUpnjgPso",
                "Official Trailer: Marvel Studios' Deadpool & Wolverine",
                "Marvel Entertainment",
                "https://yt3.ggpht.com/fGZytwDxM6wqZxnZ_a=s88-c-k-c0x00ffffff-no-rj",
                "https://i.ytimg.com/vi/L_LUpnjgPso/maxresdefault.jpg",
                "2:38",
                "65M views",
                "2 months ago"
        ));
        sampleItems.add(new VideoItem(
                "kJQP7kiw5Fk",
                "Luis Fonsi - Despacito ft. Daddy Yankee",
                "Luis Fonsi",
                "https://yt3.ggpht.com/a/AATXAJx-Fm9FvC1wVbZJ_F9WzZ9gZ1Z9Z9Z9Z9Z9=s88-c-k-c0x00ffffff-no-rj",
                "https://i.ytimg.com/vi/kJQP7kiw5Fk/maxresdefault.jpg",
                "4:41",
                "8.4B views",
                "7 years ago"
        ));
        return sampleItems;
    }
}
