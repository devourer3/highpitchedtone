package com.mymusic.orvai.high_pitched_tone.API_Result;

import com.google.gson.annotations.SerializedName;

/**
 * Created by orvai on 2018-02-08.
 */

public class Bbs_Comment_view_API_Result {

    @SerializedName("com_no")
    public String com_no;
    @SerializedName("com_content")
    public String com_content;
    @SerializedName("com_user")
    public String com_user;
    @SerializedName("com_date")
    public String com_date;


    public Bbs_Comment_view_API_Result(String com_no, String com_content, String com_user, String com_date) {
        this.com_no = com_no;
        this.com_content = com_content;
        this.com_user = com_user;
        this.com_date = com_date;
    }

    public String getCom_content() {
        return com_content;
    }

    public String getCom_user() {
        return com_user;
    }

    public String getCom_date() {
        return com_date;
    }

    public String getCom_no() {
        return com_no;
    }
}