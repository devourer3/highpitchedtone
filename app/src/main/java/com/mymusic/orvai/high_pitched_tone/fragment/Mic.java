package com.mymusic.orvai.high_pitched_tone.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mymusic.orvai.high_pitched_tone.Mic_high_challenge;
import com.mymusic.orvai.high_pitched_tone.Perfect_singer_main;
import com.mymusic.orvai.high_pitched_tone.Pitch_ear_test_Activity;
import com.mymusic.orvai.high_pitched_tone.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Mic extends Fragment {

    Context mCtx;
    ImageView challnege, pitch_test;

    public Mic() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab4_mic, container, false);
        mCtx = view.getContext();
        view.findViewById(R.id.high_challenge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mCtx, Mic_high_challenge.class));
            }
        });
        view.findViewById(R.id.pitch_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mCtx, Pitch_ear_test_Activity.class));
            }
        });
        view.findViewById(R.id.perfect_singer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mCtx, Perfect_singer_main.class));
            }
        });

        return view;
    }

}
