package com.mymusic.orvai.high_pitched_tone.models;

import android.content.Context;

/**
 * Created by orvai on 2018-03-26.
 */

public class Song_list {
    Context mCtx;
    public int song_image;
    public String song_title;
    public String song_singer;
    public int song_data;

    public Song_list(Context mCtx, int song_image, String song_title, String song_singer, int song_url) {
        this.mCtx = mCtx;
        this.song_image = song_image;
        this.song_title = song_title;
        this.song_singer = song_singer;
        this.song_data = song_url;
    }

    public Context getmCtx() {
        return mCtx;
    }

    public int getSong_image() {
        return song_image;
    }

    public String getSong_title() {
        return song_title;
    }

    public String getSong_singer() {
        return song_singer;
    }

    public int getSong_data() {
        return song_data;
    }
}
