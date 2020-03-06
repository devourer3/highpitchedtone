package com.mymusic.orvai.high_pitched_tone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.mymusic.orvai.high_pitched_tone.Interface.ItemClickListener;
import com.mymusic.orvai.high_pitched_tone.LectureActivity;
import com.mymusic.orvai.high_pitched_tone.R;
import com.mymusic.orvai.high_pitched_tone.models.Course;
import com.mymusic.orvai.high_pitched_tone.models.Vocal_trainer;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

/**
 * Created by orvai on 2018-01-25.
 */

public class VocalTrainerAdapter extends ExpandableRecyclerViewAdapter<VocalTrainerViewHolder, CourseViewHolder> {

    Context mCtx;

    public VocalTrainerAdapter(List<? extends ExpandableGroup> groups) {
        // List<? extends ExpandableGroup -> 이거는 지네릭스의 와일드 카드 중 상한 제한이다. 즉, ExpandableGroup과 그 자손들만 리스트에 넣을 수 있다는 말이다.
        // 다시말하면, 보컬트레이너 리스트와 와 그 밑의 강의목록 리스트까지 들어갈 수 있다는 것이다.
        super(groups);
    }

    @Override
    public VocalTrainerViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vocaltrainer, parent, false);
        return new VocalTrainerViewHolder(view);
    }

    @Override
    public CourseViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(VocalTrainerViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setVocalTrainer(group);
    }

    @Override
    public void onBindChildViewHolder(CourseViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Course course = (Course) group.getItems().get(childIndex);
        holder.setArtistName(course.getName());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                mCtx = view.getContext();
                if(!isLongClick){
                    Intent intent = new Intent(mCtx, LectureActivity.class);
                    intent.putExtra("youtube_url", course.getUrl(position));
                    mCtx.startActivity(intent);
                } else {
                }
            }
        });
    }
}

class CourseViewHolder extends ChildViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private TextView artistName;
    private Context mCtx;
    private ItemClickListener itemClickListener;

    public CourseViewHolder(final View itemView) {
        super(itemView);
        mCtx = itemView.getContext();
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        artistName = (TextView) itemView.findViewById(R.id.list_item_course_name);
    }

    public void setArtistName(String name) {
        artistName.setText(name);
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

class VocalTrainerViewHolder extends GroupViewHolder {

    private TextView VocalTrainer_name;
    private ImageView arrow;
    private ImageView image;

    public VocalTrainerViewHolder(View itemView) {
        super(itemView);
        VocalTrainer_name = (TextView) itemView.findViewById(R.id.list_item_vocaltrainer_name);
        arrow = (ImageView) itemView.findViewById(R.id.list_item_vocaltrainer_arrow);
        image = (ImageView) itemView.findViewById(R.id.list_item_vocaltrainer_icon);
    }

    public void setVocalTrainer(ExpandableGroup vocaltrainer){
        VocalTrainer_name.setText(vocaltrainer.getTitle());
        image.setImageResource(((Vocal_trainer)vocaltrainer).getImage());
    }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}