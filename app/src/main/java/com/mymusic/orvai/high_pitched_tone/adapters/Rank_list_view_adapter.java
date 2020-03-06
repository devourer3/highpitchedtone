package com.mymusic.orvai.high_pitched_tone.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mymusic.orvai.high_pitched_tone.API_Result.Game_high_pitch_challenge_rank_API_Result;
import com.mymusic.orvai.high_pitched_tone.Interface.ItemClickListener;
import com.mymusic.orvai.high_pitched_tone.R;
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;

import java.util.List;

/**
 * Created by orvai on 2018-02-01.
 */

public class Rank_list_view_adapter extends RecyclerView.Adapter<Rank_list_view_adapter.ViewHolder> {

    List<Game_high_pitch_challenge_rank_API_Result> pitch_rank_list;
    Context mCtx;

    public Rank_list_view_adapter(List<Game_high_pitch_challenge_rank_API_Result> pitch_rank_list, Context mCtx) {
        this.pitch_rank_list = pitch_rank_list;
        this.mCtx = mCtx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pitch_rank, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Game_high_pitch_challenge_rank_API_Result bind_rank_list = pitch_rank_list.get(position); // 랭크 리스트의 값을 포지션에 따라 리사이클러 뷰에 바인딩 시킴
        String user_id = SharedPrefManager.getInstance(mCtx).getUser().getUser_id(); // 유저 아이디 값을 셰어드 프리퍼런스에서 가져옴
        if(user_id.equals(bind_rank_list.getUser_id())){ // 유저아이디 값과 랭크 리스트 아이디 값이 같을 경우
            holder.rank_user_layout.setBackgroundResource(R.color.colorOrange); // 백그라운드 색을 오렌지 색으로 바꿈
        }
        holder.rank_user_score.setTypeface(null, Typeface.BOLD); // 랭크 리스트 점수값은 굵은 글씨체로
        if (position == 0) { // 랭크 리스트의 포지션 값이 0(=1위) 인 경우,
            holder.rank_user_note.setText(bind_rank_list.getPitch_note());
            holder.rank_user_id.setText(bind_rank_list.getUser_id());
            holder.rank_user_score.setText(bind_rank_list.getPitch_score()+"점");
            holder.rank_medal.setImageResource(R.drawable.high_pitch_challenge_activity_medal_gold_128);
            holder.rank_no.setText("1위");
            holder.rank_no.setTextColor(Color.RED);
        } else if (position == 1) { // 랭크 리스트의 포지션 값이 1(=2위) 인 경우,
            holder.rank_user_note.setText(bind_rank_list.getPitch_note());
            holder.rank_user_id.setText(bind_rank_list.getUser_id());
            holder.rank_user_score.setText(bind_rank_list.getPitch_score()+"점");
            holder.rank_medal.setImageResource(R.drawable.high_pitch_challenge_activity_medal_silver_128);
            holder.rank_no.setText("2위");
            holder.rank_no.setTextColor(Color.BLUE);
        } else if (position == 2) { // 랭크 리스트의 포지션 값이 2(=3위) 인 경우,
            holder.rank_user_note.setText(bind_rank_list.getPitch_note());
            holder.rank_user_id.setText(bind_rank_list.getUser_id());
            holder.rank_user_score.setText(bind_rank_list.getPitch_score()+"점");
            holder.rank_medal.setImageResource(R.drawable.high_pitch_challenge_activity_medal_bronze_128);
            holder.rank_no.setText("3위");
            holder.rank_no.setTextColor(Color.MAGENTA);
        } else { // 랭크 리스트의 포지션 값이 4위 이하인 경우
            holder.rank_user_note.setText(bind_rank_list.getPitch_note());
            holder.rank_user_id.setText(bind_rank_list.getUser_id());
            holder.rank_user_score.setText(bind_rank_list.getPitch_score()+"점");
            holder.rank_medal.setImageResource(R.drawable.high_pitch_challenge_activity_medal_default);
            holder.rank_no.setText("장려");
        }
    }

    @Override
    public int getItemCount() {
        return pitch_rank_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ItemClickListener itemClickListener;
        public TextView rank_no, rank_user_id, rank_user_note, rank_user_score;
        public ImageView rank_medal;
        public LinearLayout rank_user_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            rank_medal = itemView.findViewById(R.id.list_item_rank_medal);
            rank_no = itemView.findViewById(R.id.list_item_rank_no);
            rank_user_id = itemView.findViewById(R.id.list_item_rank_user_id);
            rank_user_note = itemView.findViewById(R.id.list_item_rank_note);
            rank_user_score = itemView.findViewById(R.id.list_item_rank_score);
            rank_user_layout = itemView.findViewById(R.id.user_layout);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }

}