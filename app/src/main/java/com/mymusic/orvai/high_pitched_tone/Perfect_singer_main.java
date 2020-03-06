package com.mymusic.orvai.high_pitched_tone;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mymusic.orvai.high_pitched_tone.EventBus.Event_bus_musicplay;
import com.mymusic.orvai.high_pitched_tone.adapters.Perfect_singer_adapter;
import com.mymusic.orvai.high_pitched_tone.models.Song_list;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Perfect_singer_main extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Song_list> song_lists;
    LinearLayout music_player;
    MediaPlayer mediaPlayer;
    TextView music_title;
    ImageView music_play_image;
    Handler handler;
    FloatingActionButton fab_sing_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perpect_singer_main);
        findViewById(R.id.fab_back_btn).setOnClickListener(this);
        fab_sing_btn = findViewById(R.id.fab_letssing);
        fab_sing_btn.setOnClickListener(this);
        findViewById(R.id.music_stop_btn).setOnClickListener(this);
        music_player = findViewById(R.id.music_player);
        music_title = findViewById(R.id.song_play_title);
        recyclerView = findViewById(R.id.song_list);
        music_play_image = findViewById(R.id.play_gif);
        song_lists = new ArrayList<>();
        handler = new Handler();
        adapter = new Perfect_singer_adapter(song_lists, getApplicationContext());
        mediaPlayer = new MediaPlayer();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        song_lists.add(new Song_list(this, R.drawable.perfect_singer_activity_gooldolddays, "그날처럼", "장덕철", R.raw.good_old_days_music));
        song_lists.add(new Song_list(this, R.drawable.perfect_singer_activity_onelove, "ONE LOVE", "M.C THE MAX", R.raw.one_love_music));
        song_lists.add(new Song_list(this, R.drawable.perfect_singer_activity_emptiness_in_memory, "기억의 빈자리", "나얼", R.raw.emptiness_in_memory_music));
        song_lists.add(new Song_list(this, R.drawable.perfect_singer_activity_gift, "선물", "멜로망스", R.raw.gift_music));
        song_lists.add(new Song_list(this, R.drawable.perfect_singer_activity_nowhere, "어디에도", "M.C THE MAX", R.raw.no_matter_where_music));
        recyclerView.setAdapter(adapter);
        music_player.setVisibility(View.INVISIBLE);
        fab_sing_btn.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recycler_music_play(final Event_bus_musicplay event_bus_musicplay) {
        music_player.setVisibility(View.VISIBLE);
        fab_sing_btn.setVisibility(View.VISIBLE);
        music_title.setText(event_bus_musicplay.music_title);
        if(!mediaPlayer.isPlaying()) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), event_bus_musicplay.music_data);
            Glide.with(getApplicationContext()).asGif().load(R.drawable.perfect_singer_activity_music_playing).into(music_play_image);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    music_player.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), event_bus_musicplay.music_data);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    music_player.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_back_btn:
                onBackPressed();
                break;
            case R.id.fab_letssing:
                mediaPlayer.stop();
                music_player.setVisibility(View.INVISIBLE);
                fab_sing_btn.setVisibility(View.INVISIBLE);
                startActivity(new Intent(getApplicationContext(), Perfect_singer.class));
                break;
            case R.id.music_stop_btn:
                adapter.notifyDataSetChanged();
                fab_sing_btn.setVisibility(View.INVISIBLE);
                music_player.setVisibility(View.INVISIBLE);
                mediaPlayer.stop();
        }
    }
}
