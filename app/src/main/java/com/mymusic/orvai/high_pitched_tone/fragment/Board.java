package com.mymusic.orvai.high_pitched_tone.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mymusic.orvai.high_pitched_tone.Bbs_write_activity;
import com.mymusic.orvai.high_pitched_tone.R;
import com.mymusic.orvai.high_pitched_tone.Interface.API_Service;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_View_API_Result;
import com.mymusic.orvai.high_pitched_tone.Utils.EndlessRecyclerViewScrollListener;
import com.mymusic.orvai.high_pitched_tone.adapters.Bbs_View_adapter;
import com.mymusic.orvai.high_pitched_tone.models.URLs;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
public class Board extends Fragment {

    private Context mCtx;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    Retrofit retrofit;
    API_Service service;
    int bbs_page = 0;
    List<Bbs_View_API_Result> bbs_list;


    public Board() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2_board, container, false);
        mCtx = getActivity();
        fab = view.findViewById(R.id.write_fab);
        recyclerView = view.findViewById(R.id.bbs_recycler);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mCtx, Bbs_write_activity.class));
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        bbs_page = 0;
        bbs_list.clear();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bbs_list = new ArrayList<>();
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
                view.setBackgroundColor(0xFFECE9E9);
                ViewCompat.setElevation(view, 10.0f);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onResume() {
        super.onResume();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mCtx);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new Bbs_View_adapter(bbs_list, mCtx);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                bbs_page += 6;
                paging(bbs_page);
            }
        });
        paging(bbs_page);
        recyclerView.setAdapter(adapter);
    }

    private void paging(final int page) {
        retrofit = new Retrofit.Builder().baseUrl(URLs.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(API_Service.class);
        Call<List<Bbs_View_API_Result>> call = service.getBbs_view(page); // 페이징 할 수
        call.enqueue(new Callback<List<Bbs_View_API_Result>>() {
            @Override
            public void onResponse(Call<List<Bbs_View_API_Result>> call, Response<List<Bbs_View_API_Result>> response) {
                bbs_list.addAll(response.body());
                adapter.notifyItemRangeChanged(adapter.getItemCount(), bbs_list.size() - 1);
//                Toast.makeText(mCtx, "현재" + (bbs_page / 6) + "페이지 입니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Bbs_View_API_Result>> call, Throwable t) {
            }
        });
    }

}
