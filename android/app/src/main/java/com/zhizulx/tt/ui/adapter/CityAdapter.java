package com.zhizulx.tt.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhizulx.tt.DB.entity.CityEntity;
import com.zhizulx.tt.R;

import java.util.List;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/17/15.
 */
public class CityAdapter extends RecyclerView.Adapter {
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        void onItemBtnClick(View view, int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private Context ctx;
    private List<CityEntity> list;

    public CityAdapter(Context ctx, List<CityEntity> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_city, null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new SelectCityResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        CityEntity cityEntity = list.get(i);
        SelectCityResultViewHolder holder = (SelectCityResultViewHolder) viewHolder;
        holder.name.setText(cityEntity.getName());
        if (cityEntity.getSelect() == 1) {
            holder.lyCity.setBackgroundColor(ctx.getResources().getColor(R.color.city_selected));
            holder.opt.setBackground(ctx.getResources().getDrawable(R.drawable.select_city_delete));
        } else {
            holder.lyCity.setBackgroundColor(ctx.getResources().getColor(R.color.base_bk));
            holder.opt.setBackground(ctx.getResources().getDrawable(R.drawable.select_city_add));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SelectCityResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView image;
        public TextView name;
        public ImageView opt;
        private RelativeLayout lyCity;

        public SelectCityResultViewHolder(View itemView) {
            super(itemView);
            lyCity = (RelativeLayout) itemView.findViewById(R.id.ly_city);
            image = (ImageView) itemView.findViewById(R.id.city_image);
            name = (TextView) itemView.findViewById(R.id.city_name);
            opt = (ImageView) itemView.findViewById(R.id.city_select);
            lyCity.setOnClickListener(this);
            opt.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                if (v.getId() == R.id.city_select) {
                    onRecyclerViewListener.onItemBtnClick(v, this.getPosition());
                } else {
                    onRecyclerViewListener.onItemClick(this.getPosition());
                }
            }
        }
    }

}
