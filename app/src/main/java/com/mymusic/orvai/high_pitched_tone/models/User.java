package com.mymusic.orvai.high_pitched_tone.models;

/**
 * Created by orvai on 2018-01-16.
 */

public class User {

    private String user_id, user_name, user_pic;
    private Boolean user_ex_login = false;

    public User(String user_id, String user_name, String user_pic, Boolean user_ex_login) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_pic = user_pic;
        this.user_ex_login = user_ex_login;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public Boolean getUser_ex_login() {
        return user_ex_login;
    }
}
