package com.mymusic.orvai.high_pitched_tone.API_Result;

import com.google.gson.annotations.SerializedName;

/**
 * Created by orvai on 2018-02-10.
 */

public class Bbs_Comment_delete_API_Result {

    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    public Bbs_Comment_delete_API_Result(Boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}