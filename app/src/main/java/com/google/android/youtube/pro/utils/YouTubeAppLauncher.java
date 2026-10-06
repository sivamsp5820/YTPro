package com.google.android.youtube.pro.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class YouTubeAppLauncher {

    /**
     * Launches a YouTube video using native deep linking (vnd.youtube:<VIDEO_ID>).
     * Attempts explicit routing to com.google.android.youtube first.
     * Safely catches ActivityNotFoundException and falls back to browser/HTTPS link.
     */
    public static void launchVideo(Context context, String videoId) {
        if (videoId == null || videoId.isEmpty()) {
            return;
        }

        // Primary: Deep-link directly into the official YouTube app
        try {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
            appIntent.setPackage("com.google.android.youtube");
            if (!(context instanceof android.app.Activity)) {
                appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException e1) {
            // Fallback 1: Try general vnd.youtube scheme without package restriction
            try {
                Intent genericAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                if (!(context instanceof android.app.Activity)) {
                    genericAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(genericAppIntent);
            } catch (ActivityNotFoundException e2) {
                // Fallback 2: Open HTTPS link in external web browser / Custom Tab
                try {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
                    if (!(context instanceof android.app.Activity)) {
                        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    context.startActivity(webIntent);
                } catch (Exception e3) {
                    Toast.makeText(context, "No app available to handle YouTube link", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Extracts video ID from standard YouTube URLs (youtube.com/watch?v=ID, youtu.be/ID)
     * and launches the video natively.
     */
    public static void launchUrl(Context context, String url) {
        if (url == null || url.isEmpty()) return;

        String videoId = extractVideoId(url);
        if (videoId != null && !videoId.isEmpty()) {
            launchVideo(context, videoId);
        } else {
            // Fallback for non-video URLs (e.g. channel links, playlists)
            try {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if (!(context instanceof android.app.Activity)) {
                    webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(webIntent);
            } catch (Exception e) {
                Toast.makeText(context, "Cannot open URL", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String extractVideoId(String url) {
        if (url == null) return null;
        if (url.contains("v=")) {
            int index = url.indexOf("v=");
            String idStr = url.substring(index + 2);
            int ampersandIndex = idStr.indexOf("&");
            if (ampersandIndex != -1) {
                return idStr.substring(0, ampersandIndex);
            }
            return idStr;
        } else if (url.contains("youtu.be/")) {
            int index = url.indexOf("youtu.be/");
            String idStr = url.substring(index + 9);
            int questionIndex = idStr.indexOf("?");
            if (questionIndex != -1) {
                return idStr.substring(0, questionIndex);
            }
            return idStr;
        } else if (url.contains("shorts/")) {
            int index = url.indexOf("shorts/");
            String idStr = url.substring(index + 7);
            int questionIndex = idStr.indexOf("?");
            if (questionIndex != -1) {
                return idStr.substring(0, questionIndex);
            }
            return idStr;
        }
        return null;
    }
}
