package com.mymusic.orvai.high_pitched_tone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.mymusic.orvai.high_pitched_tone.Service.Socket_Service;
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;

import java.util.StringTokenizer;

public class Chat_room_create_Activity extends AppCompatActivity {

    EditText e_subject, e_password;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button register_btn, cancel_btn;
    Switch sw;

    String subject, password, receive_data, create_room_number, create_room_name;
    String user_max = "2";
    int islock = 0; // 0은 안잠긴 것, 1은 잠긴 것
    int radio_default_id = 2131230930;

    private static final String SEPARATOR = "|";

    private static final int YES_CREATEROOM = 2011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_create);

        e_subject = (EditText) findViewById(R.id.room_create_subject);
        e_password = (EditText) findViewById(R.id.room_create_password);
        radioGroup = (RadioGroup) findViewById(R.id.room_create_usermax);
        register_btn = (Button) findViewById(R.id.room_create_reg);
        cancel_btn = (Button) findViewById(R.id.room_create_cancel);
        sw = (Switch) findViewById(R.id.room_create_lock);
        e_password.setEnabled(false);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    e_password.setEnabled(true);
                    islock = 1;
                    sw.setText("비공개");
                    sw.setTextColor(Color.RED);
                    e_password.setText("");
                } else {
                    e_password.setEnabled(false);
                    islock = 0;
                    sw.setText("공개");
                    sw.setTextColor(Color.BLUE);
                    e_password.setText("");
                }

            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (islock == 0) { // 안잠겼을 때
                    subject = e_subject.getText().toString(); // 제목
                    password = "0"; // 공개방일 때는 비밀번호 0
                    radio_default_id = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) findViewById(radio_default_id);
                    user_max = String.valueOf(radioButton.getTag());
                    if (!subject.equals("")) {
                        Intent intent = new Intent("SOCKET_SERVICE");
                        intent.putExtra("mode", "create_room");
                        intent.putExtra("room_subject", subject);
                        intent.putExtra("room_password", password);
                        intent.putExtra("room_maxuser", user_max);
                        intent.putExtra("room_islock", islock);
                        sendBroadcast(intent);
                    }else{
                        Toast.makeText(Chat_room_create_Activity.this, "빠진 사항이 없나 확인하세요.", Toast.LENGTH_SHORT).show();
                    }
                } else { // 잠겼을 때
                    subject = e_subject.getText().toString(); // 제목
                    password = e_password.getText().toString(); // 비공개방일 때 비밀번호
                    radio_default_id = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) findViewById(radio_default_id);
                    user_max = String.valueOf(radioButton.getTag()); // 최대인원 최대인원 ★☆★☆★☆★☆ 선택 안했을 때 오류있음!!!!!!!!!!!!!!!!!★☆★☆★☆★☆★☆
                    if (!subject.equals("") && !password.equals("")) {
                        Intent intent = new Intent("SOCKET_SERVICE");
                        intent.putExtra("mode", "create_room");
                        intent.putExtra("room_subject", subject);
                        intent.putExtra("room_password", password);
                        intent.putExtra("room_maxuser", user_max);
                        intent.putExtra("room_islock", islock);
                        sendBroadcast(intent);
                    }else{
                        Toast.makeText(Chat_room_create_Activity.this, "빠진 사항이 없나 확인하세요.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter("MY_ACTIVITY"));
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            receive_data = intent.getStringExtra("service_msg");
            StringTokenizer st = new StringTokenizer(receive_data, SEPARATOR);
            int command = Integer.parseInt(st.nextToken());
            switch(command) {
                case YES_CREATEROOM: // 2011|방번호|방제목
                    create_room_number = st.nextToken();
                    create_room_name = st.nextToken();
                    if(!SharedPrefManager.getInstance(getApplicationContext()).isMyroom(create_room_number)) {
                        SharedPrefManager.getInstance(getApplicationContext()).saveMyroom(create_room_number, create_room_name);
                    }
                    Intent intent1 = new Intent(getApplicationContext(), Chat_room_Activity.class);
                    intent1.putExtra("enter_room_number", create_room_number);
                    intent1.putExtra("enter_room_name", create_room_name);
                    startActivity(intent1);
                    finish();
                    break;
            }

        }
    };

}
