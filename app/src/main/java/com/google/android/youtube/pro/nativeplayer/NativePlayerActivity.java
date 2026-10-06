package com.google.android.youtube.pro.nativeplayer;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.google.android.youtube.pro.R;

public class NativePlayerActivity extends AppCompatActivity {

    public static final String EXTRA_STREAM_URL = "extra_stream_url";
    public static final String EXTRA_VIDEO_ID = "extra_video_id";
    public static final String EXTRA_VIDEO_TITLE = "extra_video_title";

    private PlayerView playerView;
    private ExoPlayer player;
    private String streamUrl;
    private String videoId;
    private String videoTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar for immersive full screen playback
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_native_player);

        playerView = findViewById(R.id.native_player_view);

        if (getIntent() != null) {
            streamUrl = getIntent().getStringExtra(EXTRA_STREAM_URL);
            videoId = getIntent().getStringExtra(EXTRA_VIDEO_ID);
            videoTitle = getIntent().getStringExtra(EXTRA_VIDEO_TITLE);
        }

        if (streamUrl != null && !streamUrl.isEmpty()) {
            initializePlayer(streamUrl);
        } else if (videoId != null && !videoId.isEmpty()) {
            extractAndPlay(videoId);
        } else {
            Toast.makeText(this, "No video URL or Video ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void extractAndPlay(String id) {
        Toast.makeText(this, "Extracting native video stream...", Toast.LENGTH_SHORT).show();
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            try {
                com.google.android.youtube.pro.extractor.YouTubeStreamExtractor extractor =
                        new com.google.android.youtube.pro.extractor.YouTubeStreamExtractor();
                com.google.android.youtube.pro.extractor.YouTubeStreamExtractor.ExtractorResult result =
                        extractor.extract(id);

                String playableUrl = null;
                if (result.hlsManifestUrl != null && !result.hlsManifestUrl.isEmpty()) {
                    playableUrl = result.hlsManifestUrl;
                } else if (!result.streams.isEmpty()) {
                    playableUrl = result.streams.get(0).url;
                }

                final String finalUrl = playableUrl;
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    if (finalUrl != null) {
                        this.streamUrl = finalUrl;
                        initializePlayer(finalUrl);
                    } else {
                        Toast.makeText(NativePlayerActivity.this, "Failed to resolve stream URL", Toast.LENGTH_LONG).show();
                    }
                });


            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(NativePlayerActivity.this, "Extraction error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void initializePlayer(String url) {
        if (player != null) {
            player.release();
        }
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
        player.setMediaItem(mediaItem);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Toast.makeText(NativePlayerActivity.this, "Playback Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        player.prepare();
        player.setPlayWhenReady(true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (player == null && streamUrl != null && !streamUrl.isEmpty()) {
            initializePlayer(streamUrl);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
