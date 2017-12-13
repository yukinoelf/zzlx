package com.zhizulx.tt.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhizulx.tt.DB.entity.CollectRouteEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.manager.IMTravelManager;
import com.zhizulx.tt.ui.widget.swiprecycleview.ItemHelpter;
import com.zhizulx.tt.ui.widget.swiprecycleview.SwipeLayout;
import com.zhizulx.tt.utils.ImageUtil;


import java.util.List;

//import com.bumptech.glide.Glide;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/17/15.
 */
public class CollectionAdapter extends RecyclerView.Adapter implements ItemHelpter.Callback{
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        /*void onTopClick(int position);*/
        void onDelClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<CollectRouteEntity> mList;
    private Context ctx;
    private RecyclerView mRecycler;
    private LayoutInflater mInflater;
    private IMTravelManager travelManager;

    public CollectionAdapter(Context ctx, IMTravelManager travelManager, List<CollectRouteEntity> mList) {
        this.ctx = ctx;
        this.travelManager = travelManager;
        this.mInflater = LayoutInflater.from(ctx);
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = mInflater.inflate(R.layout.travel_item_collection, null);

/*        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);*/
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        CollectRouteEntity collectRouteEntity = mList.get(i);
        CollectionViewHolder holder = (CollectionViewHolder) viewHolder;
        if(holder.root.isOpen()){
            holder.root.clearAnimation();
        }
        holder.time.setText(collectRouteEntity.getStartDate());
        String citycode = collectRouteEntity.getRouteEntity().getCityCode();
        String cityname = travelManager.getCityNameByCode(citycode);
        ImageUtil.GlideRoundAvatar(ctx, travelManager.getCityEntitybyCityCode(citycode).getIcon(), holder.avatar);
        holder.destination.setText(cityname);
        holder.routeType.setText(collectRouteEntity.getRouteEntity().getRouteType() + "路线");
    }

    @Override
    public SwipeLayout getSwipLayout(float x, float y) {
        return (SwipeLayout)mRecycler.findChildViewUnder(x,y);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecycler = recyclerView;
        recyclerView.addOnItemTouchListener(new ItemHelpter(ctx,this));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List<CollectRouteEntity> getDataList() {
        return mList;
    }

    class CollectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public SwipeLayout root;
        public ImageView avatar;
        public TextView time;
        public TextView destination;
        public TextView routeType;
        public ImageView item;
        //public ImageView top;
        public ImageView del;

        public CollectionViewHolder(View itemView) {
            super(itemView);
            root = (SwipeLayout)itemView;
            item = (ImageView) itemView.findViewById(R.id.collection_item);
            avatar = (ImageView) itemView.findViewById(R.id.collection_avatar);
            time = (TextView) itemView.findViewById(R.id.collection_time);
            destination = (TextView) itemView.findViewById(R.id.collection_destination);
            routeType = (TextView) itemView.findViewById(R.id.collection_route_type);
            //top = (ImageView) itemView.findViewById(R.id.collection_top);
            del = (ImageView) itemView.findViewById(R.id.collection_del);
            //top.setOnClickListener(this);
            del.setOnClickListener(this);
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                switch (v.getId()) {
                    case R.id.collection_item:
                        onRecyclerViewListener.onItemClick(this.getPosition());
                        break;
/*                    case R.id.collection_top:
                        onRecyclerViewListener.onTopClick(this.getPosition());
                        break;*/
                    case R.id.collection_del:
                        onRecyclerViewListener.onDelClick(this.getPosition());
                        break;
                }
            }
        }
    }

}
