package com.populstay.populife.find.entity;

public class VideoBean {
    private String mVideoUrl;
    private String mVideoThumbUrl;
    private int mVideoTitle;

    public VideoBean(String videoUrl, String videoThumbUrl, int videoTitle) {

        mVideoUrl = videoUrl;
        mVideoThumbUrl = videoThumbUrl;
        mVideoTitle = videoTitle;
    }

    public String getVideoUrl() {

        return mVideoUrl;
    }

    public String getVideoThumbUrl() {

        return mVideoThumbUrl;
    }

    public int getVideoTitle() {

        return mVideoTitle;
    }
}
