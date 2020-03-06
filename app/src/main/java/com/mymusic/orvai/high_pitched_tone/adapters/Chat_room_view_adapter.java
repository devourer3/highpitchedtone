package com.mymusic.orvai.high_pitched_tone.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mymusic.orvai.high_pitched_tone.Chat_room_Activity;
import com.mymusic.orvai.high_pitched_tone.Interface.ItemClickListener;
import com.mymusic.orvai.high_pitched_tone.R;
import com.mymusic.orvai.high_pitched_tone.Shared_Preference.SharedPrefManager;
import com.mymusic.orvai.high_pitched_tone.models.Chat_room;

import java.util.List;

/**
 * Created by orvai on 2018-02-17.
 */

public class Chat_room_view_adapter extends RecyclerView.Adapter<Chat_room_view_adapter.ViewHolder> {

    List<Chat_room> room_list;
    Context mCtx;

    public Chat_room_view_adapter(List<Chat_room> room_list, Context mCtx) {
        this.room_list = room_list;
        this.mCtx = mCtx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroom, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Chat_room_view_adapter.ViewHolder holder, int position) {
        final Chat_room c_list = room_list.get(position);

        holder.chat_subject.setText(c_list.getChat_room_subject()); // 채팅방 제목
        holder.chat_users.setText(c_list.getChat_room_cur_users()+"/"+c_list.getChat_room_max_users()); // 채팅방 현재인원/최대인원
        holder.chat_no.setText(c_list.getChat_room_no()+"번방");
        if(c_list.getChat_room_Lock().equals("공개")){
            holder.chat_lock.setVisibility(View.INVISIBLE);
        }
        if(SharedPrefManager.getInstance(mCtx).isMyroom(c_list.getChat_room_no())){
            holder.chat_layout.setBackgroundResource(R.color.colorLBrown);
        }
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(!isLongClick){
                    String room_number = c_list.getChat_room_no();
                    String room_name = c_list.getChat_room_subject();
                    mCtx = view.getContext();
                    Intent intent = new Intent("SOCKET_SERVICE");
                    intent.putExtra("mode","enter_room");
                    intent.putExtra("room_number",room_number);
                    intent.putExtra("room_name", room_name);
                    mCtx.sendBroadcast(intent);
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return room_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ItemClickListener itemClickListener;
        public TextView chat_subject, chat_users, chat_no;
        public ImageView chat_lock;
        public LinearLayout chat_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            chat_subject = itemView.findViewById(R.id.list_item_chatroom_subject);
            chat_users = itemView.findViewById(R.id.list_item_chatroom_users);
            chat_lock = itemView.findViewById(R.id.list_item_chatroom_lock);
            chat_no = itemView.findViewById(R.id.list_item_chatroom_no);
            chat_layout = itemView.findViewById(R.id.list_chat_room_layout);
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