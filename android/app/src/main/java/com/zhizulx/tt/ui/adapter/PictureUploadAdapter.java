package com.zhizulx.tt.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhizulx.tt.R;
import com.zhizulx.tt.utils.ImageUtil;

import java.util.List;

//import com.bumptech.glide.Glide;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/17/15.
 */
public class PictureUploadAdapter extends RecyclerView.Adapter {
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        void onAddClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<Uri> mList;
    private Context ctx;

    public PictureUploadAdapter(Context ctx, List<Uri> mList) {
        this.ctx = ctx;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.travel_item_picture_upload, null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
        return new PictureUploadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        Uri uri = mList.get(i);
        PictureUploadViewHolder holder = (PictureUploadViewHolder) viewHolder;
        if (uri.equals(Uri.parse("no pic"))) {
            Glide.with(ctx).load(R.drawable.add_image).into(holder.pic);
            holder.del.setVisibility(View.GONE);
            holder.pic.setClickable(true);
        } else {
            Glide.with(ctx).load(uri).into(holder.pic);
            holder.del.setVisibility(View.VISIBLE);
            holder.pic.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class PictureUploadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView pic;
        public ImageView del;

        public PictureUploadViewHolder(View itemView) {
            super(itemView);
            pic = (ImageView) itemView.findViewById(R.id.item_picture_upload_bk);
            del = (ImageView) itemView.findViewById(R.id.item_picture_upload_delete);
            del.setOnClickListener(this);
            pic.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.item_picture_upload_bk) {
                onRecyclerViewListener.onAddClick(this.getPosition());
            } else {
                onRecyclerViewListener.onItemClick(this.getPosition());
            }
        }
    }

}
