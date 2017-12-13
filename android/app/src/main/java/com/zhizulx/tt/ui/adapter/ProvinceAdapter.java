package com.zhizulx.tt.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhizulx.tt.R;

import java.util.List;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/17/15.
 */
public class ProvinceAdapter extends RecyclerView.Adapter {
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private Context ctx;
    private List<String> list;
    private int pos = 0;

    public ProvinceAdapter(Context ctx, List<String> list) {
        this.ctx = ctx;
        this.list = list;
    }

    public void setProvincePos(int pos) {
        if (pos < getItemCount()) {
            this.pos = pos;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_province, null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new SelectCityResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        SelectCityResultViewHolder holder = (SelectCityResultViewHolder) viewHolder;
        holder.province.setText(list.get(i));
        if (i == pos) {
            holder.province.setTextColor(ctx.getResources().getColor(R.color.clicked));
            holder.province.setBackgroundResource(R.drawable.city_bk_blue);
        } else {
            holder.province.setTextColor(ctx.getResources().getColor(R.color.not_clicked));
            holder.province.setBackgroundResource(R.drawable.city_bk_white);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SelectCityResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView province;

        public SelectCityResultViewHolder(View itemView) {
            super(itemView);
            province = (TextView) itemView.findViewById(R.id.tv_province);
            province.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(this.getPosition());
            }
        }
    }

}
