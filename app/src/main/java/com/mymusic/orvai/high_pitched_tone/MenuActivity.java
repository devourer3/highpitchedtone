package com.mymusic.orvai.high_pitched_tone;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;
import com.mymusic.orvai.high_pitched_tone.models.URLs;
import com.mymusic.orvai.high_pitched_tone.models.User;

import java.util.StringTokenizer;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    TextView user_id;
    TextView user_name;
    ImageView user_pic;
    AlertDialog.Builder builder;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);

        findViewById(R.id.youtube_btn).setOnClickListener(this);
        findViewById(R.id.board_btn).setOnClickListener(this);
        findViewById(R.id.chatting_btn).setOnClickListener(this);
        findViewById(R.id.microphone_btn).setOnClickListener(this);
        findViewById(R.id.maps_btn).setOnClickListener(this);

        toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toolbar.setBackgroundResource(R.color.colorDarkGray);
        setSupportActionBar(toolbar); // 툴바 세팅
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 툴바 타이틀(제목) 지워버리기, 중앙에 제목 입력을 위함



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close); // 현재 액티비티의 참조, drawer_layout객체, toolbar객체, 드로어 여는 문자열, 드로어 닫는 문자열
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_logout).getIcon().setColorFilter(getResources().getColor(R.color.colorOrange), PorterDuff.Mode.SRC_IN); // 로그아웃 색상 바꾸기
        User user = SharedPrefManager.getInstance(this).getUser();
        View nav_header_view = navigationView.getHeaderView(0); // 헤더 뷰를 가져옴
        user_id = nav_header_view.findViewById(R.id.profile_id);
        user_name = nav_header_view.findViewById(R.id.profile_name);
        user_pic = nav_header_view.findViewById(R.id.profile_image);
        user_id.setText(String.valueOf(user.getUser_id()));
        user_name.setText(String.valueOf(user.getUser_name()));
        if (user.getUser_ex_login() == false) {
            Glide.with(getApplicationContext()).load(URLs.URL_IMAGE_ROOT + user.getUser_pic()).into(user_pic);
        } else {
            Glide.with(getApplicationContext()).load(user.getUser_pic()).into(user_pic);
        }
        ImageView lecture = findViewById(R.id.main_start_btn);
        lecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action

        } else if (id == R.id.nav_qna) {
//            item.getIcon().setColorFilter(Color.GRAY,PorterDuff.Mode.SRC_IN);

        } else if (id == R.id.nav_logout) {
            finish();
            SharedPrefManager.getInstance(getApplicationContext()).logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.youtube_btn:
                builder.setIcon(R.drawable.menu_activity_youtube_dialog);
                builder.setTitle("유튜브 강좌");
                builder.setMessage("유명 보컬 5명의 트레이너들의\n보컬 트레이닝을\n유튜브를 통하여 학습할 수 있습니다.");
                builder.setPositiveButton("확인",null);
                builder.create();
                builder.show();
                break;
            case R.id.board_btn:
                builder.setIcon(R.drawable.menu_activity_board_dialog).setTitle("게시판").setMessage("게시판을 이용하여 글과 동영상을\n올릴 수 있으며\n댓글과 공감을 이용하여\n음악적 지식을 공유할 수 있습니다.").setPositiveButton("확인", null).create().show();
                break;
            case R.id.chatting_btn:
                builder.setIcon(R.drawable.menu_activity_chatting_dialog).setTitle("채팅").setMessage("실시간 공개 채팅을 이용하여\n사용자들로부터 발성의 피드백을\n즉시 받을 수 있습니다.").setPositiveButton("확인", null).create().show();
                break;
            case R.id.microphone_btn:
                builder.setIcon(R.drawable.menu_activity_microphone_dialog).setTitle("음정 테스트").setMessage("내장된 마이크를 이용하여\n고음 및 음정을 측정할 수 있습니다.").setPositiveButton("확인", null).create().show();
                break;
            case R.id.maps_btn:
                builder.setIcon(R.drawable.menu_activity_maps_dialog).setTitle("구글 맵").setMessage("구글 맵을 이용하여\n보컬 아카데미의 위치 및\n홈페이지를 확인할 수 있습니다.").setPositiveButton("확인", null).create().show();
                break;
        }

    }
}
