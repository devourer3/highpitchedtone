package com.mymusic.orvai.high_pitched_tone.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mymusic.orvai.high_pitched_tone.R;
import com.mymusic.orvai.high_pitched_tone.adapters.VocalTrainerAdapter;
import com.mymusic.orvai.high_pitched_tone.models.Course;
import com.mymusic.orvai.high_pitched_tone.models.Vocal_trainer;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Lecture extends Fragment {

    //variables
    private RecyclerView mRecyclerView;
    private VocalTrainerAdapter mAdapter;

    static private ArrayList<Vocal_trainer> vocaltrainers;
    static private ArrayList<Course> courses;
    static private ArrayList<Course> courses2;
    static private ArrayList<Course> courses3;
    static private ArrayList<Course> courses4;

    Context mCtx;

    public Lecture() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mCtx = getActivity();

        View view = inflater.inflate(R.layout.fragment_tab1_lecture, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        set_VocalTrainer();

        mAdapter = new VocalTrainerAdapter(vocaltrainers);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mCtx)); // 음....
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mAdapter.onRestoreInstanceState(savedInstanceState);
    }

    public void set_VocalTrainer() {

        vocaltrainers = new ArrayList<>();

        courses = new ArrayList<>();
        courses2 = new ArrayList<>();
        courses3 = new ArrayList<>();
        courses4 = new ArrayList<>();

        vocaltrainers.add(new Vocal_trainer("노래 쉽게 알려주는 5VOCAL", R.drawable.o_vocal, courses));
        courses.add(new Course("1강: 짧은시간에 있어보이는 노래 만들기!", "T3n0iwA60xA"));
        courses.add(new Course("2강: 바이브레이션 쉽게 배우기", "ffP1fJwhGQc"));
        courses.add(new Course("3강: 자기 목소리부터 찾고 시작합시다.(목에 힘빼고 기본보컬톤 찾기)", "oNIseb0RunM"));
        courses.add(new Course("4강: 고음을 정말 쉽게 내는 최고의 비법 - 야매고음", "sm7_jAiP7tE"));
        courses.add(new Course("5강: 바이브레이션 단 2분만에 배우기! (예시곡: 박효신-야생화)", "tfUnU-4_KYM"));
        courses.add(new Course("6강: 아니 대체 고음은 어떻게 하는거야?! - 고음완전이해편(성대 얇게 만들기)", "XGpeZVus9LU"));
        courses.add(new Course("7강: 고음성대(두성)를 정말 쉽게 만드는 꿀팁!", "6nj3g073pHU"));
        courses.add(new Course("8강: 고음발성의 악순환?! 내가 죽을 때까지 고음을 못하는 이유.", "j_l9L8bqiIs"));
        courses.add(new Course("9강: 고음을 정말 쉽게 내는 최고의 비법 - 야매고음2", "SPERTqbdme4"));
        courses.add(new Course("10강: 고음을 하고 싶으면 목의 힘부터 빼자 - 목에 힘빼는 법!", "qdHFbnxH3j0"));
        courses.add(new Course("11강: 호흡과 노래를 한번에!? - 1석2조 호흡연습법!", "8xPaYB6juH8"));
        courses.add(new Course("12강: 고음! 두성! 믹스보이스!...의 필수 조건, 좋은 가성 만들기!", "-GCR95Gp-cs"));
        courses.add(new Course("13강: 좋은 가성이 좋은 고음이 된다고!? - 좋은 고음이란?", "Efbt5aCijHs"));
        courses.add(new Course("14강: 압력만 있으면 고음을 편하게 낼 수 있다?!, \"저와 함께 압력 만들어 보쉴?\"", "G2SaGlbr5Nc"));


        vocaltrainers.add(new Vocal_trainer("바디사운드 발성전문학원", R.drawable.bodysound, courses2));
        courses2.add(new Course("1강: 고음, 두성 발성 - MixVoice(Humming scale)", "Zuc_PbQTJVY"));
        courses2.add(new Course("2강: 고음, 두성 Head voice 발성 - 1편", "QwMNgpLclDA"));
        courses2.add(new Course("3강: 고음, 두성 Head voice 발성 - 2편", "KaD-tinRjHM"));
        courses2.add(new Course("4강: 고음, 두성강좌 - 허밍 2 & 3단계 트레이닝(노래적용)", "zvaE6e22SMc"));
        courses2.add(new Course("5강: 고음, 두성 - Head Voice / Passagio 파사지오를 통한 두성 발성", "Fhnlhyx2c9o"));
        courses2.add(new Course("6강: 고음, 두성 발성 - 고음을 위한 '허밍 1단계'", "fIfpFTr2HTU"));
        courses2.add(new Course("7강: 고음, 두성 발성 - 고음을 위한 '허밍 2단계'", "_gQ8mJCIC44"));
        courses2.add(new Course("8강: 두성발성 그 오해와 진실", "zgso0670a7A"));
        courses2.add(new Course("9강: 두성 - 극고음 발성(Extreme High note / High G(3옥타브 솔))", "E5XECUtVtc4"));
        courses2.add(new Course("10강: 허밍프로그램 3단계 - 1편, 노래하듯 말하기", "JmJILYQ1a8E"));
        courses2.add(new Course("11강: 허밍프로그램 3단계 - 2편, 말하듯 노래하기 part.1", "hj1jKAEUVi0"));
        courses2.add(new Course("12강: 허밍프로그램 3단계 - 3편, 말하듯 노래하기 part.2", "x9_7JA3wkMc"));
        courses2.add(new Course("13강: 허밍프로그램 3단계 - 4편, 말하듯 노래하기 part.3", "R-2aSOknjtE"));
        courses2.add(new Course("14강: 허밍프로그램 - 보충강의", "xp056DUkJPs"));
        courses2.add(new Course("15강: 반가성, 믹스보이스, 두성 - 올바로 이해하면 쉽다!!!", "ZWn2cGYBO9Y"));
        courses2.add(new Course("16강: 목을 망치는 최악의 고음!!!", "4p3gonGS-x4"));
        courses2.add(new Course("17강: 이런 방법으로 '고음' 내면 100% 폭망!!!!!", "RlyhErLoXGk"));


        vocaltrainers.add(new Vocal_trainer("보컬밸런스(VocalBalance)", R.drawable.vocal_balance, courses3));
        courses3.add(new Course("1강: 개개인의 보컬밸런스를 위하여 - 호흡편", "CI5gTMgNzIo"));
        courses3.add(new Course("2강: 개개인의 보컬밸런스를 위하여 - 연습편", "-_3dps7MZeQ"));
        courses3.add(new Course("3강: 개개인의 보컬밸런스를 위하여 - 후두위치", "Uz6Ks7HSBGk"));
        courses3.add(new Course("4강: 개개인의 보컬밸런스를 위하여 - 음성질환", "Z0XcWLXC8RU"));
        courses3.add(new Course("5강: 개개인의 보컬밸런스를 위하여 - 가성의 종류", "-xn8q9Rw--w"));
        courses3.add(new Course("6강: 개개인의 보컬밸런스를 위하여 - 목이 좁은소리, 열린소리", "E4e0ypNdzoA"));
        courses3.add(new Course("7강: 개개인의 보컬밸런스를 위하여 - 가슴에 댄 소리", "kHmV_q79mHM"));
        courses3.add(new Course("8강: 개개인의 보컬밸런스를 위하여 - 흉성과 중성, 피치의 개념", "3Cs2P_iAdXo"));
        courses3.add(new Course("9강: 고음 잘하는 방법? 중음부터 연습하라!", "VVhLdw1YfMw"));
        courses3.add(new Course("10강: 워밍업(목풀기)에 대하여.", "3G68XSYCrp4"));
        courses3.add(new Course("11강: SoulJoe와 함께하는 샤우팅 강좌!", "Qx_TEaOa8ME"));
        courses3.add(new Course("12강: 딘, 크러쉬같은 창법은 어떻게 접근해야 하나요?", "d-S2YVpwSjM"));
        courses3.add(new Course("13강: 고음(두성) 잘하는법! 실전 Part.1 - 얇은소리", "k0U_EfXyvL8"));
        courses3.add(new Course("14강: 음이탈(삑사리)이 나는 이유!", "fOTvvnkS-f8"));

        vocaltrainers.add(new Vocal_trainer("HamVoice! 보컬트레이너 함현철", R.drawable.ham_voice, courses4));
        courses4.add(new Course("1강: 함현철의 보컬레슨 - '말하듯 노래하라' 뜻과 숨은 오류", "kct3DmHx28E"));
        courses4.add(new Course("2강: 함현철의 보컬레슨 - 발성을 쉽게 알아보자", "r8AlHOnAG6o"));
        courses4.add(new Course("3강: 함현철의 보컬레슨 - 고음을 낼 때 필요한 호흡은?", "fwxab3l9GIg"));
        courses4.add(new Course("4강: 함현철의 보컬레슨 - 고음으로연결되는 저음", "WxMn0duAXAA"));
        courses4.add(new Course("5강: 함현철의 보컬레슨 - 가성을 진성으로 만들어보자!", "prm6GHddBCQ"));
        courses4.add(new Course("6강: 함현철의 보컬레슨 - 호흡의 속도 조절", "Y7RaJUUIC1Y"));
        courses4.add(new Course("7강: 함현철의 보컬레슨 - (믹스보이스?) 박효신의 '야생화·숨'의 고음은?", "smX0ozhnV4g"));
        courses4.add(new Course("8강: 함현철의 보컬레슨 - 목풀기와 고음을위한 성대훈련", "IdcFiYFNXNg"));
        courses4.add(new Course("9강: 함현철의 보컬레슨 - 두성이 되는 가성", "bG7IdQ5lDvQ"));
        courses4.add(new Course("10강: 함현철의 보컬레슨 - 샤우팅을 알아보자!! 꺄오~! (cf.샤우팅은 두성일까요?)", "vC_8PyGBg5E"));

    }

}