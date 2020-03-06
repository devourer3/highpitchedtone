package com.mymusic.orvai.high_pitched_tone.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mymusic.orvai.high_pitched_tone.EventBus.Event_bus_musicplay;
import com.mymusic.orvai.high_pitched_tone.Interface.ItemClickListener;
import com.mymusic.orvai.high_pitched_tone.R;
import com.mymusic.orvai.high_pitched_tone.models.Song_list;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by orvai on 2018-03-26.
 */

public class Perfect_singer_adapter extends RecyclerView.Adapter<Perfect_singer_adapter.ViewHolder> {

    private List<Song_list> song_lists;
    private Context mCtx;
    private int selectedPosition = -1;

    public Perfect_singer_adapter(List<Song_list> song_lists, Context mCtx) {
        this.song_lists = song_lists;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Song_list songs = song_lists.get(position);
        if(selectedPosition == position) {
            holder.song_layout.setSelected(true);
        } else {
            holder.song_layout.setSelected(false);
        }
        holder.song_circle_image.setImageResource(songs.getSong_image());
        holder.song_circle_image.setBorderWidth(1);
        holder.song_singer.setText(songs.getSong_singer());
        holder.song_title.setText(songs.getSong_title());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                mCtx = view.getContext();
                if(!isLongClick){
                    selectedPosition = position;
                    Event_bus_musicplay event_bus_musicplay = new Event_bus_musicplay(songs.getSong_title(), songs.song_data);
                    EventBus.getDefault().post(event_bus_musicplay);
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return song_lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ItemClickListener itemClickListener;
        public TextView song_title;
        public TextView song_singer;
        CircleImageView song_circle_image;
        LinearLayout song_layout;


        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            song_circle_image = itemView.findViewById(R.id.song_image);
            song_title = itemView.findViewById(R.id.song_title);
            song_singer = itemView.findViewById(R.id.song_singer);
            song_layout = itemView.findViewById(R.id.songlist_item_layout);
        }

        void setItemClickListener(ItemClickListener itemClickListener) {
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
