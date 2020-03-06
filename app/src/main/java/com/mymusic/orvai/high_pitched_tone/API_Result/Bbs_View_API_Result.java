package com.mymusic.orvai.high_pitched_tone.API_Result;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by orvai on 2018-02-01.
 */

public class Bbs_View_API_Result { // Bbs 디비에 저장된 넘버, 제목, 유저이름을 저장하기 위한 빈 클래스


    @SerializedName("bbs_no")
    public String bbs_no;
    @SerializedName("bbs_subject")
    public String bbs_subject;
    @SerializedName("bbs_user")
    public String bbs_user;
    @SerializedName("bbs_com")
    public String bbs_comment;
    @SerializedName("bbs_rec")
    public String bbs_rec;

    public Bbs_View_API_Result(String bbs_no, String bbs_subject, String bbs_user, String bbs_comment, String bbs_rec) {
        this.bbs_no = bbs_no;
        this.bbs_subject = bbs_subject;
        this.bbs_user = bbs_user;
        this.bbs_comment = bbs_comment;
        this.bbs_rec = bbs_rec;
    }
}
