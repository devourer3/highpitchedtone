package com.mymusic.orvai.high_pitched_tone.API_Result;

import com.google.gson.annotations.SerializedName;

/**
 * Created by orvai on 2018-01-31.
 */

public class Game_high_pitch_challenge_rank_API_Result { // retrofit2의 gsonconvert를 이용하기 위한 클래스.. JSON으로 전달하는 response 값을 Gson으로 필드에 매핑하기 위해서 필요하다.

    @SerializedName("pitch_user") // @SerializedName는 Gson이 JSON 키를 필드에 매핑하기 위해서 필요
    private String user_id;
    @SerializedName("pitch_note")
    private String pitch_note;
    @SerializedName("pitch_score")
    private String pitch_score;

    public Game_high_pitch_challenge_rank_API_Result(String user_id, String pitch_note, String pitch_score) {
        this.user_id = user_id;
        this.pitch_note = pitch_note;
        this.pitch_score = pitch_score;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPitch_note() {
        return pitch_note;
    }

    public String getPitch_score() {
        return pitch_score;
    }
}
