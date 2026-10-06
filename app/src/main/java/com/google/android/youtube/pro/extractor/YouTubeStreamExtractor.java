package com.google.android.youtube.pro.extractor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class YouTubeStreamExtractor {

    private static final String INNERTUBE_PLAYER_URL = "https://www.youtube.com/youtubei/v1/player";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;

    public YouTubeStreamExtractor() {
        this.client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }

    public static class StreamItem {
        public String url;
        public String mimeType;
        public String qualityLabel;
        public int bitrate;
        public boolean isAudioOnly;
        public boolean isVideoOnly;

        @Override
        public String toString() {
            return "StreamItem{" +
                    "qualityLabel='" + qualityLabel + '\'' +
                    ", mimeType='" + mimeType + '\'' +
                    ", bitrate=" + bitrate +
                    ", isAudioOnly=" + isAudioOnly +
                    '}';
        }
    }

    public static class ExtractorResult {
        public String videoId;
        public String title;
        public String author;
        public String hlsManifestUrl;
        public List<StreamItem> streams = new ArrayList<>();
    }

    public ExtractorResult extract(String videoId) throws IOException {
        JsonObject clientObj = new JsonObject();
        clientObj.addProperty("clientName", "ANDROID");
        clientObj.addProperty("clientVersion", "19.05.36");
        clientObj.addProperty("hl", "en");
        clientObj.addProperty("gl", "US");

        JsonObject contextObj = new JsonObject();
        contextObj.add("client", clientObj);

        JsonObject payload = new JsonObject();
        payload.add("context", contextObj);
        payload.addProperty("videoId", videoId);

        RequestBody body = RequestBody.create(payload.toString(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(INNERTUBE_PLAYER_URL)
                .post(body)
                .addHeader("User-Agent", "com.google.android.youtube/19.05.36 (Linux; U; Android 14; en_US)")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Unexpected response code " + response);
            }

            String responseString = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(responseString).getAsJsonObject();

            ExtractorResult result = new ExtractorResult();
            result.videoId = videoId;

            // Extract video details
            if (jsonResponse.has("videoDetails") && jsonResponse.get("videoDetails").isJsonObject()) {
                JsonObject details = jsonResponse.getAsJsonObject("videoDetails");
                if (details.has("title") && !details.get("title").isJsonNull()) result.title = details.get("title").getAsString();
                if (details.has("author") && !details.get("author").isJsonNull()) result.author = details.get("author").getAsString();
            }

            // Extract streaming data
            if (jsonResponse.has("streamingData") && jsonResponse.get("streamingData").isJsonObject()) {
                JsonObject streamingData = jsonResponse.getAsJsonObject("streamingData");

                if (streamingData.has("hlsManifestUrl") && !streamingData.get("hlsManifestUrl").isJsonNull()) {
                    result.hlsManifestUrl = streamingData.get("hlsManifestUrl").getAsString();
                }

                // Combined formats
                if (streamingData.has("formats") && streamingData.get("formats").isJsonArray()) {
                    parseStreamArray(streamingData.getAsJsonArray("formats"), result.streams, false, false);
                }

                // Adaptive formats (separate video / audio)
                if (streamingData.has("adaptiveFormats") && streamingData.get("adaptiveFormats").isJsonArray()) {
                    parseStreamArray(streamingData.getAsJsonArray("adaptiveFormats"), result.streams, true, true);
                }
            }


            return result;
        }
    }

    private void parseStreamArray(JsonArray array, List<StreamItem> streams, boolean allowVideoOnly, boolean allowAudioOnly) {
        for (JsonElement element : array) {
            if (!element.isJsonObject()) continue;
            JsonObject obj = element.getAsJsonObject();

            if (!obj.has("url")) continue;

            StreamItem item = new StreamItem();
            item.url = obj.get("url").getAsString();
            item.mimeType = obj.has("mimeType") ? obj.get("mimeType").getAsString() : "";
            item.qualityLabel = obj.has("qualityLabel") ? obj.get("qualityLabel").getAsString() : "";
            item.bitrate = obj.has("bitrate") ? obj.get("bitrate").getAsInt() : 0;

            if (item.mimeType.startsWith("audio/")) {
                item.isAudioOnly = true;
            } else if (item.mimeType.startsWith("video/")) {
                item.isVideoOnly = !obj.has("audioQuality");
            }

            streams.add(item);
        }
    }
}
