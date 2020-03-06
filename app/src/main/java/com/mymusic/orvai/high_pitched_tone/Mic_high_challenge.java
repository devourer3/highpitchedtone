package com.mymusic.orvai.high_pitched_tone;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.mymusic.orvai.high_pitched_tone.API_Result.Game_high_pitch_challenge_API_Result;
import com.mymusic.orvai.high_pitched_tone.Custom_dialog.High_pitch_rank;
import com.mymusic.orvai.high_pitched_tone.Interface.API_Service;
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;
import com.mymusic.orvai.high_pitched_tone.models.URLs;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Mic_high_challenge extends AppCompatActivity {

    TextView pitch, note, time;
    // pitch는 Hz로 표시되는 음의 높이를, note는 옥타브와 음계로 표시는 음의 높이를, time은 5초의 시간을 각각 텍스트뷰에 표시하기 위한 변수
    Button start_btn, rank_btn;
    // 스타트 버튼을 나타내는 변수, 랭킹 리스트를 보여주는 버튼
    float pitchInHz;
    // 소수점으로 표시되는 음의 높이 헤르츠(Hz)
    Thread pitch_thread, timer_thread;
    // 음의 높이를 지속적으로 측정하는 스레드와, 시간측정 스레드
    AudioDispatcher dispatcher;
    // 마이크를 통해 입력된 음질들을 관리하는 매개체(변수)
    PitchDetectionHandler pitchDetectionHandler;
    // 마이크를 통해 입력된 음들을 실시간으로 Hz로 출력하는 핸들러(스레드)
    AudioProcessor audioProcessor;
    // 마이크를 통해 입력된 음들을 알고리즘을 통해 Hz로 변환하는 프로세서(스레드), 음정을 측정하는 데 가장 실질적으로 필요한 변수
    AlertDialog.Builder builder;
    // 다이얼로그를 출력하기 위한 빌더
    Retrofit retrofit;
    // 서버에 음정 측정 결과를 담기 위해서 서버와 통신해야 함. 따라서 retrofit 생성
    API_Service service;
    // retrofit을 이용하기 위한 API_SERVICE 인터페이스를 생성
    int timer = 5;
    // 5초의 시간
    int[] leading_pitch_number;
    // 각 Hz마다 나누어진 옥타브를 0.1 초마다 얼마만큼 내었는지 기록하기 위해 필요한 배열
    HashMap<String, Integer> leading_pitch_map;
    // 0.1초마다 기록된 음정들을 key값을 음계이름, value 값을 leading_pitch_number로 담는 해시 맵
    TreeMap<String, Integer> leading_pitch_sorted_map;
    // HashMap에 기록된 결과들을 value값을 기준으로 오름차순으로 정렬하기 위해 필요한 TreeMap
    IconRoundCornerProgressBar progressBar;
    // 현재 사용자가 내는 음정을 실시간으로 어떤 위치에 있는지 나타내기 위한 커스텀 프로그레스 바
    String high_pitch, user_id;
    // TreeMap의 정렬을 이용하여, 최종적으로 가장 고음에 존재하는 key값을 담는 변수와 클라이언트 유저의 아이디를 저장하는 변수
    int high_pitch_no;
    // TreeMap의 정렬을 이용하여, 최종적으로 가장 고음에 존재하는 value값을 담는 변수
    ValueComparator valueComparator;
    // TreeMap을 이용하여 정렬할 때 쓰이는 변수
    FloatingActionButton back_fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic_high_challenge);
        pitch = findViewById(R.id.pitch);
        note = findViewById(R.id.note);
        start_btn = findViewById(R.id.start_btn);
        time = findViewById(R.id.pitch_timer);
        progressBar = findViewById(R.id.corner_progress);
        rank_btn = findViewById(R.id.rank_btn);
        back_fab = findViewById(R.id.challenge_back_fab);
        leading_pitch_number = new int[73];
        leading_pitch_map = new HashMap<String, Integer>();
        valueComparator = new ValueComparator(leading_pitch_map);
        leading_pitch_sorted_map = new TreeMap<String, Integer>(valueComparator);

        user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();

        builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);
        // 얼럿 다이얼로그 창의 색을 밝은색으로 세팅

        retrofit = new Retrofit.Builder().baseUrl(URLs.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        // 레트로핏 등록

        service = retrofit.create(API_Service.class);
        // 레트로핏 서비스를 이용할 수 있도록 인터페이스 등록

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        // 매개변수를 통해 오디오 품질 값을 입력한 것을 dispatcher에 할당한다.
        // 매개변수 1은 오디오 샘플 비율(22050 Hz까지 측정가능하도록, 더 늘릴 수 있음)매개변수 2(하나의 버퍼사이즈)

        back_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pitchDetectionHandler = new PitchDetectionHandler() { // 마이크에서 입력된 음정들을 실시간으로 Hz로 표시해주는 핸들러
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                pitchInHz = pitchDetectionResult.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        process_Pitch(pitchInHz);
                        progressBar.setProgress(pitchInHz);
                        if (pitchInHz >= 10 && pitchInHz < 261.62) { // -2옥타브에서 1옥타브 까지
                            progressBar.setProgressColor(Color.GREEN);
                        } else if (pitchInHz >= 261.63 && pitchInHz < 523.25) { // 1옥브에서 2옥타브까지
                            progressBar.setProgressColor(Color.BLUE);
                        } else { // 3옥타브 부터
                            progressBar.setProgressColor(Color.RED);
                        }
                    }
                });
            }
        };
        audioProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pitchDetectionHandler); // 알고리즘을 통하여 샘플링 된 음성들을 Hz화 시켜 오디오 프로세서에 할당
        dispatcher.addAudioProcessor(audioProcessor); // dispatcher에 오디오 프로세서 추가

        timer_thread = new Thread(new Runnable() {
            @Override
            public void run() { // 타이머 스레드. 5초 지나면 결과 값이 출력되도록.
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(1000);
                        timer--;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (timer > 2) {
                                time.setText(String.valueOf(timer)+"초");
                            } else if (timer > 0 && timer <= 2) {
                                time.setTextColor(Color.parseColor("#ce0000"));
                                time.setText(String.valueOf(timer)+"초");
                            } else {
                                leading_pitch_sorted_map.putAll(leading_pitch_map);
                                Set set_tmp = leading_pitch_sorted_map.entrySet();
                                Iterator it = set_tmp.iterator();
                                Map.Entry entry_tmp = (Map.Entry) it.next();
                                high_pitch = (String) entry_tmp.getKey(); // 제일 많이 낸 소리의 음계
                                high_pitch_no = (int) entry_tmp.getValue(); // 제일 많이 낸 소리의 개수
                                time.setText("시간종료");
                                note.setText("음성 Hz");
                                pitch.setText("음계");
                                dispatcher.stop(); // 음정 기록 스레드 멈춤.
                                result_dialog(high_pitch, high_pitch_no);
                            }
                        }
                    });
                }
            }
        });

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pitch_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dispatcher.run(); // 실질적인 음정 기록 스레드 시작.
                    }
                });
                pitch_thread.setDaemon(true);
                timer_thread.setDaemon(true);
                pitch_thread.start();
                timer_thread.start();
                start_btn.setVisibility(View.GONE);
            }
        });

        rank_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                High_pitch_rank dialog = new High_pitch_rank(view.getContext()); // 고음 랭크창을 띄우는 버튼, getapplicationcontext 하면 에러가 난다.
                dialog.show();
            }
        });

    } // onCreate 끝

    public void result_dialog(final String note, final int note_no) {
        builder.setIcon(R.mipmap.ic_music_note);
        builder.setTitle("결과");
        builder.setMessage(user_id + "님의 최고 음정은\n" + note + "입니다. \n결과를 올리시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Call<Game_high_pitch_challenge_API_Result> result = service.request_pitch_update(user_id, note, note_no);
                result.enqueue(new Callback<Game_high_pitch_challenge_API_Result>() {
                    @Override
                    public void onResponse(Call<Game_high_pitch_challenge_API_Result> call, Response<Game_high_pitch_challenge_API_Result> response) {
                        Toast.makeText(Mic_high_challenge.this, "등록되었습니다!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Game_high_pitch_challenge_API_Result> call, Throwable t) {
                    }
                });
            }
        });
        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!dispatcher.isStopped()) {
            dispatcher.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!dispatcher.isStopped()) {
            dispatcher.stop();
        }
    }

    public void process_Pitch(float pitchInHz) {

        pitch.setText("" + pitchInHz);

        if (pitchInHz >= -10 && pitchInHz < 16.35) {
            note.setText("음 없음");

        } else if (pitchInHz >= 16.35 && pitchInHz <= 17.31) {
            note.setText("[-2옥타브] 도");
            leading_pitch_number[0]++;
            leading_pitch_map.put("[-2옥타브] 도", leading_pitch_number[0]);

        } else if (pitchInHz >= 17.32 && pitchInHz <= 18.34) {
            note.setText("[-2옥타브] 도#");
            leading_pitch_number[1]++;
            leading_pitch_map.put("[-2옥타브] 도#", leading_pitch_number[1]);

        } else if (pitchInHz >= 18.35 && pitchInHz <= 19.45) {
            note.setText("[-2옥타브] 레");
            leading_pitch_number[2]++;
            leading_pitch_map.put("[-2옥타브] 레", leading_pitch_number[2]);

        } else if (pitchInHz >= 19.46 && pitchInHz <= 20.59) {
            note.setText("[-2옥타브] 레#");
            leading_pitch_number[3]++;
            leading_pitch_map.put("[-2옥타브] 레#", leading_pitch_number[3]);

        } else if (pitchInHz >= 20.60 && pitchInHz <= 21.82) {
            note.setText("[-2옥타브] 미");
            leading_pitch_number[4]++;
            leading_pitch_map.put("[-2옥타브] 미", leading_pitch_number[4]);

        } else if (pitchInHz >= 21.83 && pitchInHz <= 23.11) {
            note.setText("[-2옥타브] 파");
            leading_pitch_number[5]++;
            leading_pitch_map.put("[-2옥타브] 파", leading_pitch_number[5]);

        } else if (pitchInHz >= 23.12 && pitchInHz <= 24.49) {
            note.setText("[-2옥타브] 파#");
            leading_pitch_number[6]++;
            leading_pitch_map.put("[-2옥타브] 파#", leading_pitch_number[6]);

        } else if (pitchInHz >= 24.50 && pitchInHz <= 25.95) {
            note.setText("[-2옥타브] 솔");
            leading_pitch_number[7]++;
            leading_pitch_map.put("[-2옥타브] 솔", leading_pitch_number[7]);

        } else if (pitchInHz >= 25.96 && pitchInHz <= 27.49) {
            note.setText("[-2옥타브] 솔#");
            leading_pitch_number[8]++;
            leading_pitch_map.put("[-2옥타브] 솔#", leading_pitch_number[8]);

        } else if (pitchInHz >= 27.50 && pitchInHz <= 29.13) {
            note.setText("[-2옥타브] 라");
            leading_pitch_number[9]++;
            leading_pitch_map.put("[-2옥타브] 라", leading_pitch_number[9]);

        } else if (pitchInHz >= 29.14 && pitchInHz <= 30.86) {
            note.setText("[-2옥타브] 라#");
            leading_pitch_number[10]++;
            leading_pitch_map.put("[-2옥타브] 라#", leading_pitch_number[10]);

        } else if (pitchInHz >= 30.87 && pitchInHz <= 32.69) {
            note.setText("[-2옥타브] 시");
            leading_pitch_number[11]++;
            leading_pitch_map.put("[-2옥타브] 시", leading_pitch_number[11]);

        } else if (pitchInHz >= 32.70 && pitchInHz <= 34.64) {
            note.setText("[-1옥타브] 도");
            leading_pitch_number[12]++;
            leading_pitch_map.put("[-1옥타브] 도", leading_pitch_number[12]);

        } else if (pitchInHz >= 34.65 && pitchInHz <= 36.70) {
            note.setText("[-1옥타브] 도#");
            leading_pitch_number[13]++;
            leading_pitch_map.put("[-1옥타브] 도#", leading_pitch_number[13]);

        } else if (pitchInHz >= 36.71 && pitchInHz <= 38.88) {
            note.setText("[-1옥타브] 레");
            leading_pitch_number[14]++;
            leading_pitch_map.put("[-1옥타브] 레", leading_pitch_number[14]);

        } else if (pitchInHz >= 38.89 && pitchInHz <= 41.19) {
            note.setText("[-1옥타브] 레#");
            leading_pitch_number[15]++;
            leading_pitch_map.put("[-1옥타브] 레#", leading_pitch_number[15]);

        } else if (pitchInHz >= 41.20 && pitchInHz <= 43.64) {
            note.setText("[-1옥타브] 미");
            leading_pitch_number[16]++;
            leading_pitch_map.put("[-1옥타브] 미", leading_pitch_number[16]);

        } else if (pitchInHz >= 43.65 && pitchInHz <= 46.24) {
            note.setText("[-1옥타브] 파");
            leading_pitch_number[17]++;
            leading_pitch_map.put("[-1옥타브] 파", leading_pitch_number[17]);

        } else if (pitchInHz >= 46.25 && pitchInHz <= 48.99) {
            note.setText("[-1옥타브] 파#");
            leading_pitch_number[18]++;
            leading_pitch_map.put("[-1옥타브] 파#", leading_pitch_number[18]);

        } else if (pitchInHz >= 49.00 && pitchInHz <= 51.90) {
            note.setText("[-1옥타브] 솔");
            leading_pitch_number[19]++;
            leading_pitch_map.put("[-1옥타브] 솔", leading_pitch_number[19]);

        } else if (pitchInHz >= 51.91 && pitchInHz <= 54.99) {
            note.setText("[-1옥타브] 솔#");
            leading_pitch_number[20]++;
            leading_pitch_map.put("[-1옥타브] 솔#", leading_pitch_number[20]);

        } else if (pitchInHz >= 55.00 && pitchInHz <= 58.26) {
            note.setText("[-1옥타브] 라");
            leading_pitch_number[21]++;
            leading_pitch_map.put("[-1옥타브] 라", leading_pitch_number[21]);

        } else if (pitchInHz >= 58.27 && pitchInHz <= 61.73) {
            note.setText("[-1옥타브] 라#");
            leading_pitch_number[22]++;
            leading_pitch_map.put("[-1옥타브] 라#", leading_pitch_number[22]);

        } else if (pitchInHz >= 61.74 && pitchInHz <= 65.40) {
            note.setText("[-1옥타브] 시");
            leading_pitch_number[23]++;
            leading_pitch_map.put("[-1옥타브] 시", leading_pitch_number[23]);

        } else if (pitchInHz >= 65.41 && pitchInHz <= 69.29) {
            note.setText("[0옥타브] 도");
            leading_pitch_number[24]++;
            leading_pitch_map.put("[0옥타브] 도", leading_pitch_number[24]);

        } else if (pitchInHz >= 69.30 && pitchInHz <= 73.41) {
            note.setText("[0옥타브] 도#");
            leading_pitch_number[25]++;
            leading_pitch_map.put("[0옥타브] 도#", leading_pitch_number[25]);

        } else if (pitchInHz >= 73.42 && pitchInHz <= 77.77) {
            note.setText("[0옥타브] 레");
            leading_pitch_number[26]++;
            leading_pitch_map.put("[0옥타브] 레", leading_pitch_number[26]);

        } else if (pitchInHz >= 77.78 && pitchInHz <= 82.40) {
            note.setText("[0옥타브] 레#");
            leading_pitch_number[27]++;
            leading_pitch_map.put("[0옥타브] 레#", leading_pitch_number[27]);

        } else if (pitchInHz >= 82.41 && pitchInHz <= 87.30) {
            note.setText("[0옥타브] 미");
            leading_pitch_number[28]++;
            leading_pitch_map.put("[0옥타브] 미", leading_pitch_number[28]);

        } else if (pitchInHz >= 87.31 && pitchInHz <= 92.49) {
            note.setText("[0옥타브] 파");
            leading_pitch_number[29]++;
            leading_pitch_map.put("[0옥타브] 파", leading_pitch_number[29]);

        } else if (pitchInHz >= 92.50 && pitchInHz <= 97.99) {
            note.setText("[0옥타브] 파#");
            leading_pitch_number[30]++;
            leading_pitch_map.put("[0옥타브] 파#", leading_pitch_number[30]);

        } else if (pitchInHz >= 98.00 && pitchInHz <= 103.82) {
            note.setText("[0옥타브] 솔");
            leading_pitch_number[31]++;
            leading_pitch_map.put("[0옥타브] 솔", leading_pitch_number[31]);

        } else if (pitchInHz >= 103.83 && pitchInHz <= 109.99) {
            note.setText("[0옥타브] 솔#");
            leading_pitch_number[32]++;
            leading_pitch_map.put("[0옥타브] 솔#", leading_pitch_number[32]);

        } else if (pitchInHz >= 110.00 && pitchInHz <= 116.53) {
            note.setText("[0옥타브] 라");
            leading_pitch_number[33]++;
            leading_pitch_map.put("[0옥타브] 라", leading_pitch_number[33]);

        } else if (pitchInHz >= 116.54 && pitchInHz <= 123.46) {
            note.setText("[0옥타브] 라#");
            leading_pitch_number[34]++;
            leading_pitch_map.put("[0옥타브] 라#", leading_pitch_number[34]);

        } else if (pitchInHz >= 123.47 && pitchInHz <= 130.80) {
            note.setText("[0옥타브] 시");
            leading_pitch_number[35]++;
            leading_pitch_map.put("[0옥타브] 시", leading_pitch_number[35]);

        } else if (pitchInHz >= 130.81 && pitchInHz <= 138.58) {
            note.setText("[1옥타브] 도");
            leading_pitch_number[36]++;
            leading_pitch_map.put("[1옥타브] 도", leading_pitch_number[36]);

        } else if (pitchInHz >= 138.59 && pitchInHz <= 146.82) {
            note.setText("[1옥타브] 도#");
            leading_pitch_number[37]++;
            leading_pitch_map.put("1옥타브 도#", leading_pitch_number[37]);

        } else if (pitchInHz >= 146.83 && pitchInHz <= 155.55) {
            note.setText("[1옥타브] 레");
            leading_pitch_number[38]++;
            leading_pitch_map.put("[1옥타브] 레", leading_pitch_number[38]);

        } else if (pitchInHz >= 155.56 && pitchInHz <= 164.80) {
            note.setText("[1옥타브] 레#");
            leading_pitch_number[39]++;
            leading_pitch_map.put("[1옥타브] 레#", leading_pitch_number[39]);

        } else if (pitchInHz >= 164.81 && pitchInHz <= 174.60) {
            note.setText("[1옥타브] 미");
            leading_pitch_number[40]++;
            leading_pitch_map.put("[1옥타브] 미", leading_pitch_number[40]);

        } else if (pitchInHz >= 174.61 && pitchInHz <= 184.99) {
            note.setText("[1옥타브] 파");
            leading_pitch_number[41]++;
            leading_pitch_map.put("[1옥타브] 파", leading_pitch_number[41]);

        } else if (pitchInHz >= 185.00 && pitchInHz <= 195.99) {
            note.setText("[1옥타브] 파#");
            leading_pitch_number[42]++;
            leading_pitch_map.put("[1옥타브] 파#", leading_pitch_number[42]);

        } else if (pitchInHz >= 196.00 && pitchInHz <= 207.64) {
            note.setText("[1옥타브] 솔");
            leading_pitch_number[43]++;
            leading_pitch_map.put("[1옥타브] 솔", leading_pitch_number[43]);

        } else if (pitchInHz >= 207.65 && pitchInHz <= 219.99) {
            note.setText("[1옥타브] 솔#");
            leading_pitch_number[44]++;
            leading_pitch_map.put("[1옥타브] 솔#", leading_pitch_number[44]);

        } else if (pitchInHz >= 220.00 && pitchInHz <= 233.07) {
            note.setText("[1옥타브] 라");
            leading_pitch_number[45]++;
            leading_pitch_map.put("[1옥타브] 라", leading_pitch_number[45]);

        } else if (pitchInHz >= 233.08 && pitchInHz <= 246.93) {
            note.setText("[1옥타브] 라#");
            leading_pitch_number[46]++;
            leading_pitch_map.put("[1옥타브] 라#", leading_pitch_number[46]);

        } else if (pitchInHz >= 246.94 && pitchInHz <= 261.62) {
            note.setText("[1옥타브] 시");
            leading_pitch_number[47]++;
            leading_pitch_map.put("[1옥타브] 시", leading_pitch_number[47]);

        } else if (pitchInHz >= 261.63 && pitchInHz <= 277.17) {
            note.setText("[2옥타브] 도");
            leading_pitch_number[48]++;
            leading_pitch_map.put("[2옥타브] 도", leading_pitch_number[48]);

        } else if (pitchInHz >= 277.18 && pitchInHz <= 293.65) {
            note.setText("[2옥타브] 도#");
            leading_pitch_number[49]++;
            leading_pitch_map.put("[2옥타브] 도#", leading_pitch_number[49]);

        } else if (pitchInHz >= 293.66 && pitchInHz <= 311.12) {
            note.setText("[2옥타브] 레");
            leading_pitch_number[50]++;
            leading_pitch_map.put("[2옥타브] 레", leading_pitch_number[50]);

        } else if (pitchInHz >= 311.13 && pitchInHz <= 329.62) {
            note.setText("[2옥타브] 레#");
            leading_pitch_number[51]++;
            leading_pitch_map.put("[2옥타브] 레#", leading_pitch_number[51]);

        } else if (pitchInHz >= 329.63 && pitchInHz <= 349.22) {
            note.setText("[2옥타브] 미");
            leading_pitch_number[52]++;
            leading_pitch_map.put("[2옥타브] 미", leading_pitch_number[52]);

        } else if (pitchInHz >= 349.23 && pitchInHz <= 369.98) {
            note.setText("[2옥타브] 파");
            leading_pitch_number[53]++;
            leading_pitch_map.put("[2옥타브] 파", leading_pitch_number[53]);

        } else if (pitchInHz >= 369.99 && pitchInHz <= 391.99) {
            note.setText("[2옥타브] 파#");
            leading_pitch_number[54]++;
            leading_pitch_map.put("[2옥타브] 파#", leading_pitch_number[54]);

        } else if (pitchInHz >= 392.00 && pitchInHz <= 415.29) {
            note.setText("[2옥타브] 솔");
            leading_pitch_number[55]++;
            leading_pitch_map.put("[2옥타브] 솔", leading_pitch_number[55]);

        } else if (pitchInHz >= 415.30 && pitchInHz <= 439.99) {
            note.setText("[2옥타브] 솔#");
            leading_pitch_number[56]++;
            leading_pitch_map.put("[2옥타브] 솔#", leading_pitch_number[56]);

        } else if (pitchInHz >= 440.00 && pitchInHz <= 466.15) {
            note.setText("[2옥타브] 라");
            leading_pitch_number[57]++;
            leading_pitch_map.put("[2옥타브] 라", leading_pitch_number[57]);

        } else if (pitchInHz >= 466.16 && pitchInHz <= 493.87) {
            note.setText("[2옥타브] 라#");
            leading_pitch_number[58]++;
            leading_pitch_map.put("[2옥타브] 라#", leading_pitch_number[58]);

        } else if (pitchInHz >= 493.88 && pitchInHz <= 523.24) {
            note.setText("[2옥타브] 시");
            leading_pitch_number[59]++;
            leading_pitch_map.put("[2옥타브] 시", leading_pitch_number[59]);

        } else if (pitchInHz >= 523.25 && pitchInHz <= 554.36) {
            note.setText("[3옥타브] 도");
            leading_pitch_number[60]++;
            leading_pitch_map.put("[3옥타브] 도", leading_pitch_number[60]);

        } else if (pitchInHz >= 554.37 && pitchInHz <= 587.32) {
            note.setText("[3옥타브] 도#");
            leading_pitch_number[61]++;
            leading_pitch_map.put("[3옥타브] 도#", leading_pitch_number[61]);

        } else if (pitchInHz >= 587.33 && pitchInHz <= 622.24) {
            note.setText("[3옥타브] 레");
            leading_pitch_number[62]++;
            leading_pitch_map.put("[3옥타브] 레", leading_pitch_number[62]);

        } else if (pitchInHz >= 622.25 && pitchInHz <= 659.24) {
            note.setText("[3옥타브] 레#");
            leading_pitch_number[63]++;
            leading_pitch_map.put("[3옥타브] 레#", leading_pitch_number[63]);

        } else if (pitchInHz >= 659.25 && pitchInHz <= 698.45) {
            note.setText("[3옥타브] 미");
            leading_pitch_number[64]++;
            leading_pitch_map.put("[3옥타브] 미", leading_pitch_number[64]);

        } else if (pitchInHz >= 698.46 && pitchInHz <= 739.98) {
            note.setText("[3옥타브] 파");
            leading_pitch_number[65]++;
            leading_pitch_map.put("[3옥타브] 파", leading_pitch_number[65]);

        } else if (pitchInHz >= 739.99 && pitchInHz <= 783.98) {
            note.setText("[3옥타브] 파#");
            leading_pitch_number[66]++;
            leading_pitch_map.put("[3옥타브] 파#", leading_pitch_number[66]);

        } else if (pitchInHz >= 783.99 && pitchInHz <= 830.60) {
            note.setText("[3옥타브] 솔");
            leading_pitch_number[67]++;
            leading_pitch_map.put("[3옥타브] 솔", leading_pitch_number[67]);

        } else if (pitchInHz >= 830.61 && pitchInHz <= 879.99) {
            note.setText("[3옥타브] 솔#");
            leading_pitch_number[68]++;
            leading_pitch_map.put("[3옥타브] 솔#", leading_pitch_number[68]);

        } else if (pitchInHz >= 880.00 && pitchInHz <= 932.32) {
            note.setText("[3옥타브] 라");
            leading_pitch_number[69]++;
            leading_pitch_map.put("[3옥타브] 라", leading_pitch_number[69]);

        } else if (pitchInHz >= 932.33 && pitchInHz <= 987.76) {
            note.setText("[3옥타브] 라#");
            leading_pitch_number[70]++;
            leading_pitch_map.put("[3옥타브] 라#", leading_pitch_number[70]);

        } else if (pitchInHz >= 987.77 && pitchInHz <= 1046.49) {
            note.setText("[3옥타브] 시");
            leading_pitch_number[71]++;
            leading_pitch_map.put("[3옥타브] 시", leading_pitch_number[71]);

        } else if (pitchInHz >= 1046.50 && pitchInHz <= 1108.72) {
            note.setText("[4옥타브] 도");
            leading_pitch_number[72]++;
            leading_pitch_map.put("[4옥타브] 도", leading_pitch_number[72]);

        } else {
            note.setText("돌고래와 대화하세요!");
        }


    } // 실시간으로 출력되는 Hz를 실시간으로 음계로 바꾸어 주며, 실시간으로 HashMap에 저장하는 메소드

    class ValueComparator implements Comparator<String> { // TreeMap을 통해 HashMap에 저장된 Value값을 내림차순으로 정렬해주는 클래스

        Map<String, Integer> base;

        public ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) { //반대로 하면 오름차순(<=)
                return -1;
            } else {
                return 1;
            }
        }
    }


}
