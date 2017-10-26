package com.zhizulx.tt.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhizulx.tt.DB.entity.DetailDispEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.utils.ImageUtil;
import com.zhizulx.tt.utils.TravelUIHelper;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

//import com.bumptech.glide.Glide;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/17/15.
 */
public class DetailDispAdapter extends RecyclerView.Adapter {
    public static interface OnRecyclerViewListener {
        void onDayClick(View v, int position);
        void onSightClick(View v, int position);
        void onHotelClick(View v, int position);
        void onTrafficClick(View v, int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<DetailDispEntity> mList;
    private Context ctx;
    private static final int NULL = 0;
    private static final int DAY = 1;
    private static final int SIGHT = 2;
    private static final int HOTEL = 3;
    private static final int TRAFFIC = 4;
    private int trafficEnd = 0;
    private static final int TRAVEL_START = 0;
    private static final int TRAVEL_END = 1;

    public DetailDispAdapter(Context ctx, List<DetailDispEntity> mList) {
        this.ctx = ctx;
        this.mList = mList;
    }

    private void trafficStatus() {
        int i = 0;
        for (DetailDispEntity detailDispEntity : mList) {
            if (detailDispEntity.getType() == TRAFFIC) {
               trafficEnd = i;
            }
            i ++;
        }

        i = 0;
        for (DetailDispEntity detailDispEntity : mList) {
            if (detailDispEntity.getType() == TRAFFIC) {
                if (trafficEnd == i) {
                    detailDispEntity.setStatus(TRAVEL_END);
                } else {
                    detailDispEntity.setStatus(TRAVEL_START);
                }
            }
            i ++;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (type){
            case NULL:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_detail_disp_null, null);
                //        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
                LinearLayout.LayoutParams lpn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lpn);
                holder = new NullViewHolder(view);
                break;

            case DAY:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_detail_disp_day, null);
                //        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
                LinearLayout.LayoutParams lpd = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lpd);
                holder = new DayViewHolder(view);
                break;

            case SIGHT:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_detail_disp_sight, null);
                //        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
                LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lps);
                holder = new SightViewHolder(view);
                break;

            case HOTEL:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_detail_disp_hotel, null);
                //        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
                LinearLayout.LayoutParams lph = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lph);
                holder = new HotelViewHolder(view);
                break;

            case TRAFFIC:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_detail_disp_traffic, null);
                //        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
                LinearLayout.LayoutParams lpt = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lpt);
                holder = new TrafficViewHolder(view);
                break;

        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        DetailDispEntity detailDispEntity = mList.get(i);

        switch (getItemViewType(i)){
            case NULL:
                NullViewHolder nullViewHolder = (NullViewHolder) viewHolder;
                break;
            case DAY:
                DayViewHolder dayViewHolder = (DayViewHolder) viewHolder;
                String str = String.format("<small>第<font color='#FF0000'>%s</font>天的行程</small>", detailDispEntity.getTitle());
                dayViewHolder.day.setText(Html.fromHtml(str));
                break;
            case SIGHT:
                SightViewHolder sightViewHolder = (SightViewHolder) viewHolder;
                sightViewHolder.title.setText(detailDispEntity.getTitle());

                String pic = detailDispEntity.getImage();
                List<String> picList = Arrays.asList(pic.split(","));
                if (picList.size() > 0) {
                    ImageUtil.GlideRoundRectangleAvatar(ctx, picList.get(0), sightViewHolder.avatar);
                }
                //Glide.with(ctx).load(detailDispEntity.getImage()).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(sightViewHolder.avatar);
                break;
            case HOTEL:
                HotelViewHolder hotelViewHolder = (HotelViewHolder) viewHolder;
                hotelViewHolder.title.setText(detailDispEntity.getTitle());
                Glide.with(ctx).load(detailDispEntity.getImage()).asBitmap().
                        diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().
                        into(hotelViewHolder.avatar);
                break;
            case TRAFFIC:
                trafficStatus();
                TrafficViewHolder trafficViewHolder = (TrafficViewHolder) viewHolder;
                if (detailDispEntity.getTitle().equals("飞机")) {
                    trafficViewHolder.selectResult.setBackgroundResource(R.drawable.detail_disp_traffic_plane);
                    trafficViewHolder.plane.setTextColor(ctx.getResources().getColor(R.color.price));
                    trafficViewHolder.train.setTextColor(ctx.getResources().getColor(R.color.not_clicked));
                } else {
                    trafficViewHolder.selectResult.setBackgroundResource(R.drawable.detail_disp_traffic_train);
                    trafficViewHolder.plane.setTextColor(ctx.getResources().getColor(R.color.not_clicked));
                    trafficViewHolder.train.setTextColor(ctx.getResources().getColor(R.color.price));
                }
                trafficViewHolder.trafficTime.setText(detailDispEntity.getTime());

                if (detailDispEntity.getStatus() == TRAVEL_START) {
                    trafficViewHolder.lytrafficTime.setBackgroundResource(R.drawable.detail_disp_traffic_start);
                } else {
                    trafficViewHolder.lytrafficTime.setBackgroundResource(R.drawable.detail_disp_traffic_end);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class NullViewHolder extends RecyclerView.ViewHolder {
        public NullViewHolder(View itemView) {
            super(itemView);
        }
    }

    class DayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView day;
        public TextView showInMap;

        public DayViewHolder(View itemView) {
            super(itemView);
            day = (TextView) itemView.findViewById(R.id.detail_disp_day);
            showInMap = (TextView) itemView.findViewById(R.id.detail_disp_day_show_in_map);
            showInMap.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecyclerViewListener.onDayClick(v, this.getPosition());
        }
    }

    class SightViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public FrameLayout rlSight;
        public TextView title;
        public TextView info;
        public TextView map;
        public ImageView avatar;

        public SightViewHolder(View itemView) {
            super(itemView);
            rlSight = (FrameLayout) itemView.findViewById(R.id.travel_item_detail_disp_sight_bk);
            avatar = (ImageView)  itemView.findViewById(R.id.sight_avatar);
            title = (TextView) itemView.findViewById(R.id.detail_disp_sight_title);
            info = (TextView) itemView.findViewById(R.id.detail_disp_sight_info);
            map = (TextView) itemView.findViewById(R.id.detail_disp_sight_map);
            avatar.setOnClickListener(this);
            info.setOnClickListener(this);
            map.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecyclerViewListener.onSightClick(v, this.getPosition());
        }
    }

    class HotelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public FrameLayout rlHotel;
        public TextView title;
        public TextView info;
        public TextView map;
        public ImageView avatar;

        public HotelViewHolder(View itemView) {
            super(itemView);
            rlHotel = (FrameLayout) itemView.findViewById(R.id.travel_item_detail_disp_hotel_bk);
            avatar = (ImageView)  itemView.findViewById(R.id.hotel_avatar);
            title = (TextView) itemView.findViewById(R.id.detail_disp_hotel_title);
            info = (TextView) itemView.findViewById(R.id.detail_disp_hotel_info);
            map = (TextView) itemView.findViewById(R.id.detail_disp_hotel_map);
            avatar.setOnClickListener(this);
            info.setOnClickListener(this);
            map.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecyclerViewListener.onHotelClick(v, this.getPosition());
        }
    }

    class TrafficViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView selectResult;
        public TextView plane;
        public TextView train;
        public ImageView selectResultMask;
        public LinearLayout lytrafficTime;
        public TextView trafficTime;

        public TrafficViewHolder(View itemView) {
            super(itemView);
            selectResult = (ImageView) itemView.findViewById(R.id.detail_disp_traffic_select_result);
            selectResultMask = (ImageView) itemView.findViewById(R.id.detail_disp_traffic_select_result_mask);
            plane = (TextView) itemView.findViewById(R.id.detail_disp_traffic_plane);
            train = (TextView) itemView.findViewById(R.id.detail_disp_traffic_train);
            lytrafficTime = (LinearLayout) itemView.findViewById(R.id.detail_disp_traffic_time);
            trafficTime = (TextView) itemView.findViewById(R.id.detail_disp_traffic_time_result);

            selectResult.setOnClickListener(this);
            plane.setOnClickListener(this);
            train.setOnClickListener(this);
            lytrafficTime.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecyclerViewListener.onTrafficClick(v, this.getPosition());
        }
    }
}
