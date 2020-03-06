package com.mymusic.orvai.high_pitched_tone.API_Result;

import com.google.gson.annotations.SerializedName;

/**
 * Created by orvai on 2018-02-02.
 */

public class Bbs_View_content_API_Result {

    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("bbs_rec_mark")
    private Boolean rec;

    @SerializedName("bbs_subject")
    private String subject;

    @SerializedName("bbs_user")
    private String user;

    @SerializedName("bbs_content")
    private String content;

    @SerializedName("bbs_image")
    private String image;

    @SerializedName("bbs_video")
    private String video;

    @SerializedName("bbs_time")
    private String time;

    @SerializedName("bbs_rec_amount")
    private String rec_amt;


    public Bbs_View_content_API_Result(Boolean error, String message, Boolean rec, String subject, String user, String content, String image, String video, String time, String rec_amt) {
        this.error = error;
        this.message = message;
        this.rec = rec;
        this.subject = subject;
        this.user = user;
        this.content = content;
        this.image = image;
        this.video = video;
        this.time = time;
        this.rec_amt = rec_amt;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public String getVideo() {
        return video;
    }

    public String getTime() {
        return time;
    }

    public Boolean getRec() {
        return rec;
    }

    public String getRec_amt() {
        return rec_amt;
    }
}
