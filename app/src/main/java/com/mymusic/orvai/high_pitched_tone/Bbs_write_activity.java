package com.mymusic.orvai.high_pitched_tone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.mymusic.orvai.high_pitched_tone.Interface.API_Service;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_Write_API_Result;
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

public class Bbs_write_activity extends AppCompatActivity {

    EditText bbs_e_subject, bbs_e_content;
    String bbs_subject, bbs_content, bbs_user;
    ImageView img_btn, video_btn;
    File file, file1 = null;
    Uri imageUri, videoUri = null;
    Toolbar toolbar;
    TextView pic_txt, video_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_write);

        toolbar = findViewById(R.id.bbs_toolbar);
        img_btn = findViewById(R.id.bbs_image_view);
        video_btn = findViewById(R.id.bbs_video_thumb);
        bbs_e_subject = findViewById(R.id.bbs_subject);
        bbs_e_content = findViewById(R.id.bbs_content);
        pic_txt = findViewById(R.id.pic_title);
        video_txt = findViewById(R.id.video_title);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
            img_btn.setImageURI(imageUri);
            pic_txt.setText(file.getName());
        } else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            file1 = FileUtils.getFile(getApplicationContext(), videoUri);
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file1.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            video_txt.setText(file1.getName());
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

        RequestBody request_subject = RequestBody.create(MediaType.parse("text/plain"), bbs_subject);
        RequestBody request_content = RequestBody.create(MediaType.parse("text/plain"), bbs_content);
        RequestBody request_user = RequestBody.create(MediaType.parse("text/plain"), bbs_user);

        Retrofit retrofit = new Retrofit.Builder() // 레트로핏2 객체 생성
                .baseUrl(URLs.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API_Service service = retrofit.create(API_Service.class); // API 서비스(인터페이스) 등록

        Call<Bbs_Write_API_Result> call = service.bbs_registration( // Converter를 통한 Gson값을 result(JSON)에 담는다. 제목, 내용, 이름, 이미지, 비디오
                request_subject,
                request_content,
                request_user,
                upload_file,
                upload_file1);

        call.enqueue(new Callback<Bbs_Write_API_Result>() { // php에 보내기 위한 큐 등록
            @Override
            public void onResponse(Call<Bbs_Write_API_Result> call, Response<Bbs_Write_API_Result> response) { // php에 전송 후, 등록 완료 될 때
                bbs_e_subject.setText("");
                bbs_e_content.setText("");
                Toast.makeText(getApplicationContext(), "등록 완료!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Bbs_Write_API_Result> call, Throwable t) { // php에 전송 후, 등록 실패 될 때
                Toast.makeText(getApplicationContext(), "등록 실패!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.toolbar_bbs_write_btn:
                bbs_registration_i_v();
                return true;

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bbs_write_toolbar_menu, menu);
        return true;
    }


}
