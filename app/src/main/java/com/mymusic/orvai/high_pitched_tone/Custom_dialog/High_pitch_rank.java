package com.mymusic.orvai.high_pitched_tone.Custom_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mymusic.orvai.high_pitched_tone.API_Result.Game_high_pitch_challenge_rank_API_Result;
import com.mymusic.orvai.high_pitched_tone.Interface.API_Service;
import com.mymusic.orvai.high_pitched_tone.R;
import com.mymusic.orvai.high_pitched_tone.adapters.Rank_list_view_adapter;
import com.mymusic.orvai.high_pitched_tone.models.URLs;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by orvai on 2018-02-25.
 */

public class High_pitch_rank extends Dialog {

    private Context mCtx;
    private Retrofit retrofit;
    private API_Service service;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Game_high_pitch_challenge_rank_API_Result> rank_list;

    public High_pitch_rank(@NonNull Context context) {
        super(context);
        this.mCtx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_rank_dialog);
        recyclerView = findViewById(R.id.rank_list_recycler);
        rank_list = new ArrayList<>();

        retrofit = new Retrofit.Builder().baseUrl(URLs.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(API_Service.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(mCtx));
        view_rank();
    }

    private void view_rank() {
        Call<List<Game_high_pitch_challenge_rank_API_Result>> result = service.request_pitch_rank();
        result.enqueue(new Callback<List<Game_high_pitch_challenge_rank_API_Result>>() {
            @Override
            public void onResponse(Call<List<Game_high_pitch_challenge_rank_API_Result>> call, Response<List<Game_high_pitch_challenge_rank_API_Result>> response) {
                adapter = new Rank_list_view_adapter(rank_list, mCtx);
                rank_list.addAll(response.body()); // 서버에서 받아온 고음 서열 순위의 값을 리스트에 모두 추가시킴.
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<List<Game_high_pitch_challenge_rank_API_Result>> call, Throwable t) {
                Log.e("서버에러", String.valueOf(t.getCause()));
            }
        });
    }

}
