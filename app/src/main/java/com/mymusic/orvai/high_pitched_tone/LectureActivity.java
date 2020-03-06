package com.mymusic.orvai.high_pitched_tone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class LectureActivity extends YouTubeBaseActivity {

    YouTubePlayerView youtube;
    Button play_btn;
    YouTubePlayer.OnInitializedListener listener;

    String youtube_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);

        play_btn = findViewById(R.id.play_btn);
        youtube = findViewById(R.id.youtube_view);


        Intent intent = getIntent();

        youtube_url = intent.getStringExtra("youtube_url");


        listener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(youtube_url); // 유튜브 주소
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        };

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youtube.initialize("AIzaSyC3j5PhmXsbvakaxupo251GKv2gYDNSjEk", listener);
                play_btn.setVisibility(View.GONE);
            }
        });

    }
}
