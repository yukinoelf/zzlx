package com.zhizulx.tt.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhizulx.tt.DB.entity.CityEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.utils.ImageUtil;

import java.util.List;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/17/15.
 */
public class TravelHotAdapter extends RecyclerView.Adapter {
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        void onAddClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private Context ctx;
    private List<CityEntity> mList;

    public TravelHotAdapter(Context ctx, List<CityEntity> list) {
        this.ctx = ctx;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_hot, null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new HotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        HotViewHolder holder = (HotViewHolder) viewHolder;
        CityEntity cityEntity = mList.get(i);
        ImageUtil.GlideAvatar(ctx, cityEntity.getIcon(), holder.icon);
        //holder.icon.setImageDrawable(ctx.getResources().getDrawable(R.drawable.xiamen_icon));
        holder.name.setText(cityEntity.getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class HotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView icon;
        public ImageView add;
        public TextView name;

        public HotViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.iv_item_hot_city);
            add = (ImageView) itemView.findViewById(R.id.iv_item_hot_add_city);
            name = (TextView)  itemView.findViewById(R.id.iv_item_hot_name);
            icon.setOnClickListener(this);
            add.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                switch (v.getId()) {
                    case R.id.iv_item_hot_city:
                        onRecyclerViewListener.onItemClick(this.getPosition());
                        break;
                    case R.id.iv_item_hot_add_city:
                        onRecyclerViewListener.onAddClick(this.getPosition());
                        break;
                }
            }
        }
    }
}
