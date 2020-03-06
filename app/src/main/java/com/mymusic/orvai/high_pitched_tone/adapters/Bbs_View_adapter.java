package com.mymusic.orvai.high_pitched_tone.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_View_API_Result;
import com.mymusic.orvai.high_pitched_tone.Bbs_view_activity;
import com.mymusic.orvai.high_pitched_tone.Interface.ItemClickListener;
import com.mymusic.orvai.high_pitched_tone.R;

import java.util.List;

/**
 * Created by orvai on 2018-02-01.
 */

public class Bbs_View_adapter extends RecyclerView.Adapter<Bbs_View_adapter.ViewHolder> {

    List<Bbs_View_API_Result> bbs_list;
    Context mCtx;

    public Bbs_View_adapter(List<Bbs_View_API_Result> bbs_list, Context mCtx) {
        this.bbs_list = bbs_list;
        this.mCtx = mCtx;
    }

    @Override
    public Bbs_View_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bbs, parent, false); // 레이아웃에
        return new ViewHolder(view); // 메소드를 거쳤다면 Viewholder에 view객체를 새로 생성토록 리턴
    }

    @Override
    public void onBindViewHolder(Bbs_View_adapter.ViewHolder holder, int position) { // Bbs_View_adapter.Message_ViewHolder 가 아닌 ViewHolder만 있어도 됨

        final Bbs_View_API_Result list = bbs_list.get(position);

        holder.t_bbs_no.setText(list.bbs_no);
        holder.t_bbs_subject.setText(list.bbs_subject);
        holder.t_bbs_user.setText(list.bbs_user);
        holder.t_bbs_comment.setText(list.bbs_comment);
        holder.t_bbs_rec.setText(list.bbs_rec);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                mCtx = view.getContext();
                if (!isLongClick) {
                    Intent intent = new Intent(mCtx, Bbs_view_activity.class);
                    intent.putExtra("request_no", list.bbs_no);
                    mCtx.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return bbs_list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ItemClickListener itemClickListener;
        public TextView t_bbs_no, t_bbs_subject, t_bbs_user, t_bbs_comment, t_bbs_rec;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            t_bbs_no = (TextView) itemView.findViewById(R.id.list_item_no);
            t_bbs_subject = (TextView) itemView.findViewById(R.id.list_item_subject);
            t_bbs_user = (TextView) itemView.findViewById(R.id.list_item_user);
            t_bbs_comment = (TextView) itemView.findViewById(R.id.list_item_comment);
            t_bbs_rec = (TextView) itemView.findViewById(R.id.list_item_like);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), true);
            return true;
        }

    }

}
