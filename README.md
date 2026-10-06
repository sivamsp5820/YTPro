<p align="center">
  <img src="https://raw.githubusercontent.com/prateek-chaubey/YTPro/main/.github/img/ytpro.gif" height="150" alt="YTPro Banner" />
</p>

<h1 align="center">YT PRO</h1>

<p align="center">
  <b>A modern, high-performance, fully native Android YouTube client built with Android Media3 ExoPlayer, Pitch Dark OLED design, and Google Gemini AI.</b>
</p>

<div align="center">

  [![Gradle](https://github.com/prateek-chaubey/YTPro/actions/workflows/gradle.yml/badge.svg)](https://github.com/prateek-chaubey/YTPro/actions/workflows/gradle.yml)
  [![GitHub release](https://img.shields.io/github/v/release/prateek-chaubey/YTPro?color=red&logo=github)](https://github.com/prateek-chaubey/YTPro/releases)
  [![License](https://img.shields.io/github/license/prateek-chaubey/YTPro?color=blue)](LICENSE)
  [![Downloads](https://img.shields.io/github/downloads/prateek-chaubey/YTPro/total?color=green)](https://github.com/prateek-chaubey/YTPro/releases)

</div>

---

## 🚀 Key Features

* 📱 **100% Native Android Architecture**
  * Fully native UI built with Material3 guidelines, native RecyclerView feeds, and custom adapters.
  * Bypasses `m.youtube.com` completely for 60/120fps smooth scrolling.

* 🎬 **Native Media Player (Android Media3 ExoPlayer)**
  * Hardware-accelerated HLS and DASH stream playback.
  * Immersive full-screen controls, resolution switching, and pitch/speed adjustments.

* 🌑 **Pitch Dark (#000000) OLED Theme**
  * True `#000000` pitch black aesthetic designed for AMOLED displays to minimize battery drain.

* 🔗 **Native App Deep Linking**
  * Explicit deep-linking (`vnd.youtube:<VIDEO_ID>`) into the official YouTube Android app.
  * Automatic `ActivityNotFoundException` fallback to browser/Custom Tabs if the official app is missing.

* 🤖 **Google Gemini AI Video Summaries**
  * Generates video summaries, key takeaways, and transcript answers powered by the Google AI SDK.

* 🎵 **Background Audio Playback & MediaSession**
  * Continuous background audio playback with lock screen controls via Android `MediaSession` & `ForegroundService`.

* 🚫 **SponsorBlock & Return YouTube Dislike**
  * Automatic segment skipping via SponsorBlock API.
  * Public dislike counts powered by the Return YouTube Dislike API.

* ⬇️ **On-Device Stream Downloader & Muxer**
  * Extract high-definition video and audio streams natively.
  * On-device stream merging into MP4/MP3 containers.

---

## 🛠 Tech Stack & Dependencies

| Layer | Technology |
| :--- | :--- |
| **Language** | Java 17 / Kotlin |
| **UI Framework** | Android Material 3, RecyclerView, SwipeRefreshLayout |
| **Media Engine** | `androidx.media3:media3-exoplayer`, `media3-ui`, `media3-session` |
| **Stream Extractor** | Native InnerTube API Engine |
| **Networking & JSON** | OkHttp 4, Gson |
| **Image Loading** | Glide (Memory + Disk Async Caching) |
| **AI Integration** | Google Gemini AI |

---

## 📱 Screenshots

| Home Feed | Native ExoPlayer |
|:--:|:--:|
|<img src="https://raw.githubusercontent.com/prateek-chaubey/YTPro/main/.github/img/screen1.jpg" width="300" /> | <img src="https://raw.githubusercontent.com/prateek-chaubey/YTPro/main/.github/img/screen2.jpg" width="300" /> |

---

## 📦 Building from Source

### Prerequisites
* Android Studio Ladybug (2024.2.1+) or JDK 17+
* Android SDK 36

### Build Commands

```bash
# Clone the repository
git clone https://github.com/prateek-chaubey/YTPro.git
cd YTPro

# Build Debug APK
./gradlew assembleDebug

# Build Signed Release APK
./gradlew assembleRelease
```

The compiled APK will be located at:
`app/build/outputs/apk/release/youtube_pro_signed.apk`

---

## 🤝 Credits & Acknowledgments

* [Android Media3 ExoPlayer](https://developer.android.com/guide/topics/media/media3)
* [SponsorBlock API](https://github.com/ajayyy/SponsorBlock)
* [Return YouTube Dislike API](https://github.com/Anarios/return-youtube-dislike)
* [Google Gemini AI](https://deepmind.google/technologies/gemini/)

---

## 📜 License & Disclaimer

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

*Disclaimer: This is an open-source educational project showcasing native Android media streaming, stream extraction, and AI integrations.*
