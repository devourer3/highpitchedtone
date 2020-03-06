package com.mymusic.orvai.high_pitched_tone.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mymusic.orvai.high_pitched_tone.Chat_room_Activity;
import com.mymusic.orvai.high_pitched_tone.Chat_room_create_Activity;
import com.mymusic.orvai.high_pitched_tone.R;
import com.mymusic.orvai.high_pitched_tone.Service.Socket_Service;
import com.mymusic.orvai.high_pitched_tone.adapters.Chat_room_view_adapter;
import com.mymusic.orvai.high_pitched_tone.models.Chat_room;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {

    Context mCtx;
    private FloatingActionButton room_fab;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    List<Chat_room> room_list;
    String receive_data;
    Handler handler;
    Intent intent;

    private static final String SEPARATOR = "|";
    private static final String DELIMETER = "'";
    private static final String DELIMETER2 = "=";

    private static final int YES_LOGON = 2001;
    private static final int NO_LOGON = 2002;
    private static final int YES_ENTERROOM = 2021;
    private static final int NO_ENTERROOM = 2022;
    private static final int YES_LOGOUT = 2041;
    private static final int YES_SENDWORD = 2051;
    private static final int YES_SENDWORDTO = 2052;
    private static final int NO_SENDWORDTO = 2053;
    private static final int YES_COERCEOUT = 2054;
    private static final int YES_SENDFILE = 2061;
    private static final int NO_SENDFILE = 2062;

    private static final int MDY_WAITUSER = 2003;
    private static final int MDY_WAITINFO = 2013;
    private static final int MDY_ROOMUSER = 2023;

    private static final int ERR_ALREADYUSER = 3001;
    private static final int ERR_SERVERFULL = 3002;
    private static final int ERR_ROOMSFULL = 3011;
    private static final int ERR_ROOMERFULL = 3021;
    private static final int ERR_PASSWORD = 3022;
    private static final int ERR_REJECTION = 3031;
    private static final int ERR_NOUSER = 3032;

    public Chat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_tab3_chat, container, false);
        mCtx = getActivity();
        room_list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.chat_room);
        room_fab = view.findViewById(R.id.create_room);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /**
         * 소켓서비스 시작하는 지점
         */
//        intent = new Intent(mCtx, Socket_Service.class); // 소켓 서비스 인텐트 생성
//        mCtx.startService(intent); // 소켓 서비스 시작



        room_list = new ArrayList<>();
        handler = new Handler();
        room_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Chat_room_create_Activity.class));
            }
        });
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
//                int index = parent.getChildAdapterPosition(view) + 1;

                outRect.set(10, 5, 10, 5);
                view.setBackgroundResource(R.color.colorLGray);
                ViewCompat.setElevation(view, 10.0f);
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        mCtx.registerReceiver(receiver, new IntentFilter("MY_ACTIVITY")); // CHAT_LOBBY_FRAGMENT 이름의 리시버 인텐트 필터 등록
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mCtx);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new Chat_room_view_adapter(room_list, mCtx);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() { // 새로고침이 로그인할 때 한번, 바로 resume 되자마자 또 방 새로고침이 총 2번이 벌어지게 된다. 이렇다면 한번으로 줄여야 데이터 사용량이 적어질텐데. 방법을 강구하자. 나중에.(해결안됨)
        super.onResume();
        intent = new Intent("SOCKET_SERVICE");
        intent.putExtra("mode","refresh_room");
        mCtx.sendBroadcast(intent);
        Socket_Service.service_stop = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() { // 브로드캐스트 리시버 부분
        @Override
        public void onReceive(Context context, Intent intent) {
            receive_data = intent.getStringExtra("service_msg");
            StringTokenizer st = new StringTokenizer(receive_data, SEPARATOR);
            int command = Integer.parseInt(st.nextToken());

            switch (command) {
                case YES_LOGON: {
                    try {
                        StringTokenizer st1 = new StringTokenizer(st.nextToken(), DELIMETER);
                        int a = 0;
                        while (st1.hasMoreTokens()) {
                            String temp = st1.nextToken();
                            if (!temp.equals("empty")) {
                                StringTokenizer st2 = new StringTokenizer(temp, DELIMETER2);
                                String[] temp1 = new String[6];
                                while (st2.hasMoreTokens()) {
                                    String items = st2.nextToken();
                                    temp1[a] = items;
                                    a++;
                                }
                                room_list.add(new Chat_room(temp1[0], temp1[1], temp1[2], temp1[3], temp1[4], temp1[5]));
                                a = 0;
                            }
                        }
                    } catch (NoSuchElementException e) {
                        Log.wtf("엘리먼트가 없습니다.", e);
                    }
                    handler.post(new Runnable() { // 방목록이 리스트에 추가 되었으면 handler를 통해 어댑터를 notify 해줘야 겠지?
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;
                }
                case YES_ENTERROOM: { // 2021 코드를 받으면 (2021|방번호|방제목|아이디)
                    Intent intent2 = new Intent(mCtx, Chat_room_Activity.class);
                    intent2.putExtra("enter_room_number", st.nextToken());
                    intent2.putExtra("enter_room_name", st.nextToken());
                    mCtx.startActivity(intent2);
                    break;
                }
                case MDY_WAITINFO: { // 방 새로고침(나가거나, 만들거나 했을 때) 2013|
                    Socket_Service.ct_roomNumber = "0";
                    room_list.clear();
                    StringTokenizer st1 = new StringTokenizer(st.nextToken(), DELIMETER);
                    int a = 0;
                    while (st1.hasMoreTokens()) {
                        String temp = st1.nextToken();
                        if (!temp.equals("empty")) {
                            StringTokenizer st2 = new StringTokenizer(temp, DELIMETER2);
                            String[] temp1 = new String[6];
                            while (st2.hasMoreTokens()) {
                                String items = st2.nextToken();
                                temp1[a] = items;
                                a++;
                            }
                            room_list.add(new Chat_room(temp1[0], temp1[1], temp1[2], temp1[3], temp1[4], temp1[5]));
                            a = 0;
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;
                }
            }
        }
    };

}
