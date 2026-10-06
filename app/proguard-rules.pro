# Keep JavaScript Interface methods from obfuscation
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep Media3 ExoPlayer classes
-keep class androidx.media3.** { *; }

# Keep Gson and data models
-keep class com.google.gson.** { *; }
-keep class com.google.android.youtube.pro.models.** { *; }
-keep class com.google.android.youtube.pro.extractor.** { *; }

