package com.mymusic.orvai.high_pitched_tone.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by orvai on 2018-01-25.
 */

public class Course {
    private String name;
    private String url;

    public Course(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl(int position) {
        return url;
    }

}
