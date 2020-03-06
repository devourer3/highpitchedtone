package com.mymusic.orvai.high_pitched_tone.models;

/**
 * Created by orvai on 2018-02-17.
 */

public class Chat_room {

    public String Chat_room_no;
    public String Chat_room_subject;
    public String Chat_room_cur_users;
    public String Chat_room_max_users;
    public String Chat_room_Lock;
    public String Chat_room_admin;


    public Chat_room(String chat_room_no, String chat_room_subject, String chat_room_cur_users, String chat_room_max_users, String chat_room_Lock, String chat_room_admin) {
        Chat_room_no = chat_room_no;
        Chat_room_subject = chat_room_subject;
        Chat_room_cur_users = chat_room_cur_users;
        Chat_room_max_users = chat_room_max_users;
        Chat_room_Lock = chat_room_Lock;
        Chat_room_admin = chat_room_admin;
    }

    public String getChat_room_no() {
        return Chat_room_no;
    }

    public String getChat_room_subject() {
        return Chat_room_subject;
    }

    public String getChat_room_cur_users() {
        return Chat_room_cur_users;
    }

    public String getChat_room_max_users() {
        return Chat_room_max_users;
    }

    public String getChat_room_Lock() {
        return Chat_room_Lock;
    }

    public String getChat_room_admin() {
        return Chat_room_admin;
    }
}
