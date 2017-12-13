package com.zhizulx.tt.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhizulx.tt.DB.Serializable.MapRoute;
import com.zhizulx.tt.R;
import com.zhizulx.tt.config.IntentConstant;
import com.zhizulx.tt.ui.activity.CreateTravelActivity;
import com.zhizulx.tt.ui.activity.DetailDispActivity;
import com.zhizulx.tt.ui.activity.ExpenseDetailActivity;
import com.zhizulx.tt.ui.activity.FeedbackActivity;
import com.zhizulx.tt.ui.activity.HomePageActivity;
import com.zhizulx.tt.ui.activity.IntroduceSightActivity;
import com.zhizulx.tt.ui.activity.MessageActivity;
import com.zhizulx.tt.ui.activity.SelectDesignWayActivity;
import com.zhizulx.tt.ui.activity.SelectTravelRouteActivity;
import com.zhizulx.tt.ui.activity.HotelWebViewActivity;
import com.zhizulx.tt.ui.activity.ShowSightsInMap;
import com.zhizulx.tt.ui.activity.TrafficWebViewActivity;
import com.zhizulx.tt.ui.fragment.ContactFragment;
import com.zhizulx.tt.ui.route.RouteActivity;

public class TravelUIHelper {

	// 对话框回调函数
	public interface dialogCallback{
		public void callback();
	}

	public static void showAlertDialog(Context context, String content, final dialogCallback callback) {
		final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialog_view = inflater.inflate(R.layout.travel_alert_dialog, null);
		TextView tvContent = (TextView)dialog_view.findViewById(R.id.alert_dialog_content);
		tvContent.setText(content);
		dialog_view.findViewById(R.id.bn_alert_dialog_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog_view.findViewById(R.id.bn_alert_dialog_confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callback.callback();
				dialog.dismiss();
			}
		});

		dialog.setContentView(dialog_view);
		dialog.show();
	}

	public static void showSuccessDialog(Context context, String content, final dialogCallback callback) {
		final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialog_view = inflater.inflate(R.layout.travel_success_dialog, null);
		TextView tvContent = (TextView)dialog_view.findViewById(R.id.success_dialog_content);
		tvContent.setText(content);

		dialog_view.findViewById(R.id.bn_success_dialog_confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callback.callback();
				dialog.dismiss();
			}
		});

		dialog.setContentView(dialog_view);
		dialog.show();
	}

    public static Dialog showCalculateDialog(Context context) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialog_view = inflater.inflate(R.layout.travel_calculate_dialog, null);
        ImageView calculate = (ImageView)dialog_view.findViewById(R.id.calculate_gif);
        Glide.with(context).load(R.drawable.calculating).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(calculate);
        dialog.setContentView(dialog_view);
        dialog.show();
        dialog.setCancelable(false);
        return dialog;
    }

	public static Dialog showLoadingDialog(Context context) {
		final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialog_view = inflater.inflate(R.layout.travel_calculate_dialog, null);
		ImageView calculate = (ImageView)dialog_view.findViewById(R.id.calculate_gif);
		Glide.with(context).load(R.drawable.loading).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(calculate);
		dialog.setContentView(dialog_view);
		dialog.show();
		dialog.setCancelable(false);
		return dialog;
	}

    //跳转到新建行程页面
    public static void openCreateTravelActivity(Context ctx) {
        Intent intent = new Intent(ctx, CreateTravelActivity.class);
        ctx.startActivity(intent);
    }

	//跳转到景点介绍页面
	public static void openIntroduceSightActivity(Context ctx, int sightID) {
        Intent intent = new Intent(ctx, IntroduceSightActivity.class);
        intent.putExtra(IntentConstant.KEY_PEERID, sightID);
        ctx.startActivity(intent);
	}

	//跳转到酒店介绍页面
	public static void openIntroduceHotelActivity(Context ctx, String name, String url) {
/*		Intent intent = new Intent(ctx, IntroduceHotelActivity.class);
		intent.putExtra(IntentConstant.KEY_PEERID, hotelID);
		ctx.startActivity(intent);*/
		Intent intent = new Intent(ctx, HotelWebViewActivity.class);
        intent.putExtra(IntentConstant.NAME, name);
        intent.putExtra(IntentConstant.WEBVIEW_URL, url);
		ctx.startActivity(intent);
	}

	//跳转到交通方式页面
	public static void openTrafficListActivity(Context ctx, String name, String url) {
        Log.e("openTrafficListActivity", url);
        Intent intent = new Intent(ctx, TrafficWebViewActivity.class);
		intent.putExtra(IntentConstant.NAME, name);
		intent.putExtra(IntentConstant.WEBVIEW_URL, url);
		ctx.startActivity(intent);
	}

	//跳转到细节展示页面
	public static void openDetailDispActivity(Context ctx) {
		Intent intent = new Intent(ctx, DetailDispActivity.class);
		ctx.startActivity(intent);
	}

	//跳转到游玩喜好页面
	public static void openExpenseDetailActivity(Context ctx) {
		Intent intent = new Intent(ctx, ExpenseDetailActivity.class);
		ctx.startActivity(intent);
	}

	//跳转到设计方式选择页面
	public static void openSelectDesignWayActivity(Context ctx, int emotion) {
		Intent intent = new Intent(ctx, SelectDesignWayActivity.class);
		intent.putExtra("emotion", emotion);
		ctx.startActivity(intent);
	}

	//跳转到路线选择细节页面
	public static void openSelectTravelRouteActivity(Context ctx) {
		Intent intent = new Intent(ctx, SelectTravelRouteActivity.class);
		ctx.startActivity(intent);
	}

	//跳转到景点选择细节页面
/*	public static void openSelectSightActivity(Context ctx) {
		Intent intent = new Intent(ctx, SelectSightActivity.class);
		ctx.startActivity(intent);
	}*/

	//跳转到路径规划页面
	public static void openMapRouteActivity(Context ctx, MapRoute mapRoute) {
		Intent intent = new Intent(ctx, RouteActivity.class);
        intent.putExtra("map_point", mapRoute);
        ctx.startActivity(intent);
	}

	//跳转到细节展示页面
	public static void openHomePageActivity(Context ctx) {
		Intent intent = new Intent(ctx, HomePageActivity.class);
		ctx.startActivity(intent);
	}

	//跳转到一天地点展示页
	public static void openShowSightsInMapActivity(Context ctx, int day) {
		Intent intent = new Intent(ctx, ShowSightsInMap.class);
		intent.putExtra("day", day);
		ctx.startActivity(intent);
	}
}
