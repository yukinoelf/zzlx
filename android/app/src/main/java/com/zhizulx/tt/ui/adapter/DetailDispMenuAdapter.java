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
public class DetailDispMenuAdapter extends RecyclerView.Adapter {
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private Context ctx;
    private List<Integer> list;


    public DetailDispMenuAdapter(Context ctx, List<Integer> list) {
        this.ctx = ctx;
        this.list = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_detail_disp_menu, null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new SelectCityResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        SelectCityResultViewHolder holder = (SelectCityResultViewHolder) viewHolder;
        holder.menu.setText("第"+list.get(i)+"天");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SelectCityResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView menu;

        public SelectCityResultViewHolder(View itemView) {
            super(itemView);
            menu = (TextView) itemView.findViewById(R.id.tv_detail_disp_menu);
            menu.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(this.getPosition());
            }
        }
    }

}
