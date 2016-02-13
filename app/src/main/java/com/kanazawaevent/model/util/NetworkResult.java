package com.kanazawaevent.model.util;

import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by nagai on 2016/02/04.
 */
public class NetworkResult {
    private int mResultCode;
    private byte[] mBytesBody;
    private String mStringBody;

    /**
     * コンストラクタ
     *
     * @param response レスポンス
     */
    public NetworkResult(Response response) {
        mResultCode = response.code();

        try {
            String contentType = response.headers().get("Content-Type");
            if (contentType.contains("image")) {
                mBytesBody = response.body().bytes();
            } else {
                mStringBody = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getResultCode() {
        return mResultCode;
    }

    public byte[] getBytesBody() {
        return mBytesBody;
    }

    public String getStringBody() {
        return mStringBody;
    }
}
