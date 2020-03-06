package com.mymusic.orvai.high_pitched_tone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_View_modify_API_Result;
import com.mymusic.orvai.high_pitched_tone.Interface.API_Service;
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;
import com.mymusic.orvai.high_pitched_tone.Utils.FileUtils;
import com.mymusic.orvai.high_pitched_tone.models.URLs;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Bbs_modify_activity extends AppCompatActivity {

    Button reg_btn;
    EditText bbs_e_subject, bbs_e_content;
    String bbs_subject, bbs_content, bbs_user, bbs_no, picture_path, video_file_path;
    ImageView img_btn, video_btn;
    TextView pic_exist_txt, video_exist_txt;
    File file, file1 = null;
    Uri imageUri, videoUri = null;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_modify);

        toolbar = findViewById(R.id.bbs_modify_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        img_btn = findViewById(R.id.bbs_image_view);
        video_btn = findViewById(R.id.bbs_video_thumb);
        bbs_e_subject = findViewById(R.id.bbs_subject);
        bbs_e_content = findViewById(R.id.bbs_content);
        pic_exist_txt = findViewById(R.id.bbs_modify_picture_exist);
        video_exist_txt = findViewById(R.id.bbs_modify_video_exist);

        Intent intent = getIntent();

        bbs_e_subject.setText(intent.getStringExtra("subject"));
        bbs_e_content.setText(intent.getStringExtra("content"));
        bbs_no = intent.getStringExtra("choice_no");
        picture_path = intent.getStringExtra("picture_path");
        video_file_path = intent.getStringExtra("video_file_path");

        if(!picture_path.equals("null")){
            pic_exist_txt.setTextColor(Color.BLUE);
            pic_exist_txt.setText(picture_path);
            Glide.with(getApplicationContext()).load(URLs.BBS_IMAGE$VIDEO_URL+picture_path).into(img_btn);
        } else {
            pic_exist_txt.setTextColor(Color.RED);
            pic_exist_txt.setText("사진 없음");
        }

        if(!video_file_path.equals("null")){
            video_exist_txt.setTextColor(Color.BLUE);
            video_exist_txt.setText(video_file_path);
        } else {
            video_exist_txt.setTextColor(Color.RED);
            video_exist_txt.setText("비디오 없음");
            video_btn.setImageResource(R.drawable.bbs_modify_activity_video_unexist);
        }

        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });

        video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent v = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(v, 200);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            file = FileUtils.getFile(getApplicationContext(), imageUri);
            pic_exist_txt.setTextColor(Color.BLUE);
            pic_exist_txt.setText(file.getName());
            img_btn.setImageURI(imageUri);
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            file1 = FileUtils.getFile(getApplicationContext(), videoUri);
            video_exist_txt.setTextColor(Color.BLUE);
            video_exist_txt.setText(file1.getName());
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file1.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            video_btn.setImageBitmap(bitmap);
        }
    }


    private void bbs_registration_i_v() { // retrofit2 구동. 사진과 동영상 다 올릴 경우.

        bbs_subject = bbs_e_subject.getText().toString().trim();
        bbs_content = bbs_e_content.getText().toString().trim();
        bbs_user = SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_id();

        RequestBody requestfile = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part upload_file = MultipartBody.Part.createFormData("Bbs_Image", file.getName(), requestfile);

        RequestBody requestfile1 = RequestBody.create(MediaType.parse("*/*"), file1);
        MultipartBody.Part upload_file1 = MultipartBody.Part.createFormData("Bbs_Video", file1.getName(), requestfile1);

        RequestBody request_no = RequestBody.create(MediaType.parse("text/plain"), bbs_no);
        RequestBody request_subject = RequestBody.create(MediaType.parse("text/plain"), bbs_subject);
        RequestBody request_content = RequestBody.create(MediaType.parse("text/plain"), bbs_content);

        Retrofit retrofit = new Retrofit.Builder() // 레트로핏2 객체 생성
                .baseUrl(URLs.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API_Service service = retrofit.create(API_Service.class); // API 서비스(인터페이스) 등록

        Call<Bbs_View_modify_API_Result> call = service.request_Bbs_content_modify(request_no, request_subject, request_content,
                upload_file,
                upload_file1);


        call.enqueue(new Callback<Bbs_View_modify_API_Result>() {
            @Override
            public void onResponse(Call<Bbs_View_modify_API_Result> call, Response<Bbs_View_modify_API_Result> response) {
                Toast.makeText(getApplicationContext(), "수정 완료.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Bbs_View_modify_API_Result> call, Throwable t) {
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.toolbar_bbs_modify_btn:
                bbs_registration_i_v();
                return true;

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bbs_modify_toolbar_menu, menu);
        return true;
    }
}
