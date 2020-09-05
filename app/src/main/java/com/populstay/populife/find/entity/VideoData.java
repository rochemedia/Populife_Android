package com.populstay.populife.find.entity;

import com.populstay.populife.R;
import com.populstay.populife.app.MyApplication;

import java.util.ArrayList;
import java.util.List;


public class VideoData {
    private static List<VideoBean> sVideoList;
    static {
        String[] videoUrls = MyApplication.getApplication().getResources().getStringArray(R.array.video_url);
        String[] videoCovers = MyApplication.getApplication().getResources().getStringArray(R.array.video_cover);
        int[] videoTitles = {R.string.video_title_1, R.string.video_title_2, R.string.video_title_3, R.string.video_title_4};
        sVideoList = new ArrayList<>();
        for(int i = 0; i < videoUrls.length; i++) {
            sVideoList.add(new VideoBean(videoUrls[i], videoCovers[i], videoTitles[i]));
        }
    }

    public static List<VideoBean> getVideoList() {

        return sVideoList;
    }

    public static VideoBean getVideo() {

        return sVideoList.get(0);
    }
}
