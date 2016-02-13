package com.kanazawaevent.view.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by nagai on 2016/02/11.
 */
public class DisplayUtil {
    private static final int TABLET_SIZE = 6;

    public static boolean isTablet(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        double inchX = metrics.widthPixels / metrics.xdpi;
        double inchY = metrics.heightPixels / metrics.ydpi;
        double inch = Math.sqrt((inchX * inchX) + (inchY * inchY));

        return inch > TABLET_SIZE;
    }
}
