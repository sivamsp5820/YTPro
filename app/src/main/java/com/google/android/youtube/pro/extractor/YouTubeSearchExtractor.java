package com.google.android.youtube.pro.extractor;

import com.google.android.youtube.pro.models.VideoItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class YouTubeSearchExtractor {

    private static final String INNERTUBE_SEARCH_URL = "https://www.youtube.com/youtubei/v1/search";
    private static final String INNERTUBE_BROWSE_URL = "https://www.youtube.com/youtubei/v1/browse";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;

    public YouTubeSearchExtractor() {
        this.client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }

    public List<String> getSearchSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return suggestions;

        try {
            String encoded = URLEncoder.encode(query, "UTF-8");
            String url = "https://suggestqueries.google.com/complete/search?ds=yt&client=youtube&q=" + encoded;
            Request request = new Request.Builder().url(url).build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String bodyStr = response.body().string();
                    // Suggest API returns window.google.ac.h(["query",[["suggest1",0],["suggest2",0]]])
                    int start = bodyStr.indexOf("[[");
                    int end = bodyStr.lastIndexOf("]]");
                    if (start != -1 && end != -1) {
                        String jsonArrayStr = bodyStr.substring(start, end + 2);
                        JsonArray array = JsonParser.parseString(jsonArrayStr).getAsJsonArray();
                        for (JsonElement elem : array) {
                            if (elem.isJsonArray()) {
                                JsonArray item = elem.getAsJsonArray();
                                if (item.size() > 0 && !item.get(0).isJsonNull()) {
                                    suggestions.add(item.get(0).getAsString());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return suggestions;
    }

    public List<VideoItem> search(String query) throws IOException {
        JsonObject clientObj = new JsonObject();
        clientObj.addProperty("clientName", "ANDROID");
        clientObj.addProperty("clientVersion", "19.05.36");
        clientObj.addProperty("hl", "en");
        clientObj.addProperty("gl", "US");

        JsonObject contextObj = new JsonObject();
        contextObj.add("client", clientObj);

        JsonObject payload = new JsonObject();
        payload.add("context", contextObj);
        payload.addProperty("query", query);

        RequestBody body = RequestBody.create(payload.toString(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(INNERTUBE_SEARCH_URL)
                .post(body)
                .addHeader("User-Agent", "com.google.android.youtube/19.05.36 (Linux; U; Android 14; en_US)")
                .build();

        return executeAndParseVideoList(request);
    }

    public List<VideoItem> getTrendingFeed() throws IOException {
        JsonObject clientObj = new JsonObject();
        clientObj.addProperty("clientName", "ANDROID");
        clientObj.addProperty("clientVersion", "19.05.36");
        clientObj.addProperty("hl", "en");
        clientObj.addProperty("gl", "US");

        JsonObject contextObj = new JsonObject();
        contextObj.add("client", contextObj);

        JsonObject payload = new JsonObject();
        payload.add("context", contextObj);
        payload.addProperty("browseId", "FEtrending");

        RequestBody body = RequestBody.create(payload.toString(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(INNERTUBE_BROWSE_URL)
                .post(body)
                .addHeader("User-Agent", "com.google.android.youtube/19.05.36 (Linux; U; Android 14; en_US)")
                .build();

        return executeAndParseVideoList(request);
    }

    private List<VideoItem> executeAndParseVideoList(Request request) throws IOException {
        List<VideoItem> videoList = new ArrayList<>();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return videoList;
            }

            String jsonStr = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(jsonStr).getAsJsonObject();

            parseItemsRecursively(jsonResponse, videoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoList;
    }

    private void parseItemsRecursively(JsonObject jsonObject, List<VideoItem> videoList) {
        if (jsonObject.has("videoRenderer")) {
            JsonObject videoRenderer = jsonObject.getAsJsonObject("videoRenderer");
            VideoItem item = parseVideoRenderer(videoRenderer);
            if (item != null) {
                videoList.add(item);
            }
            return;
        }

        for (String key : jsonObject.keySet()) {
            JsonElement elem = jsonObject.get(key);
            if (elem.isJsonObject()) {
                parseItemsRecursively(elem.getAsJsonObject(), videoList);
            } else if (elem.isJsonArray()) {
                for (JsonElement child : elem.getAsJsonArray()) {
                    if (child.isJsonObject()) {
                        parseItemsRecursively(child.getAsJsonObject(), videoList);
                    }
                }
            }
        }
    }

    private VideoItem parseVideoRenderer(JsonObject videoRenderer) {
        try {
            if (!videoRenderer.has("videoId")) return null;
            String videoId = videoRenderer.get("videoId").getAsString();

            String title = "";
            if (videoRenderer.has("title")) {
                title = extractTextFromRuns(videoRenderer.get("title"));
            }

            String channelName = "";
            if (videoRenderer.has("ownerText")) {
                channelName = extractTextFromRuns(videoRenderer.get("ownerText"));
            } else if (videoRenderer.has("longBylineText")) {
                channelName = extractTextFromRuns(videoRenderer.get("longBylineText"));
            }

            String durationText = "";
            if (videoRenderer.has("lengthText")) {
                durationText = extractTextFromRuns(videoRenderer.get("lengthText"));
            }

            String viewCountText = "";
            if (videoRenderer.has("viewCountText")) {
                viewCountText = extractTextFromRuns(videoRenderer.get("viewCountText"));
            }

            String publishedTimeText = "";
            if (videoRenderer.has("publishedTimeText")) {
                publishedTimeText = extractTextFromRuns(videoRenderer.get("publishedTimeText"));
            }

            String thumbnailUrl = "https://i.ytimg.com/vi/" + videoId + "/hqdefault.jpg";
            if (videoRenderer.has("thumbnail")) {
                JsonObject thumbObj = videoRenderer.getAsJsonObject("thumbnail");
                if (thumbObj.has("thumbnails") && thumbObj.get("thumbnails").isJsonArray()) {
                    JsonArray thumbs = thumbObj.getAsJsonArray("thumbnails");
                    if (thumbs.size() > 0 && thumbs.get(thumbs.size() - 1).isJsonObject()) {
                        thumbnailUrl = thumbs.get(thumbs.size() - 1).getAsJsonObject().get("url").getAsString();
                    }
                }
            }

            String channelAvatarUrl = "";
            if (videoRenderer.has("channelThumbnailSupportedRenderers")) {
                JsonObject supp = videoRenderer.getAsJsonObject("channelThumbnailSupportedRenderers");
                if (supp.has("channelThumbnailWithLinkRenderer")) {
                    JsonObject linkRenderer = supp.getAsJsonObject("channelThumbnailWithLinkRenderer");
                    if (linkRenderer.has("thumbnail") && linkRenderer.getAsJsonObject("thumbnail").has("thumbnails")) {
                        JsonArray avatarThumbs = linkRenderer.getAsJsonObject("thumbnail").getAsJsonArray("thumbnails");
                        if (avatarThumbs.size() > 0) {
                            channelAvatarUrl = avatarThumbs.get(0).getAsJsonObject().get("url").getAsString();
                        }
                    }
                }
            }

            return new VideoItem(videoId, title, channelName, channelAvatarUrl, thumbnailUrl, durationText, viewCountText, publishedTimeText);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractTextFromRuns(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("runs") && obj.get("runs").isJsonArray()) {
                StringBuilder sb = new StringBuilder();
                for (JsonElement run : obj.getAsJsonArray("runs")) {
                    if (run.isJsonObject() && run.getAsJsonObject().has("text")) {
                        sb.append(run.getAsJsonObject().get("text").getAsString());
                    }
                }
                return sb.toString();
            } else if (obj.has("simpleText")) {
                return obj.get("simpleText").getAsString();
            }
        }
        return "";
    }
}
