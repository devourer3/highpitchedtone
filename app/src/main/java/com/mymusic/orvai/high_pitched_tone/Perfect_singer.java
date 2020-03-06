package com.mymusic.orvai.high_pitched_tone;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.mymusic.orvai.high_pitched_tone.CustomView.Pitch_Custom_View;
import com.mymusic.orvai.high_pitched_tone.EventBus.Event_bus_score;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class Perfect_singer extends AppCompatActivity {

    Context mCtx;
    AudioDispatcher dispatcher;
    PitchDetectionHandler pitchDetectionHandler;
    AudioProcessor audioProcessor;
    Handler handler;
    TextView perfect_score;
    float pitchInHz;
    public static int HANDLING_PITCH = 5100;
    Thread dispatcher_thread, auto_scroll;
    ScrollView scrollView;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perpect_singer);
        dispatcher_constructor();
        handler = new Handler();
        scrollView = findViewById(R.id.pitch_scroll);
        videoView = findViewById(R.id.karaoke_video);
        perfect_score = findViewById(R.id.perfect_singer_score);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.good_old_days);
        videoView.setVideoURI(uri);
        videoView.start();
        dispatcher_thread.start();
        auto_scroll_method();
        auto_scroll.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void score_mark(Event_bus_score event_bus_score) {
        perfect_score.setText("음정점수: "+event_bus_score.score/130+" / 100");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        if(!dispatcher.isStopped()) {
            dispatcher.stop();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void auto_scroll_method() {
        auto_scroll = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!(HANDLING_PITCH == 5250))
                        scrollView.smoothScrollTo(0, HANDLING_PITCH - 400);
                }
            }
        });
    }

    private void dispatcher_constructor() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        pitchDetectionHandler = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                pitchInHz = pitchDetectionResult.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        get_pitch(pitchInHz);
                    }
                });
            }
        };
        audioProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pitchDetectionHandler);
        dispatcher.addAudioProcessor(audioProcessor);
        dispatcher_thread = new Thread(dispatcher);
    }

    public void get_pitch(float pitchInHz) {
        if (pitchInHz >= 65.41 && pitchInHz <= 69.29) { // 0옥 도
            HANDLING_PITCH = 4850;
        } else if (pitchInHz >= 69.30 && pitchInHz <= 73.41) { // 0옥 도#
            HANDLING_PITCH = 4750;
        } else if (pitchInHz >= 73.42 && pitchInHz <= 77.77) { // 0옥 레
            HANDLING_PITCH = 4650;
        } else if (pitchInHz >= 77.78 && pitchInHz <= 82.40) { // 0옥 레#
            HANDLING_PITCH = 4550;
        } else if (pitchInHz >= 82.41 && pitchInHz <= 87.30) { // 0옥 미
            HANDLING_PITCH = 4450;
        } else if (pitchInHz >= 87.31 && pitchInHz <= 92.49) { // 0옥 파
            HANDLING_PITCH = 4350;
        } else if (pitchInHz >= 92.50 && pitchInHz <= 97.99) { // 0옥 파#
            HANDLING_PITCH = 4250;
        } else if (pitchInHz >= 98.00 && pitchInHz <= 103.82) { // 0옥 솔
            HANDLING_PITCH = 4150;
        } else if (pitchInHz >= 103.83 && pitchInHz <= 109.99) { // 0옥 솔#
            HANDLING_PITCH = 4050;
        } else if (pitchInHz >= 110.00 && pitchInHz <= 116.53) { // 0옥 라
            HANDLING_PITCH = 3950;
        } else if (pitchInHz >= 116.54 && pitchInHz <= 123.46) { // 0옥 라#
            HANDLING_PITCH = 3850;
        } else if (pitchInHz >= 123.47 && pitchInHz <= 130.80) { // 0옥 시
            HANDLING_PITCH = 3750;
        } else if (pitchInHz >= 130.81 && pitchInHz <= 138.58) { // 1옥 도
            HANDLING_PITCH = 3650;
        } else if (pitchInHz >= 138.59 && pitchInHz <= 146.82) { // 1옥 도#
            HANDLING_PITCH = 3550;
        } else if (pitchInHz >= 146.83 && pitchInHz <= 155.55) { // 1옥 레
            HANDLING_PITCH = 3450;
        } else if (pitchInHz >= 155.56 && pitchInHz <= 164.80) { // 1옥 레#
            HANDLING_PITCH = 3350;
        } else if (pitchInHz >= 164.81 && pitchInHz <= 174.60) { // 1옥 미
            HANDLING_PITCH = 3250;
        } else if (pitchInHz >= 174.61 && pitchInHz <= 184.99) { // 1옥 파
            HANDLING_PITCH = 3150;
        } else if (pitchInHz >= 185.00 && pitchInHz <= 195.99) { // 1옥 파#
            HANDLING_PITCH = 3050;
        } else if (pitchInHz >= 196.00 && pitchInHz <= 207.64) { // 1옥 솔
            HANDLING_PITCH = 2950;
        } else if (pitchInHz >= 207.65 && pitchInHz <= 219.99) { // 1옥 솔#
            HANDLING_PITCH = 2850;
        } else if (pitchInHz >= 220.00 && pitchInHz <= 233.07) { // 1옥 라
            HANDLING_PITCH = 2750;
        } else if (pitchInHz >= 233.08 && pitchInHz <= 246.93) { // 1옥 라#
            HANDLING_PITCH = 2650;
        } else if (pitchInHz >= 246.94 && pitchInHz <= 261.62) { // 1옥 시
            HANDLING_PITCH = 2550;
        } else if (pitchInHz >= 261.63 && pitchInHz <= 277.17) { // 2옥 도
            HANDLING_PITCH = 2450;
        } else if (pitchInHz >= 277.18 && pitchInHz <= 293.65) { // 2옥 도#
            HANDLING_PITCH = 2350;
        } else if (pitchInHz >= 293.66 && pitchInHz <= 311.12) { // 2옥 레
            HANDLING_PITCH = 2250;
        } else if (pitchInHz >= 311.13 && pitchInHz <= 329.62) { // 2옥 레#
            HANDLING_PITCH = 2150;
        } else if (pitchInHz >= 329.63 && pitchInHz <= 349.22) { // 2옥 미
            HANDLING_PITCH = 2050;
        } else if (pitchInHz >= 347.23 && pitchInHz <= 369.98) { // 2옥 파
            HANDLING_PITCH = 1950;
        } else if (pitchInHz >= 369.99 && pitchInHz <= 391.99) { // 2옥 파#
            HANDLING_PITCH = 1850;
        } else if (pitchInHz >= 392.00 && pitchInHz <= 415.29) { // 2옥 솔
            HANDLING_PITCH = 1750;
        } else if (pitchInHz >= 415.30 && pitchInHz <= 439.99) { // 2옥 솔#
            HANDLING_PITCH = 1650;
        } else if (pitchInHz >= 440.00 && pitchInHz <= 466.15) { // 2옥 라
            HANDLING_PITCH = 1550;
        } else if (pitchInHz >= 466.16 && pitchInHz <= 493.87) { // 2옥 라#
            HANDLING_PITCH = 1450;
        } else if (pitchInHz >= 493.88 && pitchInHz <= 523.24) { // 2옥 시
            HANDLING_PITCH = 1350;
        } else if (pitchInHz >= 523.25 && pitchInHz <= 554.36) { // 3옥 도
            HANDLING_PITCH = 1250;
        } else if (pitchInHz >= 554.37 && pitchInHz <= 587.32) { // 3옥 도#
            HANDLING_PITCH = 1150;
        } else if (pitchInHz >= 587.33 && pitchInHz <= 622.24) { // 3옥 레
            HANDLING_PITCH = 1050;
        } else if (pitchInHz >= 622.25 && pitchInHz <= 659.24) { // 3옥 레#
            HANDLING_PITCH = 950;
        } else if (pitchInHz >= 659.25 && pitchInHz <= 698.45) { // 3옥 미
            HANDLING_PITCH = 850;
        } else if (pitchInHz >= 698.46 && pitchInHz <= 739.98) { // 3옥 파
            HANDLING_PITCH = 750;
        } else if (pitchInHz >= 739.99 && pitchInHz <= 783.98) { // 3옥 파#
            HANDLING_PITCH = 650;
        } else if (pitchInHz >= 783.99 && pitchInHz <= 830.60) { // 3옥 솔
            HANDLING_PITCH = 550;
        } else if (pitchInHz >= 830.61 && pitchInHz <= 879.99) { // 3옥 솔#
            HANDLING_PITCH = 450;
        } else if (pitchInHz >= 880.00 && pitchInHz <= 932.32) { // 3옥 라
            HANDLING_PITCH = 350;
        } else if (pitchInHz >= 932.33 && pitchInHz <= 987.76) { // 3옥 라#
            HANDLING_PITCH = 250;
        } else if (pitchInHz >= 987.77 && pitchInHz <= 1046.49) { // 3옥 시
            HANDLING_PITCH = 150;
        } else if (pitchInHz >= 1046.50 && pitchInHz <= 1108.72) { // 4옥 도
            HANDLING_PITCH = 50;
        } else {
            HANDLING_PITCH = 5250;
        }
    }
}