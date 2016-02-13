package com.kanazawaevent.view.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by nagai on 2016/01/10.
 */
public class DrawableUtil {
    private DrawableUtil() {

    }

    /**
     * Drawableの取得
     *
     * @param context コンテキスト
     * @param id      id
     * @return Drawable
     */
    public static Drawable get(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(id, context.getTheme());
        } else {
            return context.getResources().getDrawable(id);
        }
    }
}
