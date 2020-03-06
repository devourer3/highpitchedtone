package com.mymusic.orvai.high_pitched_tone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class Pitch_ear_test_Activity extends AppCompatActivity implements View.OnClickListener {

    SoundPool soundPool;
    // Raw폴더내에 저장된 피아노 소리를 관리하기 위한 soundpool 변수
    TextView pitch_min_txt, pitch_max_txt, note;
    // 최저음 텍스트뷰, 최고음 텍스트뷰, 실시간 음계 텍스트뷰 변수
    Context mCtx;
    // 컨텍스트 변수
    int[] pitch;
    // Raw 폴더 내에 저장된 효과음을 저장할 수 있는 배열
    int pitch_min, pitch_max;
    // 사용자가 실시간으로 조정하는 최저음과 최고음 int 변수
    float pitchInHz;
    // 실시간 음정 Hz를 소수점으로 나타내기 위한 변수
    AudioDispatcher dispatcher;
    // 마이크를 통해 입력된 음질들을 관리하는 매개체(변수)
    PitchDetectionHandler pitchDetectionHandler;
    // 마이크를 통해 입력된 음들을 실시간으로 Hz로 출력하는 핸들러(스레드)
    AudioProcessor audioProcessor;
    // 마이크를 통해 입력된 음들을 알고리즘을 통해 Hz로 변환하는 프로세서(스레드), 음정을 측정하는 데 가장 실질적으로 필요한 변수
    String[] pitch_name;
    // 1옥타브 도에서부터 4옥타브 도까지 저장할 수 있는 String 배열
    boolean pitch_min_boolean, pitch_max_boolean = false;
    // 최저음 또는 최고음을 변경했을 때, 최근에 변경한 음이 재생될 수 있도록 하는 Boolean 값
    Thread pitch_input;
    private FloatingActionButton back_fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_ear_test);
        mCtx = getApplicationContext();
        findViewById(R.id.pitch_min_down_btn).setOnClickListener(this);
        findViewById(R.id.pitch_min_up_btn).setOnClickListener(this);
        findViewById(R.id.pitch_max_down_btn).setOnClickListener(this);
        findViewById(R.id.pitch_max_up_btn).setOnClickListener(this);
        findViewById(R.id.pitch_play_btn).setOnClickListener(this);
        findViewById(R.id.pitch_test_btn).setOnClickListener(this);
        pitch_min_txt = findViewById(R.id.pitch_min_txt);
        pitch_max_txt = findViewById(R.id.pitch_max_txt);
        note = findViewById(R.id.pitch_note);
        back_fab = findViewById(R.id.ear_test_back_fab);
        pitch = new int[37];
        // int 37개가 들어가는 배열 생성
        pitch_name = new String[]{"1옥타브 도", "1옥타브 도#", "1옥타브 레", "1옥타브 레#", "1옥타브 미", "1옥타브 파", "1옥타브 파#", "1옥타브 솔", "1옥타브 솔#", "1옥타브 라", "1옥타브 라#", "1옥타브 시",
                "2옥타브 도", "2옥타브 도#", "2옥타브 레", "2옥타브 레#", "2옥타브 미", "2옥타브 파", "2옥타브 파#", "2옥타브 솔", "2옥타브 솔#", "2옥타브 라", "2옥타브 라#", "2옥타브 시",
                "3옥타브 도", "3옥타브 도#", "3옥타브 레", "3옥타브 레#", "3옥타브 미", "3옥타브 파", "3옥타브 파#", "3옥타브 솔", "3옥타브 솔#", "3옥타브 라", "3옥타브 라#", "3옥타브 시", "4옥타브 도"};
        // String 37개가 들어가는 배열 생성

        audio_processor();
        pitch_input = new Thread(dispatcher);
        pitch_input.setDaemon(true);
        pitch_input.start();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { // min_sdk 버전이 롤리팝 버전 이하일 경우
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 1); // SoundPool 구버전(deprecated)을 씀
        } else {
            soundPool = new SoundPool.Builder().setMaxStreams(10).build(); // 롤리팝 이후 버전일 경우, 신형 현 SoundPool을 씀.
        }
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

        back_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void audio_processor() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        // 매개변수를 통해 오디오 품질 값을 입력한 것을 dispatcher에 할당한다.
        // 매개변수 1은 오디오 샘플 비율(22050 Hz까지 측정가능하도록, 더 늘릴 수 있음)매개변수 2(하나의 버퍼사이즈)

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        pitch_min = 5; // 최초 최저음의 숫자는 5
        pitch_max = 13; // 최초 최고음의 숫자는 13
    }

    @Override
    protected void onResume() {
        super.onResume();
        pitch_min_txt.setText(pitch_name[pitch_min]); // 최초 최저음에 맞추어 최저음계 텍스트에 표시
        pitch_max_txt.setText(pitch_name[pitch_max]); // 최초 최고음에 맞추어 최고음계 텍스트에 표시
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!dispatcher.isStopped())
            dispatcher.stop(); // 액티비티가 종료될 때 쯤, 음정측정 프로세스 종료
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!dispatcher.isStopped())
            dispatcher.stop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pitch_min_down_btn: // 최저음 하향 버튼을 눌렀을 때,
                if (pitch_min >= 1) { // 1옥타브 도 밑으로는 안내려 가도록 함
                    pitch_min--;
                    pitch_min_txt.setText(pitch_name[pitch_min]);
                    pitch_min_boolean = true;
                    pitch_max_boolean = false;
                }
                break;
            case R.id.pitch_min_up_btn: // 최저음 상향 버튼을 눌렀을 때,
                if (pitch_min < pitch_max) // 최저음 상향 조정버튼은 최고음 보다 밑에 있어야 함
                    if (pitch_min <= 34) { // 3옥타브 시 위로는 안내려 가도록 함
                        pitch_min++;
                        pitch_min_txt.setText(pitch_name[pitch_min]);
                        pitch_min_boolean = true;
                        pitch_max_boolean = false;
                    }
                break;

            case R.id.pitch_max_down_btn: // 최고음 하향 버튼을 눌렀을 때,
                if (pitch_max > pitch_min) // 최고음 하향 버튼 최저음 보다 위에 있어야 함
                    if (pitch_max >= 2) { // 1옥타브 도# 밑으로는 안내려 가도록 함
                        pitch_max--;
                        pitch_max_txt.setText(pitch_name[pitch_max]);
                        pitch_min_boolean = false;
                        pitch_max_boolean = true;
                    }
                break;

            case R.id.pitch_max_up_btn: // 최고음 상향 버튼을 눌렀을 때,
                if (pitch_max <= 35) { // 4옥타브 도 위로는 안내려 가도록 함
                    pitch_max++;
                    pitch_max_txt.setText(pitch_name[pitch_max]);
                    pitch_min_boolean = false;
                    pitch_max_boolean = true;
                }
                break;

            case R.id.pitch_play_btn: // 재생 버튼을 눌렀을 때
                if (pitch_min_boolean == true) { // 최저음을 가장 최근에 눌렀을 때
                    soundPool.play(pitch[pitch_min], 1, 1, 1, 0, 1);
                } else if (pitch_max_boolean == true) { // 최고음을 가장 최근에 눌렀을 때
                    soundPool.play(pitch[pitch_max], 1, 1, 1, 0, 1);
                } else {
                    Toast.makeText(mCtx, "음을 선택해 주세요!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.pitch_test_btn: // 음정 테스트 버튼을 눌렀을 때
                if (pitch_min_boolean == true || pitch_max_boolean == true) {
                    Intent intent = new Intent(mCtx, Ear_sensitivity_test.class);
                    intent.putExtra("min_pitch", pitch_min);
                    intent.putExtra("max_pitch", pitch_max);
                    startActivity(intent);
                } else {
                    Toast.makeText(mCtx, "나의 최저음과 최고음을 맞추어 주세요!", Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }

    public void process_Pitch(float pitchInHz) { // 실시간으로 출력되는 Hz를 실시간으로 음계로 바꾸어 주는 메소드

        if (pitchInHz >= -10 && pitchInHz < 16.35) {
            note.setText("음 없음");

        } else if (pitchInHz >= 16.35 && pitchInHz <= 17.31) {
            note.setText("[-2옥타브] 도");

        } else if (pitchInHz >= 17.32 && pitchInHz <= 18.34) {
            note.setText("[-2옥타브] 도#");

        } else if (pitchInHz >= 18.35 && pitchInHz <= 19.45) {
            note.setText("[-2옥타브] 레");

        } else if (pitchInHz >= 19.46 && pitchInHz <= 20.59) {
            note.setText("[-2옥타브] 레#");

        } else if (pitchInHz >= 20.60 && pitchInHz <= 21.82) {
            note.setText("[-2옥타브] 미");

        } else if (pitchInHz >= 21.83 && pitchInHz <= 23.11) {
            note.setText("[-2옥타브] 파");

        } else if (pitchInHz >= 23.12 && pitchInHz <= 24.49) {
            note.setText("[-2옥타브] 파#");

        } else if (pitchInHz >= 24.50 && pitchInHz <= 25.95) {
            note.setText("[-2옥타브] 솔");

        } else if (pitchInHz >= 25.96 && pitchInHz <= 27.49) {
            note.setText("[-2옥타브] 솔#");

        } else if (pitchInHz >= 27.50 && pitchInHz <= 29.13) {
            note.setText("[-2옥타브] 라");

        } else if (pitchInHz >= 29.14 && pitchInHz <= 30.86) {
            note.setText("[-2옥타브] 라#");

        } else if (pitchInHz >= 30.87 && pitchInHz <= 32.69) {
            note.setText("[-2옥타브] 시");

        } else if (pitchInHz >= 32.70 && pitchInHz <= 34.64) {
            note.setText("[-1옥타브] 도");

        } else if (pitchInHz >= 34.65 && pitchInHz <= 36.70) {
            note.setText("[-1옥타브] 도#");

        } else if (pitchInHz >= 36.71 && pitchInHz <= 38.88) {
            note.setText("[-1옥타브] 레");

        } else if (pitchInHz >= 38.89 && pitchInHz <= 41.19) {
            note.setText("[-1옥타브] 레#");

        } else if (pitchInHz >= 41.20 && pitchInHz <= 43.64) {
            note.setText("[-1옥타브] 미");

        } else if (pitchInHz >= 43.65 && pitchInHz <= 46.24) {
            note.setText("[-1옥타브] 파");

        } else if (pitchInHz >= 46.25 && pitchInHz <= 48.99) {
            note.setText("[-1옥타브] 파#");

        } else if (pitchInHz >= 49.00 && pitchInHz <= 51.90) {
            note.setText("[-1옥타브] 솔");

        } else if (pitchInHz >= 51.91 && pitchInHz <= 54.99) {
            note.setText("[-1옥타브] 솔#");

        } else if (pitchInHz >= 55.00 && pitchInHz <= 58.26) {
            note.setText("[-1옥타브] 라");

        } else if (pitchInHz >= 58.27 && pitchInHz <= 61.73) {
            note.setText("[-1옥타브] 라#");

        } else if (pitchInHz >= 61.74 && pitchInHz <= 65.40) {
            note.setText("[-1옥타브] 시");

        } else if (pitchInHz >= 65.41 && pitchInHz <= 69.29) {
            note.setText("[0옥타브] 도");

        } else if (pitchInHz >= 69.30 && pitchInHz <= 73.41) {
            note.setText("[0옥타브] 도#");

        } else if (pitchInHz >= 73.42 && pitchInHz <= 77.77) {
            note.setText("[0옥타브] 레");

        } else if (pitchInHz >= 77.78 && pitchInHz <= 82.40) {
            note.setText("[0옥타브] 레#");

        } else if (pitchInHz >= 82.41 && pitchInHz <= 87.30) {
            note.setText("[0옥타브] 미");

        } else if (pitchInHz >= 87.31 && pitchInHz <= 92.49) {
            note.setText("[0옥타브] 파");

        } else if (pitchInHz >= 92.50 && pitchInHz <= 97.99) {
            note.setText("[0옥타브] 파#");

        } else if (pitchInHz >= 98.00 && pitchInHz <= 103.82) {
            note.setText("[0옥타브] 솔");

        } else if (pitchInHz >= 103.83 && pitchInHz <= 109.99) {
            note.setText("[0옥타브] 솔#");

        } else if (pitchInHz >= 110.00 && pitchInHz <= 116.53) {
            note.setText("[0옥타브] 라");

        } else if (pitchInHz >= 116.54 && pitchInHz <= 123.46) {
            note.setText("[0옥타브] 라#");

        } else if (pitchInHz >= 123.47 && pitchInHz <= 130.80) {
            note.setText("[0옥타브] 시");

        } else if (pitchInHz >= 125.81 && pitchInHz <= 133.58) {
            note.setText("[1옥타브] 도");

        } else if (pitchInHz >= 133.59 && pitchInHz <= 141.82) {
            note.setText("[1옥타브] 도#");

        } else if (pitchInHz >= 141.83 && pitchInHz <= 150.55) {
            note.setText("[1옥타브] 레");

        } else if (pitchInHz >= 150.56 && pitchInHz <= 159.80) {
            note.setText("[1옥타브] 레#");

        } else if (pitchInHz >= 159.81 && pitchInHz <= 169.60) {
            note.setText("[1옥타브] 미");

        } else if (pitchInHz >= 169.61 && pitchInHz <= 179.99) {
            note.setText("[1옥타브] 파");

        } else if (pitchInHz >= 180.00 && pitchInHz <= 190.99) {
            note.setText("[1옥타브] 파#");

        } else if (pitchInHz >= 191.00 && pitchInHz <= 202.64) {
            note.setText("[1옥타브] 솔");

        } else if (pitchInHz >= 202.65 && pitchInHz <= 214.99) {
            note.setText("[1옥타브] 솔#");

        } else if (pitchInHz >= 215.00 && pitchInHz <= 228.07) {
            note.setText("[1옥타브] 라");

        } else if (pitchInHz >= 228.08 && pitchInHz <= 241.93) {
            note.setText("[1옥타브] 라#");

        } else if (pitchInHz >= 241.94 && pitchInHz <= 256.62) {
            note.setText("[1옥타브] 시");

        } else if (pitchInHz >= 256.63 && pitchInHz <= 272.17) {
            note.setText("[2옥타브] 도");

        } else if (pitchInHz >= 272.18 && pitchInHz <= 288.65) {
            note.setText("[2옥타브] 도#");

        } else if (pitchInHz >= 288.66 && pitchInHz <= 306.12) {
            note.setText("[2옥타브] 레");

        } else if (pitchInHz >= 306.13 && pitchInHz <= 326.62) {
            note.setText("[2옥타브] 레#");

        } else if (pitchInHz >= 326.63 && pitchInHz <= 347.22) {
            note.setText("[2옥타브] 미");

        } else if (pitchInHz >= 347.23 && pitchInHz <= 369.98) {
            note.setText("[2옥타브] 파");

        } else if (pitchInHz >= 369.99 && pitchInHz <= 391.99) {
            note.setText("[2옥타브] 파#");

        } else if (pitchInHz >= 392.00 && pitchInHz <= 415.29) {
            note.setText("[2옥타브] 솔");

        } else if (pitchInHz >= 415.30 && pitchInHz <= 439.99) {
            note.setText("[2옥타브] 솔#");

        } else if (pitchInHz >= 440.00 && pitchInHz <= 466.15) {
            note.setText("[2옥타브] 라");

        } else if (pitchInHz >= 466.16 && pitchInHz <= 493.87) {
            note.setText("[2옥타브] 라#");

        } else if (pitchInHz >= 493.88 && pitchInHz <= 523.24) {
            note.setText("[2옥타브] 시");

        } else if (pitchInHz >= 523.25 && pitchInHz <= 554.36) {
            note.setText("[3옥타브] 도");

        } else if (pitchInHz >= 554.37 && pitchInHz <= 587.32) {
            note.setText("[3옥타브] 도#");

        } else if (pitchInHz >= 587.33 && pitchInHz <= 622.24) {
            note.setText("[3옥타브] 레");

        } else if (pitchInHz >= 622.25 && pitchInHz <= 659.24) {
            note.setText("[3옥타브] 레#");

        } else if (pitchInHz >= 659.25 && pitchInHz <= 698.45) {
            note.setText("[3옥타브] 미");

        } else if (pitchInHz >= 698.46 && pitchInHz <= 739.98) {
            note.setText("[3옥타브] 파");

        } else if (pitchInHz >= 739.99 && pitchInHz <= 783.98) {
            note.setText("[3옥타브] 파#");

        } else if (pitchInHz >= 783.99 && pitchInHz <= 830.60) {
            note.setText("[3옥타브] 솔");

        } else if (pitchInHz >= 830.61 && pitchInHz <= 879.99) {
            note.setText("[3옥타브] 솔#");

        } else if (pitchInHz >= 880.00 && pitchInHz <= 932.32) {
            note.setText("[3옥타브] 라");

        } else if (pitchInHz >= 932.33 && pitchInHz <= 987.76) {
            note.setText("[3옥타브] 라#");

        } else if (pitchInHz >= 987.77 && pitchInHz <= 1046.49) {
            note.setText("[3옥타브] 시");

        } else if (pitchInHz >= 1046.50 && pitchInHz <= 1108.72) {
            note.setText("[4옥타브] 도");

        } else {
            note.setText("돌고래와 대화하세요!");
        }


    }

}
