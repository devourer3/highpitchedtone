package com.mymusic.orvai.high_pitched_tone.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestartService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("ACTION_RESTART_Socket_Service")){ // 해당 이름(ACTION_RESTART...)으로 등록된 인텐트 명으로 활성 되었을 때,
            Intent i = new Intent(context, Socket_Service.class);  // Socket_Service 클래스를 실행 시킴.
            context.startService(i); // Socket_Service 서비스 실행
        }
    }
}