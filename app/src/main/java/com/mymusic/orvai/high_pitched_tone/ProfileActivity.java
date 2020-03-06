package com.mymusic.orvai.high_pitched_tone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;
import com.mymusic.orvai.high_pitched_tone.models.URLs;
import com.mymusic.orvai.high_pitched_tone.models.User;

public class ProfileActivity extends AppCompatActivity {

    TextView p_user_id, p_user_name;
    Button logout;
    ImageView p_user_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        p_user_id = (TextView)findViewById(R.id.p_user_id);
        p_user_name = (TextView)findViewById(R.id.p_user_name);
        p_user_image = (ImageView)findViewById(R.id.profile_image);

        logout = (Button)findViewById(R.id.logout);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        User user = SharedPrefManager.getInstance(this).getUser();

        p_user_id.setText(String.valueOf(user.getUser_id()));

        p_user_name.setText(String.valueOf(user.getUser_name()));

        Glide.with(getApplicationContext()).load(URLs.URL_IMAGE_ROOT+user.getUser_pic()).into(p_user_image);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
