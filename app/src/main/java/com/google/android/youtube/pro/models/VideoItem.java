package com.google.android.youtube.pro.models;

import java.io.Serializable;

public class VideoItem implements Serializable {

    private String videoId;
    private String title;
    private String channelName;
    private String channelAvatarUrl;
    private String thumbnailUrl;
    private String durationText;
    private String viewCountText;
    private String publishedTimeText;

    public VideoItem() {}

    public VideoItem(String videoId, String title, String channelName, String channelAvatarUrl,
                     String thumbnailUrl, String durationText, String viewCountText, String publishedTimeText) {
        this.videoId = videoId;
        this.title = title;
        this.channelName = channelName;
        this.channelAvatarUrl = channelAvatarUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.durationText = durationText;
        this.viewCountText = viewCountText;
        this.publishedTimeText = publishedTimeText;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelAvatarUrl() {
        return channelAvatarUrl;
    }

    public void setChannelAvatarUrl(String channelAvatarUrl) {
        this.channelAvatarUrl = channelAvatarUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public String getViewCountText() {
        return viewCountText;
    }

    public void setViewCountText(String viewCountText) {
        this.viewCountText = viewCountText;
    }

    public String getPublishedTimeText() {
        return publishedTimeText;
    }

    public void setPublishedTimeText(String publishedTimeText) {
        this.publishedTimeText = publishedTimeText;
    }
}
