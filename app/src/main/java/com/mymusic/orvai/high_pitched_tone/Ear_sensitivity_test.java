package com.mymusic.orvai.high_pitched_tone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class Ear_sensitivity_test extends AppCompatActivity {

    private SoundPool soundPool;
    private int min_pitch, max_pitch, ran_pitch, pitch_score;
    int[] pitch;
    private double[] input_pitch_min, input_pitch_max;
    String[] pitch_name;
    // 1옥타브 도에서부터 4옥타브 도까지 저장할 수 있는 String 배열
    private float pitchInHz;
    private Thread dispatcher_thread;
    private AudioDispatcher dispatcher;
    private PitchDetectionHandler pitchDetectionHandler;
    private AudioProcessor audioProcessor;
    AlertDialog.Builder builder;
    private TextView timer;
    private ImageView note_image, note_image2;
    Context mCtx;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = getApplicationContext();
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ear_sensitivity_test);
        timer = findViewById(R.id.test_timer);
        note_image = findViewById(R.id.test_note);
        note_image2 = findViewById(R.id.test_note2);
        builder = new AlertDialog.Builder(Ear_sensitivity_test.this, R.style.Theme_AppCompat_Light_Dialog);
        pitch = new int[37];
        pitch_name = new String[]{"1옥타브 도", "1옥타브 도#", "1옥타브 레", "1옥타브 레#", "1옥타브 미", "1옥타브 파", "1옥타브 파#", "1옥타브 솔", "1옥타브 솔#", "1옥타브 라", "1옥타브 라#", "1옥타브 시",
                "2옥타브 도", "2옥타브 도#", "2옥타브 레", "2옥타브 레#", "2옥타브 미", "2옥타브 파", "2옥타브 파#", "2옥타브 솔", "2옥타브 솔#", "2옥타브 라", "2옥타브 라#", "2옥타브 시",
                "3옥타브 도", "3옥타브 도#", "3옥타브 레", "3옥타브 레#", "3옥타브 미", "3옥타브 파", "3옥타브 파#", "3옥타브 솔", "3옥타브 솔#", "3옥타브 라", "3옥타브 라#", "3옥타브 시", "4옥타브 도"};
        random = new Random();
        input_pitch_min = new double[]{125.81, 133.59, 141.83, 150.56, 159.81, 169.61, 180.00, 191.00, 202.65, 215.00, 228.08, 241.94, 256.63, 272.18, 288.66, 306.13, 326.63, 347.23, 369.99, 392.00, 415.30, 440.00, 466.16, 493.88, 523.25, 554.37, 587.33, 622.25, 659.25, 698.46, 739.99, 783.99, 830.61, 880.00, 932.33, 987.77, 1046.50};
        input_pitch_max = new double[]{133.58, 141.82, 150.55, 159.80, 169.60, 179.99, 190.99, 202.64, 214.99, 228.07, 241.93, 256.62, 272.17, 288.65, 306.12, 326.62, 347.22, 369.98, 391.99, 415.29, 439.99, 466.15, 493.87, 523.24, 554.36, 587.32, 622.24, 659.24, 698.45, 739.98, 783.98, 830.60, 879.99, 932.32, 987.76, 1046.49, 1108.72};
        pitch_score = 100;
        Intent intent = getIntent();
        min_pitch = intent.getIntExtra("min_pitch", 0);
        max_pitch = intent.getIntExtra("max_pitch", 0);
        ran_pitch = random.nextInt(max_pitch - min_pitch) + 1 + min_pitch;

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        pitchDetectionHandler = new PitchDetectionHandler() { // 마이크에서 입력된 음정들을 실시간으로 Hz로 표시해주는 핸들러
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                pitchInHz = pitchDetectionResult.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        process_Pitch(pitchInHz);
                    }
                });
            }
        };
        audioProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pitchDetectionHandler); // 알고리즘을 통하여 샘플링 된 음성들을 Hz화 시켜 오디오 프로세서에 할당
        dispatcher.addAudioProcessor(audioProcessor); // dispatcher에 오디오 프로세서 추가

        dispatcher_thread = new Thread(dispatcher);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { // min_sdk 버전이 롤리팝 버전 이하일 경우
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 1); // SoundPool 구버전(deprecated)을 씀
        } else {
            soundPool = new SoundPool.Builder().setMaxStreams(10).build(); // 롤리팝 이후 버전일 경우, 신형 현 SoundPool을 씀.
        }
        sound_pool_registration();

        test_start();

    }

    private void sound_pool_registration() {
        pitch[0] = soundPool.load(mCtx, R.raw.do_1oc, 1);
        pitch[1] = soundPool.load(mCtx, R.raw.do_sharp_1oc, 1);
        pitch[2] = soundPool.load(mCtx, R.raw.re_1oc, 1);
        pitch[3] = soundPool.load(mCtx, R.raw.re_sharp_1oc, 1);
        pitch[4] = soundPool.load(mCtx, R.raw.mi_1oc, 1);
        pitch[5] = soundPool.load(mCtx, R.raw.fa_1oc, 1);
        pitch[6] = soundPool.load(mCtx, R.raw.fa_sharp_1oc, 1);
        pitch[7] = soundPool.load(mCtx, R.raw.sol_1oc, 1);
        pitch[8] = soundPool.load(mCtx, R.raw.sol_sharp_1oc, 1);
        pitch[9] = soundPool.load(mCtx, R.raw.la_1oc, 1);
        pitch[10] = soundPool.load(mCtx, R.raw.la_sharp_1oc, 1);
        pitch[11] = soundPool.load(mCtx, R.raw.si_1oc, 1);

        pitch[12] = soundPool.load(mCtx, R.raw.do_2oc, 1);
        pitch[13] = soundPool.load(mCtx, R.raw.do_sharp_2oc, 1);
        pitch[14] = soundPool.load(mCtx, R.raw.re_2oc, 1);
        pitch[15] = soundPool.load(mCtx, R.raw.re_sharp_2oc, 1);
        pitch[16] = soundPool.load(mCtx, R.raw.mi_2oc, 1);
        pitch[17] = soundPool.load(mCtx, R.raw.fa_2oc, 1);
        pitch[18] = soundPool.load(mCtx, R.raw.fa_sharp_2oc, 1);
        pitch[19] = soundPool.load(mCtx, R.raw.sol_2oc, 1);
        pitch[20] = soundPool.load(mCtx, R.raw.sol_sharp_2oc, 1);
        pitch[21] = soundPool.load(mCtx, R.raw.la_2oc, 1);
        pitch[22] = soundPool.load(mCtx, R.raw.la_sharp_2oc, 1);
        pitch[23] = soundPool.load(mCtx, R.raw.si_2oc, 1);

        pitch[24] = soundPool.load(mCtx, R.raw.do_3oc, 1);
        pitch[25] = soundPool.load(mCtx, R.raw.do_sharp_3oc, 1);
        pitch[26] = soundPool.load(mCtx, R.raw.re_3oc, 1);
        pitch[27] = soundPool.load(mCtx, R.raw.re_sharp_3oc, 1);
        pitch[28] = soundPool.load(mCtx, R.raw.mi_3oc, 1);
        pitch[29] = soundPool.load(mCtx, R.raw.fa_3oc, 1);
        pitch[30] = soundPool.load(mCtx, R.raw.fa_sharp_3oc, 1);
        pitch[31] = soundPool.load(mCtx, R.raw.sol_3oc, 1);
        pitch[32] = soundPool.load(mCtx, R.raw.sol_sharp_3oc, 1);
        pitch[33] = soundPool.load(mCtx, R.raw.la_3oc, 1);
        pitch[34] = soundPool.load(mCtx, R.raw.la_sharp_3oc, 1);
        pitch[35] = soundPool.load(mCtx, R.raw.si_3oc, 1);

        pitch[36] = soundPool.load(mCtx, R.raw.do_4oc, 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!dispatcher.isStopped())
            dispatcher.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!dispatcher.isStopped())
            dispatcher.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!dispatcher.isStopped())
            dispatcher.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void process_Pitch(float pitchInHz) { // 실시간으로 출력되는 Hz를 실시간으로 음계로 바꾸어 주는 메소드
        Log.d("kb점수", String.valueOf(pitch_score));
        if (pitchInHz < input_pitch_min[ran_pitch] || pitchInHz > input_pitch_max[ran_pitch]) {
            pitch_score--;
        }
    }

    private void test_start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 3; i > 0; i--) {
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finalI > 0)
                                timer.setText(finalI + "초 후,\n\n 무작위로 하나의 음이 재생됩니다.\n\n이를 듣고 맞추어 보세요!");
                            else {
                                timer.setText("");
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                play_pitch();
            }
        }).start();
    }

    private void play_pitch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        note_image.setImageResource(R.drawable.ear_test_note);
                        note_image2.setImageResource(R.drawable.ear_test_note2);
                    }
                });
                soundPool.play(pitch[ran_pitch], 1, 1, 1, 0, 1);
                for (int i = 3; i > 0; i--) {
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timer.setText(finalI + "초 후,\n\n 들으신 음을\n\n 3초동안 목소리로 내주세요!");
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                input_voice();
            }
        }).start();
    }

    private void input_voice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        note_image.setVisibility(View.INVISIBLE);
                        note_image2.setVisibility(View.INVISIBLE);
                        timer.setText("들으신 음을 내주세요!\n(3초 남았습니다.)");
                        timer.setTextSize(25);
                        timer.setTextColor(Color.RED);
                        timer.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                });
                dispatcher_thread.setDaemon(true);
                dispatcher_thread.start();
                for (int i = 3; i > 0; i--) {
                    try {
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timer.setText("들으신 음을 내주세요!\n("+finalI+"초 남았습니다.)");
                                timer.setTextSize(25);
                                timer.setTextColor(Color.RED);
                                timer.setTypeface(Typeface.DEFAULT_BOLD);
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                dispatcher.stop();
                if (pitch_score >= 80) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pitch_correct();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pitch_incorrect();
                        }
                    });
                }
            }
        }).start();
    }

    private void pitch_correct() {
        builder.setTitle("일치!").setMessage("[" + pitch_name[ran_pitch] + "] 와 일치합니다!").setIcon(R.drawable.correct_sign);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create();
        builder.show();
    }

    private void pitch_incorrect() {
        builder.setTitle("불일치!").setMessage("[" + pitch_name[ran_pitch] + "] 와 일치하지 않습니다!").setIcon(R.drawable.incorrect_sign);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create();
        builder.show();
    }

}
