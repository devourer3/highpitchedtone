package com.mymusic.orvai.high_pitched_tone.Shared_Preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mymusic.orvai.high_pitched_tone.LoginActivity;
import com.mymusic.orvai.high_pitched_tone.models.User;

import static com.mymusic.orvai.high_pitched_tone.LoginActivity.mAuth;
import static com.mymusic.orvai.high_pitched_tone.LoginActivity.mGoogleSignInClient;

/**
 * Created by orvai on 2018-01-16.
 */

public class SharedPrefManager { // 로그인 세션을 유지하기 위해, 셰어드 프리퍼런스를 사용해야 한다.

    public static final String SHARED_PREF_NAME = "USER_INFO";
    public static final String KEY_USER_NAME = "USER_NAME";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_USER_PIC = "USER_PIC";
    public static final String KEY_EXTERNAL_USER_PIC = "EXTERNAL_USER_PIC";
    public static final String SHARED_PREF_NAME2 = "USER_ROOM_NUMBER";

    public static SharedPrefManager mInstance;
    private static Context mCtx; // 안드로이드 시스템(context)을 다루는 변수

    private SharedPrefManager(Context context) {
        mCtx = context; // 생성자를 거친 어플리케이션 컨텍스트.
    }

    public static synchronized SharedPrefManager getInstance(Context context) { // 외부에서 쉐어드 프리퍼런스에 접근하기 위한 메서드(객체를 새로 생성하지 않고도(new 없이도) 접근 가능), 싱글턴 패턴이다.
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void userLogin(User user) { // 로그인 했을 시, 유저의 정보를 저장하기 위한 셰어드 프리퍼런스.
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, user.getUser_id());
        editor.putString(KEY_USER_NAME, user.getUser_name());
        editor.putString(KEY_USER_PIC, user.getUser_pic());
        editor.putBoolean(KEY_EXTERNAL_USER_PIC, user.getUser_ex_login());
        editor.apply();
    }

    public boolean isLoggedIn(){ // 이미 로그인 했는지 안했는지 알도록 하는 셰어드 프리퍼런스.
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_ID, null) != null;
    }

    public User getUser(){ // 로그인 한 유저의 정보를 담은 유저 헬퍼 클래스, 셰어드프린퍼런스의 getter라 생각
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_USER_ID, null),
                sharedPreferences.getString(KEY_USER_NAME, null),
                sharedPreferences.getString(KEY_USER_PIC, null),
                sharedPreferences.getBoolean(KEY_EXTERNAL_USER_PIC, false)
        );
    }

    public void saveMyroom(String roomNumber, String roomName) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(roomNumber, roomName);
        editor.apply();
    }

    public boolean isMyroom(String roomNumber) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);
        boolean myroomisthere = sharedPreferences.contains(roomNumber);
        if(myroomisthere==true) {
            return true;
        }
        return false;
    }

    public void deleteMyroom(String roomNumber) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(roomNumber);
        editor.apply();
    }


    @SuppressLint("RestrictedApi")
    public void logout() { // 로그아웃 메소드, 셰어드 프리퍼런스 다 지워놓음.
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mAuth.signOut(); // 파이어베이스 로그아웃
        mGoogleSignInClient.signOut(); // 구글연동 로그아웃
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }
}
