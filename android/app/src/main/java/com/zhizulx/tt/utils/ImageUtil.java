package com.zhizulx.tt.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.zhizulx.tt.ui.activity.HomePageActivity;
import com.zhizulx.tt.ui.helper.PhotoHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Description 图片处理
 * @author Nana
 * @date 2014-8-4
 *
 */
public class ImageUtil {
    private static Logger logger = Logger.getLogger(ImageUtil.class);

	public static Bitmap getBigBitmapForDisplay(String imagePath,
			Context context) {
		if (null == imagePath || !new File(imagePath).exists())
			return null;
		try {
			int degeree = PhotoHelper.readPictureDegree(imagePath);
			Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
			if (bitmap == null)
				return null;
			DisplayMetrics dm = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
			float scale = bitmap.getWidth() / (float) dm.widthPixels;
			Bitmap newBitMap = null;
			if (scale > 1) {
				newBitMap = zoomBitmap(bitmap, (int) (bitmap.getWidth() / scale), (int) (bitmap.getHeight() / scale));
				bitmap.recycle();
				Bitmap resultBitmap = PhotoHelper.rotaingImageView(degeree, newBitMap);
				return resultBitmap;
			}
			Bitmap resultBitmap = PhotoHelper.rotaingImageView(degeree, bitmap);
			return resultBitmap;
		} catch (Exception e) {
			logger.e(e.getMessage());
			return null;
		}
	}

	private static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		if (null == bitmap) {
			return null;
		}
		try {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidth = ((float) width / w);
			float scaleHeight = ((float) height / h);
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
			return newbmp;
		} catch (Exception e) {
			logger.e(e.getMessage());
			return null;
		}
	}

	public static void GlideRoundAvatar(final Context ctx, String url, final ImageView avatar) {
		Glide.with(ctx).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(new BitmapImageViewTarget(avatar) {
			@Override
			protected void setResource(Bitmap resource) {
				RoundedBitmapDrawable circularBitmapDrawable =
						RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
				circularBitmapDrawable.setCircular(true);
				avatar.setImageDrawable(circularBitmapDrawable);
			}
		});
	}

	public static void GlideRoundRectangleAvatar(final Context ctx, String url, final ImageView avatar) {
		Glide.with(ctx).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(new BitmapImageViewTarget(avatar) {
			@Override
			protected void setResource(Bitmap resource) {
				RoundedBitmapDrawable circularBitmapDrawable =
						RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
				circularBitmapDrawable.setCornerRadius(15);
				avatar.setImageDrawable(circularBitmapDrawable);
			}
		});
	}

    public static void GlideAvatar(final Context ctx, String url, final ImageView avatar) {
        //Glide.with(ctx).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(new BitmapImageViewTarget(avatar));
		Glide.with(ctx).load(url).crossFade(500).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(avatar);
    }

	public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
		InputStream input = ac.getContentResolver().openInputStream(uri);
		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;//optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		int originalWidth = onlyBoundsOptions.outWidth;
		int originalHeight = onlyBoundsOptions.outHeight;
		if ((originalWidth == -1) || (originalHeight == -1))
			return null;
		//图片分辨率以480x800为标准
		float hh = 800f;//这里设置高度为800f
		float ww = 480f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (originalWidth / ww);
		} else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (originalHeight / hh);
		}
		if (be <= 0)
			be = 1;
		//比例压缩
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = be;//设置缩放比例
		bitmapOptions.inDither = true;//optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
		input = ac.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return compressImage(bitmap);//再进行质量压缩
	}

	/**
	 * 质量压缩方法
	 *
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();//重置baos即清空baos
			//第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}
}
