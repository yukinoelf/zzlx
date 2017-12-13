package com.zhizulx.tt.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhizulx.tt.DB.entity.DateEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.config.DateType;

import java.text.SimpleDateFormat;
import java.util.List;

//import com.bumptech.glide.Glide;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/17/15.
 */
public class DayAdapter extends RecyclerView.Adapter {
    public static interface OnRecyclerViewListener {
        void onSelectClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<DateEntity> mList;
    private Context ctx;

    public DayAdapter(Context ctx, List<DateEntity> mList) {
        this.ctx = ctx;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_date_day, null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        DateEntity dateEntity = mList.get(i);
        DayViewHolder holder = (DayViewHolder) viewHolder;
        holder.day.setTextColor(ctx.getResources().getColor(R.color.not_clicked));
        holder.day.setBackground(null);
        switch (dateEntity.getType()) {
            case DateType.today:
                holder.day.setBackground(ctx.getResources().getDrawable(R.drawable.select_date_today));
                break;
            case DateType.start:
                holder.day.setBackground(ctx.getResources().getDrawable(R.drawable.select_date_start));
                holder.day.setTextColor(ctx.getResources().getColor(R.color.clicked));
                break;
            case DateType.between:
                holder.day.setBackground(ctx.getResources().getDrawable(R.drawable.select_date_between));
                break;
            case DateType.end:
                holder.day.setBackground(ctx.getResources().getDrawable(R.drawable.select_date_end));
                holder.day.setTextColor(ctx.getResources().getColor(R.color.clicked));
                break;
            case DateType.select:
                holder.day.setBackground(ctx.getResources().getDrawable(R.drawable.select_date_select));
                holder.day.setTextColor(ctx.getResources().getColor(R.color.clicked));
                break;
            case DateType.cannot_select:
                holder.day.setTextColor(ctx.getResources().getColor(R.color.not_select_color));
                holder.day.setClickable(false);
                break;
            case DateType.blank:
                holder.day.setClickable(false);
                holder.day.setText("");
                break;
            case DateType.normal:
                break;
        }

        if (dateEntity.getType() != DateType.blank) {
            holder.day.setText(sdf.format(dateEntity.getDate()));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class DayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView day;

        public DayViewHolder(View itemView) {
            super(itemView);
            day = (TextView) itemView.findViewById(R.id.date_day);
            day.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onSelectClick(this.getPosition());
            }
        }
    }

}
