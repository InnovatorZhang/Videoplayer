package com.zhang.videoplayer.bean;

/**
 * Created by 张 on 2017/5/19.
 */

import android.graphics.Bitmap;

/**
 * 注意  有一些中没有视频播放地址
 */
public class Video {
    //播放地址
    private String playUri;
    //标题 视频描诉
    private String title;
    //作者名字
    private String authorName;
    //作者照片地址
    private String imageUri;

    public String getPlayUri() {
        return playUri;
    }

    public void setPlayUri(String playUrl) {
        this.playUri = playUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUrl) {
        this.imageUri = imageUrl;
    }
}
