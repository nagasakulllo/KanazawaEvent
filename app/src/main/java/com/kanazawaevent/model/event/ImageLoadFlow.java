package com.kanazawaevent.model.event;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.kanazawaevent.model.util.NetworkResult;
import com.kanazawaevent.model.util.NetworkUtil;

import java.util.ArrayList;

/**
 * イメージロードフロー
 * Created by nagai on 2016/02/07.
 */
public class ImageLoadFlow {
    private EventLocation mLocation;
    private ArrayList<String> mUrlList;

    /**
     * コンストラクタ
     *
     * @param exUrl 独自prefixをつけた拡張URL
     * @throws IllegalArgumentException
     */
    public ImageLoadFlow(String exUrl) throws IllegalArgumentException {
        if (TextUtils.isEmpty(exUrl)) {
            throw new IllegalArgumentException("argument is empty");
        }

        String[] strs = exUrl.split(",");
        mLocation = EventLocation.nameOf(strs[0]);
        mUrlList = new ArrayList<>();
        for (int i = 1; i < strs.length; i++) {
            mUrlList.add(strs[i]);
        }
    }

    /**
     * ロード
     *
     * @param context コンテキスト
     * @return BMPリスト
     */
    public ArrayList<Bitmap> load(Context context) {
        ArrayList<Bitmap> bmpList = new ArrayList<>();

        EventDB db = new EventDB(context);
        for (String url : mUrlList) {
            // DBから取得
            Bitmap bmp = db.query(mLocation, url);
            if (bmp != null) {
                bmpList.add(bmp);
                continue;
            }

            // なかったらURLへアクセスしにいく
            NetworkResult result = NetworkUtil.get(url, context);
            if (result == null || result.getResultCode() == 200) {
                byte[] bytes = result.getBytesBody();
                if (bytes != null) {
                    bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bmp != null) {
                        // 取得したBMPをDBに入れてBMPを返す
                        db.insert(mLocation, url, bmp);
                        bmpList.add(bmp);
                    }
                }
            }
        }

        return bmpList;
    }
}
