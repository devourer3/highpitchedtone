package com.mymusic.orvai.high_pitched_tone;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_Comment_Api_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_Comment_view_API_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_View_content_API_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_View_delete_API_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_View_recommend_API_Result;
import com.mymusic.orvai.high_pitched_tone.EventBus.BusProvider;
import com.mymusic.orvai.high_pitched_tone.Interface.API_Service;
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;
import com.mymusic.orvai.high_pitched_tone.adapters.Bbs_Comment_View_adapter;
import com.mymusic.orvai.high_pitched_tone.models.URLs;
import com.mymusic.orvai.high_pitched_tone.models.User;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;

public class Bbs_view_activity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 게시판 글 보기 클래스
     */

    TextView bbs_subject, bbs_content;
    // 게시판 제목, 게시판 내용
    String s_bbs_user, s_bbs_subject, s_bbs_content, comment, video_path, picture_path, video_file_path;
    // Mysql DB에 저장된 유저의 아이디
    // Mysql DB에 저장된 제목
    // Mysql DB에 저장된 내용
    // 댓글 EditText
    // DB에 저장된 비디오 파일 이름
    // DB에 저장된 사진 파일 이름
    // AWS에 저장된 비디오 파일 경로

    Button rec_btn; // 추천 버튼
    static public String request_no; // 인텐트로 넘어온 방 번호
    boolean options_menu = false; // 게시글 유저의 아이디와 클라이언트 유저의 아이디와 일치하지 않을 경우 옵션메뉴 표시 여부 불린 값
    ImageView bbs_image; // 해당 게시글의 이미지 뷰를 표시할 공간
    VideoView bbs_video; // 해당 게시글의 비디오 뷰를 표시할 공간
    AlertDialog.Builder builder; // 게시글 삭제 창을 띄우는 다이얼로그 빌더
    Retrofit retrofit;
    API_Service service;
    User user;
    EditText e_comment;
    Intent intent;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Bbs_Comment_view_API_Result> comment_list;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(Bbs_view_activity.this); // 이벤트 버스 등록
        setContentView(R.layout.activity_bbs_view);

        user = SharedPrefManager.getInstance(this).getUser();
        builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);
        intent = getIntent();
        request_no = intent.getStringExtra("request_no");
        toolbar = findViewById(R.id.bbs_view_toolbar);
        bbs_subject = findViewById(R.id.bbs_subject);
        bbs_content = findViewById(R.id.bbs_content);
        e_comment = findViewById(R.id.e_comment);
        bbs_image = findViewById(R.id.bbs_image);
        bbs_video = findViewById(R.id.bbs_video);
        rec_btn = findViewById(R.id.rec_btn);
        recyclerView = findViewById(R.id.comment_recycler);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.comment_reg_btn).setOnClickListener(this);
        rec_btn.setOnClickListener(this);

        retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(API_Service.class);

        comment_list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int index = parent.getChildAdapterPosition(view) + 1;
                outRect.set(5, 5, 5, 5);
                view.setBackgroundColor(0xFFf1f1f1);
                ViewCompat.setElevation(view, 5.0f);
            }
        });
        view_content();
        view_comment();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        view_content();
        request_no = intent.getStringExtra("request_no");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(Bbs_view_activity.this); // 이벤트 버스 해제
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_back_btn: // 백버튼을 눌렀을 경우
                onBackPressed();
                break;
//                AlertDialog dialog = builder.create(); 이건 Alert다이얼로그 background 등을 수정 할 때 dialog 객체를 생성할 필요.
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.dialog_show();
            case R.id.rec_btn: // 추천 버튼을 눌렀을 경우
                Call<Bbs_View_recommend_API_Result> result = service.request_Bbs_recommend(request_no, user.getUser_id());
                result.enqueue(new Callback<Bbs_View_recommend_API_Result>() {
                    @Override
                    public void onResponse(Call<Bbs_View_recommend_API_Result> call, Response<Bbs_View_recommend_API_Result> response) {
                        if (response.body().getRec_mark() == false) {
                            rec_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bbs_view_activity_thumbs_up_activated, 0, 0, 0);
                            rec_btn.setText(" 추천(" + response.body().getRec_amount() + ")");
                        } else {
                            rec_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bbs_view_activity_thumbs_up_inactivated, 0, 0, 0);
                            rec_btn.setText(" 추천(" + response.body().getRec_amount() + ")");
                        }
                    }

                    @Override
                    public void onFailure(Call<Bbs_View_recommend_API_Result> call, Throwable t) {
                    }
                });
                break;

            case R.id.comment_reg_btn: // 댓글 등록 버튼
                comment = e_comment.getText().toString().trim();
                s_bbs_user = user.getUser_id();
                final Call<Bbs_Comment_Api_Result> com_result = service.request_Bbs_comment(request_no, s_bbs_user, comment);
                com_result.enqueue(new Callback<Bbs_Comment_Api_Result>() {
                    @Override
                    public void onResponse(Call<Bbs_Comment_Api_Result> call, Response<Bbs_Comment_Api_Result> response) {
                        e_comment.setText("");
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        comment_list.clear();
                        view_comment();
                    }

                    @Override
                    public void onFailure(Call<Bbs_Comment_Api_Result> call, Throwable t) {
                    }
                });
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void view_content() { // 해당 게시글의 내용을 보여주는 메소드
        Call<Bbs_View_content_API_Result> result = service.request_Bbs_content_view(request_no, user.getUser_id());
        result.enqueue(new Callback<Bbs_View_content_API_Result>() {
            @Override
            public void onResponse(Call<Bbs_View_content_API_Result> call, Response<Bbs_View_content_API_Result> response) {
                s_bbs_user = response.body().getUser();
                s_bbs_subject = response.body().getSubject();
                s_bbs_content = response.body().getContent();
                getSupportActionBar().setTitle(s_bbs_subject); // 제목 표시
                getSupportActionBar().setSubtitle(s_bbs_user + " / " + response.body().getTime()); // 게시글 등록 시간 표시
                bbs_content.setText(s_bbs_content); // 내용 표시
                if (response.body().getImage() != null) { // 이미지가 존재 한다면
                    picture_path = response.body().getImage();
                    Glide.with(getApplicationContext()).load(URLs.BBS_IMAGE$VIDEO_URL + picture_path).into(bbs_image); // 이미지를 Glide를 통해 표시
                } else {
                    bbs_image.setVisibility(GONE); // 이미지가 없다면 ImageView를 제거
                }
                if (response.body().getVideo() != null) { // 비디오 경로가 존재한다면
                    video_file_path = response.body().getVideo();
                    video_play(video_file_path); // 비디오 플레이어 실행
                } else {
                    bbs_video.setVisibility(GONE); // 아닌 경우, 비디오 뷰 없앰
                }
                if (!s_bbs_user.equals(user.getUser_id())) { // 게시글의 유저와 클라이언트 유저 아이디와 같은 경우
                    options_menu = true; // 옵션 메뉴 활성화
                    invalidateOptionsMenu(); // 툴바를 invalidate를 통해 새로 고침
                }
                if (response.body().getRec() == true) {
                    rec_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bbs_view_activity_thumbs_up_activated, 0, 0, 0);
                } else {
                    rec_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bbs_view_activity_thumbs_up_inactivated, 0, 0, 0);
                }
                rec_btn.setText(" 추천(" + response.body().getRec_amt() + ")");
            }

            @Override
            public void onFailure(Call<Bbs_View_content_API_Result> call, Throwable t) {
            }
        });
    }

    private void view_comment() { // 해당 글에 댓글을 보여주는 메소드
        Call<List<Bbs_Comment_view_API_Result>> call = service.view_Bbs_comment(request_no);
        call.enqueue(new Callback<List<Bbs_Comment_view_API_Result>>() {
            @Override
            public void onResponse(Call<List<Bbs_Comment_view_API_Result>> call, Response<List<Bbs_Comment_view_API_Result>> response) {
                adapter = new Bbs_Comment_View_adapter(comment_list, getApplicationContext());
                comment_list.addAll(response.body());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Bbs_Comment_view_API_Result>> call, Throwable t) {

            }
        });
    }

    private void video_play(String url) { // DB에 저장된 URL을 통해 받아온 비디오 경로를 내장된 비디오 플레이어로 실행함.
        video_path = URLs.BBS_IMAGE$VIDEO_URL + url;
        MediaController video_control = new MediaController(Bbs_view_activity.this); // getapplicationContext는 하면 오류 뜸.
        video_control.setAnchorView(bbs_video);
        bbs_video.setVideoPath(video_path);
        bbs_video.setMediaController(video_control);
        bbs_video.requestFocus();
        bbs_video.start();
    }

    @Subscribe // otto를 사용하여 어댑터에서 이벤트 리스너를 통해 받아옴
    public void delete_com(String string) {
        Call<List<Bbs_Comment_view_API_Result>> call = service.view_Bbs_comment(request_no);
        call.enqueue(new Callback<List<Bbs_Comment_view_API_Result>>() {
            @Override
            public void onResponse(Call<List<Bbs_Comment_view_API_Result>> call, Response<List<Bbs_Comment_view_API_Result>> response) {
                adapter = new Bbs_Comment_View_adapter(comment_list, getApplicationContext());
                comment_list.clear();
                comment_list.addAll(response.body());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Bbs_Comment_view_API_Result>> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // 툴바 옵션 메뉴 생성 메소드
        MenuInflater menuInflater = getMenuInflater(); // 메뉴 인플레이터를 이용하여 메뉴를 툴바에 붙임
        menuInflater.inflate(R.menu.bbs_view_toolbar_menu, menu);
        MenuItem menu_modify = menu.findItem(R.id.toolbar_bbs_view_modify_btn);
        MenuItem menu_delete = menu.findItem(R.id.toolbar_bbs_view_delete_btn);
        if (options_menu == false) { // 게시글에 등록된 아이디와 내 아이디와 같은 경우 수정과 삭제버튼이 표시되도록
            menu_delete.setVisible(true);
            menu_modify.setVisible(true);
        } else { // 게시글에 등록된 아이디와 내 아이디와 같은 경우 수정과 삭제버튼이 보이지 않도록
            menu_delete.setVisible(false);
            menu_modify.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 게시글 수정과 삭제를 나타내는 툴바 옵션 메뉴를 선택했을 때
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 버튼
                onBackPressed();
                return true;
            case R.id.toolbar_bbs_view_modify_btn: // 수정 버튼을 눌렀을 때
                Intent intent = new Intent(getApplicationContext(), Bbs_modify_activity.class);
                intent.putExtra("choice_no", request_no);
                intent.putExtra("subject", s_bbs_subject);
                intent.putExtra("content", s_bbs_content);
                intent.putExtra("video_file_path", video_file_path);
                intent.putExtra("picture_path", picture_path);
                startActivity(intent);
                return true;
            case R.id.toolbar_bbs_view_delete_btn: // 삭제 버튼을 눌렀을 때
                builder.setIcon(R.drawable.delete_btn);
                builder.setTitle("삭제");
                builder.setMessage("게시물을 삭제하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Call<Bbs_View_delete_API_Result> result = service.request_Bbs_content_delete(request_no);
                        result.enqueue(new Callback<Bbs_View_delete_API_Result>() {
                            @Override
                            public void onResponse(Call<Bbs_View_delete_API_Result> call, Response<Bbs_View_delete_API_Result> response) {
                                finish();
                            }

                            @Override
                            public void onFailure(Call<Bbs_View_delete_API_Result> call, Throwable t) {
                            }
                        });
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.create();
                builder.show();
                return true;
        }
        return true;
    }
}