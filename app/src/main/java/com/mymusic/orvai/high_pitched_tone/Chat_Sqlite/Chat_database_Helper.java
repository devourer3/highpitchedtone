package com.mymusic.orvai.high_pitched_tone.Chat_Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mymusic.orvai.high_pitched_tone.models.Chat_msg;

import java.util.ArrayList;
import java.util.List;

public class Chat_database_Helper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "My_chat.db";

    public static final String D_TAG = "KB_DATABASE";

    public static final String TABLE_NAME = "My_chat_table";
    public static final int DB_VERSION = 1;
    public static final String COL_0 = "CHAT_NO";
    public static final String COL_1 = "CHAT_ROOM_NO";
    public static final String COL_2 = "CHAT_USER_ID";
    public static final String COL_3 = "CHAT_MSG";
    public static final String COL_4 = "CHAT_USER_PIC_URL";
    public static final String COL_5 = "CHAT_USER_MODIFY";
    public static final String COL_6 = "CHAT_TIME";
    public static final String COL_7 = "CHAT_IMAGE_BOOL";

    private SQLiteDatabase db;

    public Chat_database_Helper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) { // 테이블 만들자
        db.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + COL_0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " // 메시지 넘버
                + COL_1 + " TEXT, " // 방번호
                + COL_2 + " TEXT, " // 유저아이디
                + COL_3 + " TEXT, " // 메시지내용
                + COL_4 + " TEXT, " // 유저사진 URL
                + COL_5 + " INTEGER, " // 유저 입/퇴장 번호(0은퇴장, 1은입장)
                + COL_6 + " TEXT, " // 메시지 시간
                + COL_7 + " INTEGER" + ")" // 메시지의 이미지 여부
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insert_chat_data(Chat_msg chat_msg) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, chat_msg.getChatting_room_number());
        contentValues.put(COL_2, chat_msg.getChatting_user_id());
        contentValues.put(COL_3, chat_msg.getChatting_msg());
        contentValues.put(COL_4, chat_msg.getChatting_user_pic());
        contentValues.put(COL_5, chat_msg.getChatting_user_modify());
        contentValues.put(COL_6, chat_msg.getChatting_server_time());
        contentValues.put(COL_7, chat_msg.getChatting_image());
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        if(result==-1){
            return false;
        } else{
            return true;
        }
    }

    public List<Chat_msg> load_chat_data(String roomNumber) {
        List<Chat_msg> msgs = new ArrayList<>();
        db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "CHAT_ROOM_NO=?", new String[]{roomNumber}, null, null, null);
        if(cursor.moveToFirst()) {
            do{
                Chat_msg chat_msg = new Chat_msg();
                chat_msg.setChatting_room_number(cursor.getString(cursor.getColumnIndex(COL_1)));
                chat_msg.setChatting_user_id(cursor.getString(cursor.getColumnIndex(COL_2)));
                chat_msg.setChatting_msg(cursor.getString(cursor.getColumnIndex(COL_3)));
                chat_msg.setChatting_user_pic(cursor.getString(cursor.getColumnIndex(COL_4)));
                chat_msg.setChatting_user_modify(cursor.getInt(cursor.getColumnIndex(COL_5)));
                chat_msg.setChatting_server_time(cursor.getString(cursor.getColumnIndex(COL_6)));
                chat_msg.setChatting_image(cursor.getInt(cursor.getColumnIndex(COL_7)));
                msgs.add(chat_msg);
             }while(cursor.moveToNext());
        }
        db.close();

        return msgs;
    }

    public boolean delete_chat_data(String roomNumber) {
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "CHAT_ROOM_NO=?", new String[]{roomNumber});
        db.close();
        return true;
    }

}