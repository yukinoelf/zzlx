package com.zhizulx.tt.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * Created by yuki on 2016/6/17.
 */
public class ImageEffect {
    //虚化
    public static BitmapDrawable avatarBlur(Context ctx, BitmapDrawable avatar, float radius)
    {
        Bitmap avatarBitMap = avatar.getBitmap();
        Bitmap bitmap = avatarBitMap.copy(avatarBitMap.getConfig(), true);
        if (Build.VERSION.SDK_INT > 16) {
            final RenderScript rs = RenderScript.create(ctx);
            final Allocation input = Allocation.createFromBitmap(rs, avatarBitMap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            Bitmap bitmapCut = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth()- Math.round(radius), bitmap.getHeight());
            BitmapDrawable avatarBlur = new BitmapDrawable(bitmapCut);
            return avatarBlur;
        }
        return avatar;
    }

    //切圆
    public static BitmapDrawable makeRoundCorner(BitmapDrawable avatar)
    {
        Bitmap avatarBitMap = avatar.getBitmap();
        Bitmap bitmap = avatarBitMap.copy(avatarBitMap.getConfig(), true);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height/2;
        if (width > height) {
            left = (width - height)/2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width)/2;
            right = width;
            bottom = top + width;
            roundPx = width/2;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        BitmapDrawable avatar_round = new BitmapDrawable(output);
        return avatar_round;
    }

    public static Bitmap makeRoundCorner(Bitmap bitmap)
    {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height/2;
        if (width > height) {
            left = (width - height)/2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width)/2;
            right = width;
            bottom = top + width;
            roundPx = width/2;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap avatarBlur(Context ctx, Bitmap avatarBitMap, float radius)
    {
        Bitmap bitmap = avatarBitMap.copy(avatarBitMap.getConfig(), true);
        if (Build.VERSION.SDK_INT > 16) {
            final RenderScript rs = RenderScript.create(ctx);
            final Allocation input = Allocation.createFromBitmap(rs, avatarBitMap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            Bitmap bitmapCut = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth()- Math.round(radius), bitmap.getHeight());
            return bitmapCut;
        }
        return bitmap;
    }
}
