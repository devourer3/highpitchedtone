package com.mymusic.orvai.high_pitched_tone.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_Comment_delete_API_Result;
import com.mymusic.orvai.high_pitched_tone.API_Result.Bbs_Comment_view_API_Result;
import com.mymusic.orvai.high_pitched_tone.Bbs_view_activity;
import com.mymusic.orvai.high_pitched_tone.EventBus.BusProvider;
import com.mymusic.orvai.high_pitched_tone.Interface.API_Service;
import com.mymusic.orvai.high_pitched_tone.Interface.ItemClickListener;
import com.mymusic.orvai.high_pitched_tone.R;
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;
import com.mymusic.orvai.high_pitched_tone.models.URLs;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by orvai on 2018-02-01.
 */

public class Bbs_Comment_View_adapter extends RecyclerView.Adapter<Bbs_Comment_View_adapter.ViewHolder> {

    List<Bbs_Comment_view_API_Result> bbs_comment_list;
    Context mCtx;
    AlertDialog.Builder builder;
    Retrofit retrofit;
    API_Service service;

    public Bbs_Comment_View_adapter(List<Bbs_Comment_view_API_Result> bbs_comment_list, Context mCtx) {
        this.bbs_comment_list = bbs_comment_list;
        this.mCtx = mCtx;
    }


    @Override
    public Bbs_Comment_View_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final Bbs_Comment_View_adapter.ViewHolder holder, final int position) {

        String user_id = SharedPrefManager.getInstance(mCtx).getUser().getUser_id();
        final Bbs_Comment_view_API_Result comment_p = bbs_comment_list.get(position);

        builder = new AlertDialog.Builder(holder.itemView.getContext(), R.style.Theme_AppCompat_Light_Dialog); // 다이얼로그를 액티비티에 띄우게 하려면 밑의 Viewholder에 있는 itemView 객체를 통해 액티비티의 컨텍스트를 가져와야 함
        retrofit = new Retrofit.Builder().baseUrl(URLs.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(API_Service.class);

        holder.com_user.setText(comment_p.com_user);
        holder.com_content.setText(comment_p.com_content);
        holder.com_date.setText(comment_p.com_date);
        if(!comment_p.com_user.equals(user_id)) {
            holder.delete_com.setVisibility(View.INVISIBLE);
        }

        final String comment_number = comment_p.com_no;

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(!isLongClick){
                }
            }
        });

        holder.delete_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setIcon(R.drawable.delete_com);
                builder.setTitle("댓글 삭제");
                builder.setMessage("댓글을 삭제하시겠습니까?");
                builder.setNegativeButton("취소",null);
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Call<Bbs_Comment_delete_API_Result> result = service.Bbs_comment_delete(comment_number, Bbs_view_activity.request_no);
                        result.enqueue(new Callback<Bbs_Comment_delete_API_Result>() {
                            @Override
                            public void onResponse(Call<Bbs_Comment_delete_API_Result> call, Response<Bbs_Comment_delete_API_Result> response) {
                                Toast.makeText(mCtx,"댓글삭제",Toast.LENGTH_SHORT).show();
                                BusProvider.getInstance().post("delete_comment"); // Bbs_view_activity로 이벤트 버스 보냄
                            }
                            @Override
                            public void onFailure(Call<Bbs_Comment_delete_API_Result> call, Throwable t) {
                            }
                        });
                    }
                });
                builder.create();
                builder.show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return bbs_comment_list.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ItemClickListener itemClickListener;
        public TextView com_user;
        public TextView com_content;
        public TextView com_date;
        public ImageView delete_com;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            com_user = itemView.findViewById(R.id.list_item_com_user_id);
            com_content = itemView.findViewById(R.id.list_item_com_content);
            com_date = itemView.findViewById(R.id.list_item_com_date);
            delete_com = itemView.findViewById(R.id.list_item_delete_btn);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
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