package com.mymusic.orvai.high_pitched_tone.models;

/**
 * Created by orvai on 2018-01-17.
 */

public class URLs {

    public static final String USER_ROOT_URL = "http://52.78.218.125/High_Pitch_User.php?action_call="; // Volley 용 URL

    public static final String URL_REGISTER = USER_ROOT_URL + "register";

    public static final String URL_LOGIN = USER_ROOT_URL + "login";

    public static final String URL_IMAGE_ROOT = "http://52.78.218.125/images/"; //

    public static final String BASE_URL = "http://52.78.218.125/"; // Retrofit2 용 업로드 URL

    public static final String BBS_IMAGE$VIDEO_URL = "http://52.78.218.125/bbs_upload/"; // Retrofit2 용 이미지 다운로드 URL
}