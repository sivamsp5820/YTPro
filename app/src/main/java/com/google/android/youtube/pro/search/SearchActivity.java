package com.google.android.youtube.pro.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.pro.R;
import com.google.android.youtube.pro.adapters.VideoAdapter;
import com.google.android.youtube.pro.extractor.YouTubeSearchExtractor;
import com.google.android.youtube.pro.models.VideoItem;

import java.util.List;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {

    private EditText edtSearch;
    private ImageView btnClear;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private YouTubeSearchExtractor searchExtractor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchExtractor = new YouTubeSearchExtractor();

        ImageView btnBack = findViewById(R.id.btn_back);
        edtSearch = findViewById(R.id.edt_search);
        btnClear = findViewById(R.id.btn_clear);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_search);

        btnBack.setOnClickListener(v -> finish());
        btnClear.setOnClickListener(v -> edtSearch.setText(""));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(this);
        recyclerView.setAdapter(videoAdapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL) {
                performSearch(edtSearch.getText().toString());
                return true;
            }
            return false;
        });

        // Focus search box automatically
        edtSearch.requestFocus();
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;

        progressBar.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<VideoItem> results = searchExtractor.search(query);
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    progressBar.setVisibility(View.GONE);
                    if (results.isEmpty()) {
                        Toast.makeText(SearchActivity.this, "No search results found", Toast.LENGTH_SHORT).show();
                    } else {
                        videoAdapter.setVideos(results);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SearchActivity.this, "Search error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
