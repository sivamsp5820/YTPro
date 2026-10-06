package com.google.android.youtube.pro.nativefeed;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.youtube.pro.MainActivity;
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
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_feed);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        recyclerView = findViewById(R.id.recycler_videos);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        ImageView btnSearch = findViewById(R.id.btn_search);
        ImageView btnBell = findViewById(R.id.btn_bell);

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.google.android.youtube.pro.search.SearchActivity.class);
            startActivity(intent);
        });

        btnBell.setOnClickListener(v -> Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(this);
        recyclerView.setAdapter(videoAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadFeedData);

        com.google.android.material.chip.ChipGroup chipGroup = findViewById(R.id.chip_group);
        if (chipGroup != null) {
            chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
                com.google.android.material.chip.Chip chip = findViewById(checkedId);
                if (chip != null) {
                    String category = chip.getText().toString();
                    if ("All".equalsIgnoreCase(category)) {
                        loadFeedData();
                    } else {
                        loadCategoryData(category);
                    }
                }
            });
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFeedData();
                return true;
            } else if (itemId == R.id.nav_shorts) {
                Intent intent = new Intent(this, com.google.android.youtube.pro.search.SearchActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_subscriptions) {
                Toast.makeText(this, "Subscriptions", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_you) {
                Toast.makeText(this, "Library & Saved Videos", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        loadFeedData();
    }

    private void loadCategoryData(String category) {
        swipeRefreshLayout.setRefreshing(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                com.google.android.youtube.pro.extractor.YouTubeSearchExtractor extractor =
                        new com.google.android.youtube.pro.extractor.YouTubeSearchExtractor();
                List<VideoItem> items = extractor.search(category);

                if (items == null || items.isEmpty()) {
                    items = fetchSampleFeedData();
                }

                final List<VideoItem> finalItems = items;
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    videoAdapter.setVideos(finalItems);
                    swipeRefreshLayout.setRefreshing(false);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
        });
    }


    private void loadFeedData() {
        swipeRefreshLayout.setRefreshing(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                com.google.android.youtube.pro.extractor.YouTubeSearchExtractor extractor =
                        new com.google.android.youtube.pro.extractor.YouTubeSearchExtractor();
                List<VideoItem> items = extractor.getTrendingFeed();

                if (items == null || items.isEmpty()) {
                    items = fetchSampleFeedData();
                }

                final List<VideoItem> finalItems = items;
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    videoAdapter.setVideos(finalItems);
                    swipeRefreshLayout.setRefreshing(false);
                });
            } catch (Exception e) {
                List<VideoItem> fallbackItems = fetchSampleFeedData();
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    videoAdapter.setVideos(fallbackItems);
                    swipeRefreshLayout.setRefreshing(false);
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
