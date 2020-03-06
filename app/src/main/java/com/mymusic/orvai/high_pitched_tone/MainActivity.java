package com.mymusic.orvai.high_pitched_tone;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mymusic.orvai.high_pitched_tone.Service.Socket_Service;
import com.mymusic.orvai.high_pitched_tone.adapters.TabPagerAdapter;
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;
import com.mymusic.orvai.high_pitched_tone.models.URLs;
import com.mymusic.orvai.high_pitched_tone.models.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView user_id;
    TextView user_name;
    ImageView user_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        /**
         * 툴바 및 드로어 세팅
         */

        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar); // 툴바 세팅
        toolbar.setBackgroundResource(R.color.colorBlack);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 툴바 타이틀(제목) 지워버리기

        /**
         * 액션바 토글에 내비게이션 드로어를 붙임
         */

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close); // 현재 액티비티의 참조, drawer_layout객체, toolbar객체, 드로어 여는 문자열, 드로어 닫는 문자열

        drawer.addDrawerListener(toggle);
        toggle.syncState();



        /**
         * 내비게이션 드로어 객체 및 내비게이션에 유저 정보 세팅
         */

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_logout).getIcon().setColorFilter(getResources().getColor(R.color.colorOrange), PorterDuff.Mode.SRC_IN); // 로그아웃 아이콘 색상 바꾸기
        User user = SharedPrefManager.getInstance(this).getUser();
        View nav_header_view = navigationView.getHeaderView(0); // 헤더 뷰를 가져옴
        user_id = nav_header_view.findViewById(R.id.profile_id);
        user_name = nav_header_view.findViewById(R.id.profile_name);
        user_pic = nav_header_view.findViewById(R.id.profile_image);
        user_id.setText(String.valueOf(user.getUser_id()));
        user_name.setText(String.valueOf(user.getUser_name()));

        if(user.getUser_ex_login()==false){
            Glide.with(getApplicationContext()).load(URLs.URL_IMAGE_ROOT+user.getUser_pic()).into(user_pic);
        }else{
            Glide.with(getApplicationContext()).load(user.getUser_pic()).into(user_pic);
        }



        /**
         * 탭 레이아웃 및 뷰페이저 어댑터를 통한 페이지 이동 구현
         */

        final TabLayout tabLayout = findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.menu_youtube_lecture).setText("유튜브"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.menu_bbs).setText("게시판"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.menu_chat).setText("채팅"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.menu_microphone).setText("마이크"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.menu_school).setText("지도"));
        tabLayout.setBackgroundResource(R.color.colorDarkNavy_2);

        final ViewPager viewPager = findViewById(R.id.pager);

        final PagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter); // 뷰페이저 구현

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout)); // 페이지 이동 리스너

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { // 탭 선택 시 리스너
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        //툴바 옵션 클릭 시 이벤트
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // 내비게이션 메뉴 클릭 이벤트
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            finish();
            startActivity(new Intent(getApplicationContext(),MenuActivity.class));

        } else if (id == R.id.nav_qna) {
//            item.getIcon().setColorFilter(Color.GRAY,PorterDuff.Mode.SRC_IN);

        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent("SOCKET_SERVICE");
            intent.putExtra("mode","logout");
            sendBroadcast(intent);
            SharedPrefManager.getInstance(getApplicationContext()).logout();
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START); // 드로어 초기화
        return true;
    }

}