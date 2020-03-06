package com.mymusic.orvai.high_pitched_tone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.mymusic.orvai.high_pitched_tone.Chat_Sqlite.Chat_database_Helper;
import com.mymusic.orvai.high_pitched_tone.Service.Socket_Service;
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;
import com.mymusic.orvai.high_pitched_tone.adapters.Chat_msg_view_adapter;
import com.mymusic.orvai.high_pitched_tone.models.Chat_msg;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Chat_room_Activity extends AppCompatActivity {

    public String user_id, receive_data, chat_user_id, chat_msg, user_pic_url, chat_server_time, room_number, my_room_number, my_roomName;
    // 유저 아이디 값, 받은 데이터 값, 유저 아이디 값, 메시지 값, 유저 프로필 사진 값, 메시지 등록 시간, 방 번호, 클라이언트 스레드에 등록된 방번호, 방이름
    public ImageButton img_btn;
    // 채팅 서버에 이미지 보내는 버튼
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private AlertDialog.Builder builder;
    public Toolbar toolbar;
    public List<Chat_msg> msg_list, saved_msg_list;
    private Intent intent;
    private Handler handler;
    private EditText chat_e_text;
    public Button send_btn;
    int mdy_code;

    private Chat_database_Helper helper;
    // SQLite 헬퍼

    private static final String SEPARATOR = "|";

    private static final int YES_SENDWORD = 2051;
    private static final int MDY_ROOMUSER = 2023;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        user_id = SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_id();
        my_room_number = getIntent().getStringExtra("enter_room_number");
        my_roomName = getIntent().getStringExtra("enter_room_name");
        recyclerView = findViewById(R.id.chat_list);
        chat_e_text = findViewById(R.id.chat_e_text);
        send_btn = findViewById(R.id.chat_send_btn);
        img_btn = findViewById(R.id.chat_album);
        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(my_roomName);
        msg_list = new ArrayList<>();
        helper = new Chat_database_Helper(this);
        builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = chat_e_text.getText().toString();
                if (!msg.equals("") && !msg.startsWith("|") && !msg.endsWith("|")) {
                    intent = new Intent("SOCKET_SERVICE");
                    intent.putExtra("mode", "sending_msg");
                    intent.putExtra("msg_room_name", my_roomName);
                    intent.putExtra("msg_room_number", my_room_number);
                    intent.putExtra("msg", msg);
                    sendBroadcast(intent);
                    chat_e_text.setText("");
                }
            }
        });

        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
            }
        });

        saved_msg_list = helper.load_chat_data(my_room_number); // 내가 들어간 방에 있는 방번호를 매개변수를 통해 채팅 내역을 모두 불러와, ArrayList에 저장한다.
        msg_list.addAll(saved_msg_list); // 저장된 ArrayList를 메시지 리스트에 모두 저장한다.
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter("MY_ACTIVITY")); // 브로드캐스트 리시버 등록
        adapter = new Chat_msg_view_adapter(msg_list, this); // 리사이클러뷰 어댑터 등록
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext())); // 리사이클러뷰를 Linear로 바꿈
        handler = new Handler(); // 핸들러 생성
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.setAdapter(adapter); // 리사이클러 뷰에 어댑터 넣음
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        }); // 핸들러를 통해 SQLITE을 통해 불러온 메시지 내역을 UI 변경시킴
        Socket_Service.service_stop = false; // 태스크킬 되었을 때 바뀌는 불린 값을 false로 바꿈
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver); // 브로드캐스트 리시버 해제
    }

    @Override
    protected void onDestroy() { // 백스페이스, 또는 태스크 킬 하였을 때, 로비로 가는 요청 코드를 보내는 인텐트를 보내며, 클라이언트 스레드의 방 번호를 0으로 바꿈.
        super.onDestroy();
        intent = new Intent("SOCKET_SERVICE");
        intent.putExtra("mode", "enter_lobby");
        intent.putExtra("quit_lobby", "0");
        sendBroadcast(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 이미지를 가져왔을 때
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            String image_temp = String.valueOf(imageUri);
            intent = new Intent("SOCKET_SERVICE");
            intent.putExtra("mode", "sending_image");
            intent.putExtra("msg_room_name", my_roomName);
            intent.putExtra("msg_room_number", my_room_number);
            intent.putExtra("image", image_temp);
            sendBroadcast(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 툴바 선택 메뉴
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.chat_exit_btn:
                builder.setIcon(R.drawable.chatroom_activity_exit_btn);
                builder.setTitle("방 나가기").setMessage("채팅방을 나가게 되면 채팅내역이 모두 삭제됩니다.\n나가시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent = new Intent("SOCKET_SERVICE");
                        intent.putExtra("mode", "quit_room");
                        intent.putExtra("quit_room_number", my_room_number);
                        sendBroadcast(intent);
                        helper.delete_chat_data(my_room_number);
                        SharedPrefManager.getInstance(getApplicationContext()).deleteMyroom(my_room_number);
                        finish();
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.create();
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // 툴바 생성
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.chat_toolbar_menu, menu);
        return true;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            receive_data = intent.getStringExtra("service_msg");
            StringTokenizer st = new StringTokenizer(receive_data, SEPARATOR);
            int command = Integer.parseInt(st.nextToken());

            switch (command) {

                case MDY_ROOMUSER: { // 2023|유저아이디|방번호|코드(0은 퇴장, 1은 입장, 2는 강퇴)|방에 남아있는 유저 아이디들
                    chat_user_id = st.nextToken();
                    room_number = st.nextToken();
                    mdy_code = Integer.parseInt(st.nextToken());
                    Log.d("kb"+room_number,my_room_number);
                    if (room_number.equals(my_room_number)) {
                        msg_list.add(new Chat_msg(room_number, chat_user_id, null, null, mdy_code, null, 0));
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    break;
                }

                case YES_SENDWORD: { // 2051|유저아이디|유저사진url|방번호|메시지내용
                    chat_user_id = st.nextToken();
                    user_pic_url = st.nextToken();
                    room_number = st.nextToken();
                    chat_server_time = st.nextToken();
                    chat_msg = st.nextToken();
                    if (room_number.equals(my_room_number)) {
                        if (chat_msg.length() >= 1000) { // 메시지 크기가 비정상적으로 큰 경우... 이미지로 판단
                            msg_list.add(new Chat_msg(room_number, chat_user_id, chat_msg, user_pic_url, 3, chat_server_time, 1));
                        } else {
                            msg_list.add(new Chat_msg(room_number, chat_user_id, chat_msg, user_pic_url, 3, chat_server_time, 0));
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    break;
                }
            }
        }
    };


}