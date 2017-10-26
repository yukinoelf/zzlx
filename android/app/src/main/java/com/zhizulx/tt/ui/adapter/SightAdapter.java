package com.zhizulx.tt.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhizulx.tt.DB.entity.SightEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.utils.ImageUtil;

import java.util.List;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/17/15.
 */
public class SightAdapter extends RecyclerView.Adapter {
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        void onSelectClick(int position, View v);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<SightEntity> mList;
    private Context ctx;

    public SightAdapter(Context ctx, List<SightEntity> mList) {
        this.ctx = ctx;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_sight, null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new SightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        SightEntity sightEntity = mList.get(i);
        SightViewHolder holder = (SightViewHolder) viewHolder;
        ImageUtil.GlideAvatar(ctx, sightEntity.getPic(), holder.pic);
        holder.name.setText(sightEntity.getName());
        holder.star.setRating((float)(sightEntity.getStar()));
        holder.tag.setText(sightEntity.getTag());
        if (sightEntity.getPrice() == 0) {
            holder.free.setBackgroundResource(R.drawable.no_ticket);
        } else {
            holder.free.setBackgroundResource(R.drawable.ticket);
        }
        holder.sightPlayTime.setText("建议游玩"+sightEntity.getPlayTime()+"小时");
        if (sightEntity.getMustGo() == 1) {
            holder.mustGo.setVisibility(View.VISIBLE);
        } else {
            holder.mustGo.setVisibility(View.GONE);
        }
        if (sightEntity.getSelect() == 1) {
            holder.sightSelect.setBackgroundResource(R.drawable.sight_selected);
        } else {
            holder.sightSelect.setBackgroundResource(R.drawable.sight_not_selected);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class SightViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout lySight;
        public ImageView pic;
        public TextView name;
        public RatingBar star;
        public TextView tag;
        public ImageView free;
        public ImageView mustGo;
        public ImageView sightSelect;
        public TextView sightPlayTime;

        public SightViewHolder(View itemView) {
            super(itemView);
            lySight = (LinearLayout) itemView.findViewById(R.id.ly_sight);
            pic = (ImageView) itemView.findViewById(R.id.sight_pic);
            name = (TextView) itemView.findViewById(R.id.sight_name);
            star = (RatingBar) itemView.findViewById(R.id.sight_star);
            tag = (TextView) itemView.findViewById(R.id.sight_tag);
            free = (ImageView) itemView.findViewById(R.id.sight_free);
            mustGo = (ImageView) itemView.findViewById(R.id.must_go);
            sightSelect = (ImageView) itemView.findViewById(R.id.sight_select);
            sightPlayTime = (TextView) itemView.findViewById(R.id.sight_play_time);
            lySight.setOnClickListener(this);
            sightSelect.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                if (v.getId() == R.id.sight_select) {
                    onRecyclerViewListener.onSelectClick(this.getPosition(), v);
                } else {
                    onRecyclerViewListener.onItemClick(this.getPosition());
                }
            }
        }
    }

}
