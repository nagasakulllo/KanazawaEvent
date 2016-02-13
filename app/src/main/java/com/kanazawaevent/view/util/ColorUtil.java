package com.kanazawaevent.view.util;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.kanazawaevent.R;

/**
 * Created by nagai on 2016/01/10.
 */
public class ColorUtil {
    private ColorUtil() {

    }

    /**
     * 背景カラーの取得
     *
     * @param context コンテキスト
     * @param color   カラー
     * @return カラー
     */
    public static int getBack(Context context, Color color) {
        return get(context, color.getBack());
    }

    /**
     * テキストカラーの取得
     *
     * @param context コンテキスト
     * @param color   カラー
     * @return カラー
     */
    public static int getText(Context context, Color color) {
        return get(context, color.getText());
    }

    private static int get(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public enum Color {
        RED(R.color.bg_red, R.color.text_red),
        PURPLE(R.color.bg_purple, R.color.text_purple),
        BLUE(R.color.bg_blue, R.color.text_blue),
        CYAN(R.color.bg_cyan, R.color.text_cyan),
        GREEN(R.color.bg_green, R.color.text_green),
        LIME(R.color.bg_lime, R.color.text_lime),
        YELLOW(R.color.bg_yellow, R.color.text_yellow),
        ORANGE(R.color.bg_orane, R.color.text_orane),
        BROWN(R.color.bg_brown, R.color.text_brown),;

        private int mBack;
        private int mText;

        Color(int back, int text) {
            mBack = back;
            mText = text;
        }

        public int getBack() {
            return mBack;
        }

        public int getText() {
            return mText;
        }
    }

}
