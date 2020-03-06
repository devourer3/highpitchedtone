package com.mymusic.orvai.high_pitched_tone.API_Result;

import com.google.gson.annotations.SerializedName;

/**
 * Created by orvai on 2018-02-08.
 */

public class Bbs_View_recommend_API_Result {

    @SerializedName("error")
    private Boolean error;

    @SerializedName("success")
    private String success;

    @SerializedName("rec_mark")
    private Boolean rec_mark;

    @SerializedName("rec_amount")
    private String rec_amount;

    public Bbs_View_recommend_API_Result(Boolean error, String success, Boolean rec_mark, String rec_amount) {
        this.error = error;
        this.success = success;
        this.rec_mark = rec_mark;
        this.rec_amount = rec_amount;
    }


    public Boolean getError() {
        return error;
    }

    public String getSuccess() {
        return success;
    }

    public Boolean getRec_mark() {
        return rec_mark;
    }

    public String getRec_amount() {
        return rec_amount;
    }
}
