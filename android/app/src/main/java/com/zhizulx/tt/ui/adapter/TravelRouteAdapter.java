package com.zhizulx.tt.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhizulx.tt.DB.entity.RouteEntity;
import com.zhizulx.tt.DB.sp.SystemConfigSp;
import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.manager.IMTravelManager;
import com.zhizulx.tt.utils.ImageUtil;

import java.util.List;

//import com.bumptech.glide.Glide;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/17/15.
 */
public class TravelRouteAdapter extends RecyclerView.Adapter {
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<RouteEntity> mList;
    private Context ctx;
    private IMTravelManager travelManager;

    public TravelRouteAdapter(Context ctx, IMTravelManager travelManager, List<RouteEntity> mList) {
        this.ctx = ctx;
        this.travelManager = travelManager;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_travel_route, null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new TravelRouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        RouteEntity routeEntity = mList.get(i);
        TravelRouteViewHolder holder = (TravelRouteViewHolder) viewHolder;
        String avatarUrl = travelManager.getCityEntitybyCityCode(routeEntity.getCityCode()).getIcon();
        ImageUtil.GlideAvatar(ctx, avatarUrl, holder.city);
        String routeType = routeEntity.getRouteType();
        holder.title.setText(travelManager.getCityNameByCode(routeEntity.getCityCode())+routeType+"之旅");
        holder.days.setText(routeEntity.getDay() + "天");
        holder.cost.setText("约￥1000/人");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class TravelRouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout lyTravelRoute;
        public ImageView city;
        public TextView title;
        public TextView days;
        public TextView cost;

        public TravelRouteViewHolder(View itemView) {
            super(itemView);
            lyTravelRoute = (LinearLayout) itemView.findViewById(R.id.ly_travel_route);
            city = (ImageView) itemView.findViewById(R.id.travel_item_travel_route_city_bk);
            title = (TextView) itemView.findViewById(R.id.travel_route_title);
            days = (TextView) itemView.findViewById(R.id.travel_route_days);
            cost = (TextView) itemView.findViewById(R.id.travel_route_cost);
            lyTravelRoute.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(this.getPosition());
            }
        }
    }

}
