/*
 * Copyright 2014 trinea.cn All right reserved. This software is the confidential and proprietary information of
 * trinea.cn ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with trinea.cn.
 */
package com.zhizulx.tt.ui.widget.adver;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhizulx.tt.R;

import java.util.List;

/**
 * @Description: 图片适配器
 * @author http://blog.csdn.net/finddreams
 */ 
public class ImagePagerAdapter extends BaseAdapter {

	private Context context;
	private List<String> picList;
	private int size;
	private boolean isInfiniteLoop;

	public ImagePagerAdapter(Context context, List<String> picList)
	{
		this.context = context;
		this.picList = picList;
		if (picList != null) {
			this.size = picList.size();
		}
		isInfiniteLoop = false;
	}

	@Override
	public int getCount() {
		// Infinite loop
		return isInfiniteLoop ? Integer.MAX_VALUE : picList.size();
	}

	/**
	 * get really position
	 * 
	 * @param position
	 * @return
	 */
	private int getPosition(int position) {
		return isInfiniteLoop ? position % size : position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup container) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = holder.imageView = new ImageView(context);
			holder.imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
			holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			view.setTag(R.id.viewflowindic, holder);
		} else {
			holder = (ViewHolder) view.getTag(R.id.viewflowindic);
		}

		Glide.with(context).load(picList.get(ImagePagerAdapter.this.getPosition(position))).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(holder.imageView);
		//holder.imageView.set(imageIdList.get(ImagePagerAdapter.this.getPosition(position)));
		return view;
	}

	private static class ViewHolder {

		ImageView imageView;
	}

	/**
	 * @return the isInfiniteLoop
	 */
	public boolean isInfiniteLoop() {
		return isInfiniteLoop;
	}

	/**
	 * @param isInfiniteLoop
	 *            the isInfiniteLoop to set
	 */
	public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
		this.isInfiniteLoop = isInfiniteLoop;
		return this;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

}
