package com.populstay.populife.find.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.populstay.populife.R;
import com.populstay.populife.find.entity.VideoBean;
import com.populstay.populife.find.entity.VideoData;

import java.util.List;

import cn.ittiger.player.VideoPlayerView;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private Context mContext;
    private List<VideoBean> mVideoList;
    private int mScreenWidth;

    public VideoAdapter(Context context) {

        mContext = context;
        mVideoList = VideoData.getVideoList();
        int margin =  mContext.getResources().getDimensionPixelSize(R.dimen.common_page_left_right_space) * 2;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels - margin;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.video_item_view, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {

        VideoBean video = mVideoList.get(position);
        holder.mPlayerView.bind(video.getVideoUrl(), mContext.getString(video.getVideoTitle()));
        holder.tvVideoDesc.setText(video.getVideoTitle());
        holder.mPlayerView.getThumbImageView().setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(mContext).load(Uri.parse("file:///android_asset/" + video.getVideoThumbUrl())).into(holder.mPlayerView.getThumbImageView());
        //Glide.with(mContext).load("file:///android_asset/" + video.getVideoThumbUrl()).into(holder.mPlayerView.getThumbImageView());
    }

    @Override
    public int getItemCount() {

        return mVideoList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoPlayerView mPlayerView;
        TextView tvVideoDesc;

        public VideoViewHolder(View itemView) {

            super(itemView);
            mPlayerView = itemView.findViewById(R.id.video_player_view);
            tvVideoDesc = itemView.findViewById(R.id.tv_video_desc);
            mPlayerView.getLayoutParams().width = mScreenWidth;
            mPlayerView.getLayoutParams().height = (int) (mScreenWidth * 1.0f / 16 * 9 + 0.5f);
        }
    }
}
