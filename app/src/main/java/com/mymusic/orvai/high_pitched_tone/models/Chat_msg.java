package com.mymusic.orvai.high_pitched_tone.models;

/**
 * Created by orvai on 2018-02-20.
 */

public class Chat_msg {

    private String chatting_room_number;
    private String chatting_user_id;
    private String chatting_msg;
    private String chatting_user_pic;
    private int chatting_user_modify; // 0번은 퇴장, 1은 입장
    private String chatting_server_time;
    private int chatting_image; // 1 이미지, 0은 메시지

    public Chat_msg() {
    }

    public Chat_msg(String chatting_room_number, String chatting_user_id, String chatting_msg, String chatting_user_pic, int chatting_user_modify, String chatting_server_time, int chatting_image) {
        this.chatting_room_number = chatting_room_number;
        this.chatting_user_id = chatting_user_id;
        this.chatting_msg = chatting_msg;
        this.chatting_user_pic = chatting_user_pic;
        this.chatting_user_modify = chatting_user_modify;
        this.chatting_server_time = chatting_server_time;
        this.chatting_image = chatting_image;
    }

    public String getChatting_room_number() {
        return chatting_room_number;
    }

    public String getChatting_user_id() {
        return chatting_user_id;
    }

    public String getChatting_msg() {
        return chatting_msg;
    }

    public String getChatting_user_pic() {
        return chatting_user_pic;
    }

    public int getChatting_user_modify() {
        return chatting_user_modify;
    }

    public String getChatting_server_time() {
        return chatting_server_time;
    }

    public int getChatting_image() {
        return chatting_image;
    }

    public void setChatting_room_number(String chatting_room_number) {
        this.chatting_room_number = chatting_room_number;
    }

    public void setChatting_user_id(String chatting_user_id) {
        this.chatting_user_id = chatting_user_id;
    }

    public void setChatting_msg(String chatting_msg) {
        this.chatting_msg = chatting_msg;
    }

    public void setChatting_user_pic(String chatting_user_pic) {
        this.chatting_user_pic = chatting_user_pic;
    }

    public void setChatting_user_modify(int chatting_user_modify) {
        this.chatting_user_modify = chatting_user_modify;
    }

    public void setChatting_server_time(String chatting_server_time) {
        this.chatting_server_time = chatting_server_time;
    }

    public void setChatting_image(int chatting_image) {
        this.chatting_image = chatting_image;
    }
}
