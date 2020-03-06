package com.mymusic.orvai.high_pitched_tone.API_Result;

import com.google.gson.annotations.SerializedName;

/**
 * Created by orvai on 2018-01-31.
 */

public class Game_high_pitch_challenge_API_Result { // retrofit2의 gsonconvert를 이용하기 위한 클래스.. JSON으로 전달하는 response 값을 Gson으로 필드에 매핑하기 위해서 필요하다.

    @SerializedName("error") // @SerializedName는 Gson이 JSON 키를 필드에 매핑하기 위해서 필요
    private Boolean error;

    @SerializedName("message")
    private String message;

    public Game_high_pitch_challenge_API_Result(Boolean error, String message) {
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
