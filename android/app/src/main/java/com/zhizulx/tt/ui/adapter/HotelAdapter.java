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

//import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.zhizulx.tt.DB.entity.HotelEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.utils.ImageUtil;

import java.util.List;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/17/15.
 */
public class HotelAdapter extends RecyclerView.Adapter {
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        void onSelectClick(int position, View v);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<HotelEntity> mList;
    private Context ctx;

    public HotelAdapter(Context ctx, List<HotelEntity> mList) {
        this.ctx = ctx;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_hotel, null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        HotelEntity hotelEntity = mList.get(i);
        HotelViewHolder holder = (HotelViewHolder) viewHolder;
        ImageUtil.GlideAvatar(ctx, hotelEntity.getPic(), holder.pic);
        holder.name.setText(hotelEntity.getName());
        holder.star.setRating((float)(hotelEntity.getStar()));
        holder.tag.setText(hotelEntity.getTag());
        holder.price.setText("￥"+hotelEntity.getPrice()+"起(晚/间)");

        if (mList.get(i).getSelect() == 1) {
            holder.select.setBackgroundResource(R.drawable.sight_selected);
        } else {
            holder.select.setBackgroundResource(R.drawable.sight_not_selected);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class HotelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout lyHotel;
        public ImageView pic;
        public TextView name;
        public RatingBar star;
        public TextView tag;
        public TextView price;
        public ImageView select;

        public HotelViewHolder(View itemView) {
            super(itemView);
            lyHotel = (LinearLayout) itemView.findViewById(R.id.ly_hotel);
            pic = (ImageView) itemView.findViewById(R.id.hotel_pic);
            name = (TextView) itemView.findViewById(R.id.hotel_name);
            star = (RatingBar) itemView.findViewById(R.id.hotel_star);
            tag = (TextView) itemView.findViewById(R.id.hotel_tag);
            price = (TextView) itemView.findViewById(R.id.hotel_price);
            select = (ImageView) itemView.findViewById(R.id.hotel_select);
            lyHotel.setOnClickListener(this);
            select.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                if (v.getId() == R.id.hotel_select) {
                    onRecyclerViewListener.onSelectClick(this.getPosition(), v);
                } else {
                    onRecyclerViewListener.onItemClick(this.getPosition());
                }
            }
        }
    }

}
