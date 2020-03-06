package com.mymusic.orvai.high_pitched_tone.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.mymusic.orvai.high_pitched_tone.Chat_Sqlite.Chat_database_Helper;
import com.mymusic.orvai.high_pitched_tone.Chat_room_Activity;
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;
import com.mymusic.orvai.high_pitched_tone.models.Chat_msg;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.StringTokenizer;

public class Socket_Service extends Service {

    private Socket c_socket; // 클라이언트 소켓
    private DataInputStream dis;
    private DataOutputStream dos;
    private int ServerPort = 7777; // 채팅서버 포트번호
//    private String Server_Ip = "13.124.236.98"; // AWS를 이용하여 채팅서버 연결
    private String Server_Ip = "192.168.0.5"; // 채팅서버 테스트를 위한 로컬 아이피
    private StringBuffer ct_buffer; // 채팅서버로부터 받은 String 값을 나누기 위한 buffer 값
    private Bitmap bitmap; // 채팅서버로부터 받은 이미지 Base64 값을 Bitmap으로 변화시키기 위한 전역변수

    public NotificationManager nm; // 죽지않는 서비스와, 채팅 메시지 알람을 관리하기 위한 노티피케이션 매니저

    public static boolean service_destroy = false; // 로그아웃을 했을 때 서비스를 완전히 종료시키기 위한 불린 값
    public static boolean service_stop = false; // 태스크 킬을 하였을 때 서비스를 노티피케이션으로 올리기 위한 불린 값

    private static String receive_data, user_id, user_pic_url; // 채팅 서버로부터 받은 String값, 클라이언트 유저 아이디 값, 클라이언트 유저 프로필 사진 값
    public static String ct_roomNumber = "0"; // 나의 방번호
    private static String ct_roomName = ""; // 나의 방이름
    private Client_thread ct; // 클라이언트 스레드 값
    private Chat_database_Helper helper; // SQlite를 이용하기 위한 Helper값
    private Notification.Builder chat_noti_builder; // 채팅 메시지 노티피케이션을 만들기 위한 빌더

    private static final String SEPARATOR = "|"; // TCP/IP 통신 중 구분자1
    private static final String DELIMETER = "'"; // TCP/IP 통신 중 구분자2

    private static final int REQ_LOGON = 1001; // 채팅 서버 로그인 코드
    private static final int REQ_CREATEROOM = 1011; // 채팅 방 만들기 요청 코드
    private static final int REQ_ENTERROOM = 1021; // 채팅 방 입장 요청 코드
    private static final int REQ_ENTERLOBBY = 1030; // 백스페이스 요청 코드
    private static final int REQ_QUITROOM = 1031; // 방 나가기 요청 코드
    private static final int REQ_LOGOUT = 1041; // 로그아웃 했을 때 요청 코드
    private static final int REQ_SENDWORD = 1051; // 메시지 요청코드
    private static final int REQ_REFRESH_ROOM = 1071; // 뷰페이저를 통해 이동했을 때

    private static boolean flag_Connection; // 채팅 클라이언트를 실행했을 때, 무한 반복되는 클라이언트 Datainputstream 스레드를 시작/종료하기 위한 불린 값


    @Override
    public void onCreate() {
        // startservice를 처음 사용할 경우 액티비티에서 startService를 실행할 경우 처음 호출.
        // bindservice를 처음 사용할 경우, 액티비티에서 bindService를 실행할 경우 처음 호출.
        // 단 서비스는 싱글톤이기 때문에 어플리케이션에서 실행된 경우 onCreate는 한번밖에 호출되지 않는다.
        // onCreate에서 생성자를 통해 클라이언트 소켓 스레드를 무한 시작함과 동시에 로그인까지 시도하겠다.
        super.onCreate();
        service_destroy = false;
        flag_Connection = true;
        user_id = SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_id();
        user_pic_url = SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_pic();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ct = new Client_thread(); // 소켓 스레드 생성
                ct.start(); // 소켓 스레드 시작
                ct.request_Logon(user_id, user_pic_url); // 서버에 내 아이디를 매개변수, 사진 URL를 매개변수로 하여 로그인 요청을 보냄
                ct_roomNumber = "0"; // 클라이언트 전역 변수 방번호를 0으로 바꿈(현재 로비에 있으니까)
            }
        }).start();
        registerReceiver(broadcastReceiver, new IntentFilter("SOCKET_SERVICE")); // 액티비티로부터 메시지를 받을 SOCKET_SERVICE란 이름의 브로드캐스트 리시버 등록
        helper = new Chat_database_Helper(this);
        chat_noti_builder = new Notification.Builder(this);
        unregisterRestartAlarm();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // startservice 를 두번 째 이후로 사용할 경우 호출. 여러 액티비티에서 서비스를 반복하여 사용하고자 하는 경우 이것이 호출 됨
        service_destroy = false;
        ct_roomNumber = "0";
        startForeground(1, new Notification());
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        notification = new Notification.Builder(getApplicationContext()).setContentTitle("").setContentText("").build();
        // setsmallicon을 안했기 때문에 노티 자체가 안만들어져서 꼼수로 노티가 안보이게 함
        nm.notify(1, notification);
//        nm.cancel(1); 내 노티피 케이션은 영구적 노티피케이션이기 때문에 cancel를 실행할 경우 서비스가 죽어버림
        return super.onStartCommand(intent, flags, startId);
    }

    private void registerRestartAlarm() { // 태스크 킬을 하였을 때, onTaskRemoved 메소드가 실행된 경우, RestartService 클래스를 통해 서비스를 다시 실행하기 위한 알람 메소드
        Intent intent = new Intent(Socket_Service.this, RestartService.class);
        intent.setAction("ACTION_RESTART.Socket_Service");
        PendingIntent sender = PendingIntent.getBroadcast(Socket_Service.this, 0, intent, 0);
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 1 * 1000;
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1 * 1000, sender);
    }

    private void unregisterRestartAlarm() { // 다시 액티비티를 실행했을 때, 알람을 해제하는 메소드
        Intent intent = new Intent(Socket_Service.this, RestartService.class);
        intent.setAction("ACTION.RESTART.Socket_Service");
        PendingIntent sender = PendingIntent.getBroadcast(Socket_Service.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        /**
         * 알람 취소
         */
        alarmManager.cancel(sender);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) { // 태스크 킬을 하였을 때 알람을 등록하기 위한 메소드
        super.onTaskRemoved(rootIntent);
        service_stop = true;
        registerRestartAlarm();
    }

    @Override
    public void onDestroy() { // bindservice, startservice에서 stopService 메소드를 호출하여 모든 서비스가 중지될 때 실행되는 콜백 메소드... 나중에 로그아웃 했을 때 여기다가 소켓 통신을 종료하도록....(완료)
        super.onDestroy();
        if (service_destroy == true) {
            try {
                c_socket.close(); // 소켓 닫음
                flag_Connection = false; // 스레드 정지
                dis.close(); // datainputstream 닫음
                dos.close(); // dataoutputstream 닫음
                ct = null;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    c_socket.close(); // 소켓 마지막으로 확인차 한번 더 닫음
                    dis.close(); // datainputstream 확인차 한번 더 닫음
                    dos.close(); // dataoutputstream 확인차 한번 더 닫음
                } catch (IOException e) {
                    e.printStackTrace();
                }
                flag_Connection = false; // 스레드 마지막으로 정지
                ct = null; // 현재 스레드는 null값으로 지정
            }
            unregisterReceiver(broadcastReceiver); // 브로드캐스트 리시버 등록 해제
        }
    }

    @Override
    public void onLowMemory() { // 메모리 적을 때 쓰이는 메소드... 나중에 생각해보자... 바로 onDestroy를 호출하도록 할 것인지...
        super.onLowMemory();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // bindservice 를 두번 째 이후로 사용할 경우 호출. 여러 액티비티에서 서비스를 사용하고자 하는 경우 이것이 호출 됨
        // 안 씀
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // bindservice 를 사용할 경우, 서비스 실행중이었던 걸 unbindservice를 실행했을 때 ondestroy전에 호출
        // 안 씀
        return super.onUnbind(intent);
    }

    /**
     * 여기부터 여러 액티비티로 부터 메시지를 받는 브로드캐스트를 정의함.
     */

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // 방 생성 정보, 메시지 정보, 파일 정보 등을 액티비티로부터 전달받고, 서버로 전송할 브로드캐스트 리시버
        @Override
        public void onReceive(Context context, final Intent intent) {
            String mode = intent.getStringExtra("mode");
            ct_roomNumber = intent.getStringExtra("ct_room_number");
            if (mode != null) {
                switch (mode) { // 여기부터 액티비티로 부터 요청받은 것들(REQUEST CODE)을 서버로 전송할 목록들
                    case "refresh_room": { // 방 목록 갱신 요청
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ct.request_refresh();
                            }
                        }).start();
                        break;
                    }
                    case "create_room": { // 방 생성 요청
                        final String create_room_name = intent.getStringExtra("room_subject");
                        String create_room_maxuser_tmp = intent.getStringExtra("room_maxuser");
                        final int create_room_maxuser = Integer.parseInt(create_room_maxuser_tmp);
                        final int create_room_islock = intent.getIntExtra("room_islock", 0);
                        final String create_room_password = intent.getStringExtra("room_password");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ct.request_Create_Room(create_room_name, create_room_maxuser, create_room_islock, create_room_password);
                            }
                        }).start();
                        break;
                    }
                    case "logout": { // 로그아웃 요청
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ct.request_Logout();
                            }
                        }).start();
                        break;
                    }
                    case "enter_lobby": { // 대기실 입장 요청
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ct_roomNumber = intent.getStringExtra("quit_lobby");
                                ct.request_Enter_lobby();
                            }
                        }).start();
                        break;
                    }
                    case "enter_room": { // 방 들어가기 요청
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String temp_room_number = intent.getStringExtra("room_number");
                                String temp_room_name = intent.getStringExtra("room_name");
                                ct.request_Enter_Room(temp_room_number, String.valueOf(0), temp_room_name);
                            }
                        }).start();
                        break;
                    }
                    case "quit_room": { // 방 나가기 요청
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String room_number = intent.getStringExtra("quit_room_number");
                                ct.request_Quit_Room(room_number);
                            }
                        }).start();
                        break;
                    }
                    case "sending_msg": { // 메시지 보내는 요청
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String c_msg = intent.getStringExtra("msg");
                                String room_name = intent.getStringExtra("msg_room_name");
                                String room_number = intent.getStringExtra("msg_room_number");
                                ct_roomNumber = room_number;
                                ct.request_Sending_msg(c_msg, room_number, room_name);
                            }
                        }).start();
                        break;
                    }
                    case "sending_image": { // 이미지 보내는 요청
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String tmp_image = intent.getStringExtra("image");
                                String room_number = intent.getStringExtra("msg_room_number");
                                String room_name = intent.getStringExtra("msg_room_name");

                                Uri uri = Uri.parse(tmp_image);
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos); // 2번째 매개변수가 이미지 압축률 조절(0%~100%)
                                byte[] imgBytes = bos.toByteArray();
                                String image = Base64.encodeToString(imgBytes, Base64.DEFAULT);
                                ct.request_Sending_msg(image, room_number, room_name);
                            }
                        }).start();
                    }
                }
            }
        }
    };

    class Client_thread extends Thread {

        public Client_thread() { // 클라이언트 소켓 connect를 최초로 실행하기 위한 생성자, 74번 째 줄에서 ct = new Client_thread() 를 통하여 생성할 때 생성.
            try {
                c_socket = new Socket(); // 클라이언트 소켓을 생성한다.
                SocketAddress socket_addr = new InetSocketAddress(Server_Ip, ServerPort); // 서버 아이피와 서버 포트를 매개변수로 얻은 소켓 주소를 생성한다.
                c_socket.connect(socket_addr, 10000000); // 얻은 주소를 매개변수로 하여 소켓을 서버에 연결한다.
                dis = new DataInputStream(c_socket.getInputStream()); // 서버로부터 메시지를 보낼 DataInputSteram을 생성한다.
                dos = new DataOutputStream(c_socket.getOutputStream()); // 서버에 메시지를 받을 DataOutputStream을 생성한다.
                ct_buffer = new StringBuffer(8192); // 구분자를 기준으로 하는 메시지를 전송하기 위해 StringBuffer를 생성한다.
            } catch (IOException e) {
                e.printStackTrace();
                Log.wtf("서버오류", e);
            }
        }

        @Override
        public void run() {
            while (flag_Connection) { // 서버와 연결된 이후, 서버로부터 0.2초마다 메시지를 받는 스레드. 로그아웃 하여 서비스가 destroy 될 때까지 계속 실행된다.
                try {
                    receive_data = dis.readUTF();
                    Log.d("kb: 서버로 받은 RAW 데이터", receive_data);
                    Intent intent = new Intent("MY_ACTIVITY"); // 특정 액티비티에 등록된 브로드캐스트 리시버 명으로 서버로부터 수신된 메시지를 전달하기 위함
                    intent.putExtra("service_msg", receive_data);
                    sendBroadcast(intent);
                    StringTokenizer st = new StringTokenizer(receive_data, SEPARATOR);
                    String is_msg = st.nextToken();
                    if (is_msg.equals("2051")) { // 그냥 메시지인 경우
                        String chat_user_id = st.nextToken();
                        String chat_user_pic = st.nextToken();
                        String roomNumber = st.nextToken();
                        String chat_time = st.nextToken();
                        String chat_msg = st.nextToken();
                        int chat_image_whether;
                        if (chat_msg.length() >= 1000) {
                            chat_image_whether = 1;
                        } else {
                            chat_image_whether = 0;
                        }
                        String room_name = st.nextToken();
                        Chat_msg chat_object = new Chat_msg(roomNumber, chat_user_id, chat_msg, chat_user_pic, 3, chat_time, chat_image_whether); // 룸넘버, 유저아이디, 메시지, 유저사진주소, 입퇴장메시지여부, 서버시간, 이미지 여부
                        helper.insert_chat_data(chat_object);
                        if (!user_id.equals(chat_user_id) && !ct_roomNumber.equals(roomNumber)) { // 내 아이디와 같을 경우 또, 클라이언트 방 번호와 일치하지 않은 경우만 노티피 보냄
                            Intent intent2 = new Intent("SOCKET_SERVICE");
                            intent2.putExtra("mode", "enter_room");
                            intent2.putExtra("room_number", roomNumber);
                            intent2.putExtra("room_name", room_name);
                            PendingIntent pintent = PendingIntent.getBroadcast(getApplicationContext(), 5678, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                            chat_noti_builder.setSmallIcon(android.R.drawable.btn_star_big_on).setContentTitle(chat_user_id).setContentText(chat_msg).setAutoCancel(true).setContentIntent(pintent);
                            NotificationManager notify_mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            notify_mgr.notify(200, chat_noti_builder.build());
                        }
//                        Glide.with(getApplicationContext()).asBitmap().load(URLs.URL_IMAGE_ROOT + chat_user_pic).into(new SimpleTarget<Bitmap>() { //오류발생 ㅅㅂ?????
//                            @Override
//                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                Bitmap bitmap = ImageUtils.getCircularBitmap(resource);
//
//                                chat_noti_builder.setLargeIcon(bitmap);
//                            }
//                        });
                    } else if (is_msg.equals("2023")) { // 입퇴장 메시지인 경우
                        String chat_user_id = st.nextToken();
                        String roomNumber = st.nextToken();
                        int code = Integer.parseInt(st.nextToken());
                        Chat_msg chat_object = new Chat_msg(roomNumber, chat_user_id, null, null, code, null, 0); // 룸넘버, 유저아이디, 메시지, 유저사진주소, 입퇴장메시지여부(0퇴장, 1입장), 서버시간, 이미지 여부
                        helper.insert_chat_data(chat_object);
                    } else if (is_msg.equals("2041")) { // 로그아웃
                        service_destroy = true;
                        Intent intent1 = new Intent(getApplicationContext(), Socket_Service.class);
                        stopService(intent1);
                    } else if (is_msg.equals("2021")) { // 방입장 YES 메시지 -> 2021|방번호|방제목|아이디
                        ct_roomNumber = st.nextToken(); // 클라이언트 스레드의 방 번호를 바꿈. 노티피로 챗방을 입장하거나, 방목록에서 챗방을 입장하거나 동일.
                        ct_roomName = st.nextToken();
                        if (!SharedPrefManager.getInstance(getApplicationContext()).isMyroom(String.valueOf(ct_roomNumber))) {
                            SharedPrefManager.getInstance(getApplicationContext()).saveMyroom(String.valueOf(ct_roomNumber), ct_roomName);
                        }
                        if(service_stop == true) { // 지금 프로세스가 종료중이라면,
                            Intent intent2 = new Intent(getApplicationContext(), Chat_room_Activity.class);
                            intent2.putExtra("enter_room_name", ct_roomName);
                            intent2.putExtra("enter_room_number", ct_roomNumber);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent2);
                        }
                    } else if(is_msg.equals("2005")){ // 로비로 들어오기 허가
                        ct_roomNumber = st.nextToken();
                    } else if(is_msg.equals("2011")){ // 방을 만들고 그 방에 들어가기 허가
                        ct_roomNumber = st.nextToken();
                        ct_roomName = st.nextToken();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 여기부턴 클라이언트 스레드를 통해 서버에 요청을 보내는 메소드들
         */

        public void request_Logon(String id, String pic_url) { // 채팅 서버에 로그인 요청을 보내는 메소드
            try {
                ct_buffer.setLength(0);
                ct_buffer.append(REQ_LOGON);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(id);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(pic_url);
                send(ct_buffer.toString());
            } catch (IOException e) {
                Log.e("logon_IOException", String.valueOf(e));
            }
        }

        public void request_Enter_lobby() { // 백스페이스를 눌렀을 때, 채팅 서버에 이전 화면으로 넘기는 요청을 하는 메소드
            try {
                ct_buffer.setLength(0);
                ct_buffer.append(REQ_ENTERLOBBY);
                ct_buffer.append(SEPARATOR);
                send(ct_buffer.toString());
            } catch (IOException e) {
                Log.e("enter_lobby_IOException", String.valueOf(e));
            }
        }

        public void request_refresh() { // 탭 레이아웃을 이용하여 화면을 전환할 때, 채팅방에 채팅방이 새로고침되는 요청을 보내는 메소드
            try {
                ct_buffer.setLength(0);
                ct_buffer.append(REQ_REFRESH_ROOM);
                ct_buffer.append(SEPARATOR);
                send(ct_buffer.toString());
            } catch (IOException e) {
                Log.e("refresh_IOException", String.valueOf(e));
            }
        }

        public void request_Logout() { // 로그아웃을 했을 때, 채팅 서버에 로그아웃 했다는 요청을 보내는 메소드
            try {
                ct_buffer.setLength(0);
                ct_buffer.append(REQ_LOGOUT);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(user_id);
                send(ct_buffer.toString());
            } catch (IOException e) {
                Log.e("logout_IOException", String.valueOf(e));
            }
        }

        public void request_Enter_Room(String roomNumber, String password, String roomName) { // 선택한 채팅방에 들어가기 위한 요청을 채팅 서버에 보냄
            try {
                ct_buffer.setLength(0);
                ct_buffer.append(REQ_ENTERROOM);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(user_id);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(roomNumber);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(password);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(user_pic_url);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(roomName);
                send(ct_buffer.toString());
            } catch (IOException e) {
                Log.e("enter_room_IOException", String.valueOf(e));
            }
        }

        public void request_Quit_Room(String roomNumber) { // 방을 나가기 위한 요청코드를 채팅 서버에 보냄
            try {
                ct_buffer.setLength(0);
                ct_buffer.append(REQ_QUITROOM);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(user_id);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(roomNumber);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(user_pic_url);
                send(ct_buffer.toString());
            } catch (IOException e) {
                Log.e("quit_room_IOException", String.valueOf(e));
            }
        }

        public void request_Create_Room(String roomName, int roomMaxUser, int isLock, String password) { // 방을 생성하기 위한 요청 코드를 채팅 서버에 보냄
            try {
                ct_buffer.setLength(0);
                ct_buffer.append(REQ_CREATEROOM);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(user_id); // 이건 채팅방의 방장이 누구인가 구분하기 위한 아이디 값.
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(roomName);
                ct_buffer.append(DELIMETER);
                ct_buffer.append(roomMaxUser);
                ct_buffer.append(DELIMETER);
                ct_buffer.append(isLock);
                ct_buffer.append(DELIMETER);
                ct_buffer.append(password);
                send(ct_buffer.toString());
            } catch (IOException e) {
                Log.e("create_room_IOException", String.valueOf(e));
            }
        }

        public void request_Sending_msg(String msg, String roomNumber, String roomName) { // 메시지를 해당 채팅방에 보내기 위한 요청 코드를 채팅 서버에 보냄
            try {
                ct_buffer.setLength(0);
                ct_buffer.append(REQ_SENDWORD);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(user_id);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(user_pic_url);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(roomNumber);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(msg);
                ct_buffer.append(SEPARATOR);
                ct_buffer.append(roomName);
                send(ct_buffer.toString());
            } catch (IOException e) {
                Log.e("Sending_msg_Exception", String.valueOf(e));
            }
        }

        private void send(String sendData) throws IOException { // DataoutputStream을 이용하여 채팅서버에 모든 요청코드들을 보냄
            dos.writeUTF(sendData);
            dos.flush();
        }

    }


}
