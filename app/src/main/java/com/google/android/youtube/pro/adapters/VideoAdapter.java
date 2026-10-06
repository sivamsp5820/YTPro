package com.google.android.youtube.pro.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.youtube.pro.R;
import com.google.android.youtube.pro.models.VideoItem;
import com.google.android.youtube.pro.nativeplayer.NativePlayerActivity;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final Context context;
    private final List<VideoItem> videoList = new ArrayList<>();
    private OnVideoClickListener listener;

    public interface OnVideoClickListener {
        void onVideoClick(VideoItem item);
    }

    public VideoAdapter(Context context) {
        this.context = context;
    }

    public void setOnVideoClickListener(OnVideoClickListener listener) {
        this.listener = listener;
    }

    public void setVideos(List<VideoItem> items) {
        this.videoList.clear();
        if (items != null) {
            this.videoList.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void addVideos(List<VideoItem> items) {
        if (items != null) {
            int startPos = videoList.size();
            this.videoList.addAll(items);
            notifyItemRangeInserted(startPos, items.size());
        }
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_card, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem item = videoList.get(position);

        holder.txtTitle.setText(item.getTitle());

        String metaStr = item.getChannelName() + " • " + item.getViewCountText() + " • " + item.getPublishedTimeText();
        holder.txtMeta.setText(metaStr);

        if (item.getDurationText() != null && !item.getDurationText().isEmpty()) {
            holder.txtDuration.setVisibility(View.VISIBLE);
            holder.txtDuration.setText(item.getDurationText());
        } else {
            holder.txtDuration.setVisibility(View.GONE);
        }

        // Load Thumbnail with Glide
        Glide.with(context)
                .load(item.getThumbnailUrl())
                .placeholder(R.drawable.ic_play_arrow_white)
                .centerCrop()
                .into(holder.imgThumbnail);


        // Load Avatar with Glide
        if (item.getChannelAvatarUrl() != null && !item.getChannelAvatarUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getChannelAvatarUrl())
                    .circleCrop()
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.mipmap.app_icon);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVideoClick(item);
            } else {
                Intent intent = new Intent(context, NativePlayerActivity.class);
                intent.putExtra(NativePlayerActivity.EXTRA_VIDEO_ID, item.getVideoId());
                intent.putExtra(NativePlayerActivity.EXTRA_VIDEO_TITLE, item.getTitle());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            com.google.android.youtube.pro.utils.YouTubeAppLauncher.launchVideo(context, item.getVideoId());
            return true;
        });
    }


    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        ShapeableImageView imgAvatar;
        TextView txtDuration;
        TextView txtTitle;
        TextView txtMeta;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            txtDuration = itemView.findViewById(R.id.txt_duration);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtMeta = itemView.findViewById(R.id.txt_meta);
        }
    }
}
