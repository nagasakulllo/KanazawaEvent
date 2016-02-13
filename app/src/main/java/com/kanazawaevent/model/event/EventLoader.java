package com.kanazawaevent.model.event;

import android.content.Context;

import com.kanazawaevent.model.util.NetworkResult;
import com.kanazawaevent.model.util.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nagai on 2016/01/10.
 */
class EventLoader {
    private EventLocation mLocation;
    private Error mError;

    /**
     * コンストラクタ
     *
     * @param location
     * @throws IllegalArgumentException
     */
    EventLoader(EventLocation location) throws IllegalArgumentException {
        if (location == null) {
            throw new IllegalArgumentException("location is empty");
        }

        mLocation = location;
        mError = Error.NO_DATA;
    }

    /**
     * イベントデータのロード
     *
     * @param context コンテキスト
     * @return イベントデータ
     */
    ArrayList<EventData> load(Context context) {
        ArrayList<EventData> eventList = new ArrayList<>();

        // まずDBから取得
        EventDB db = new EventDB(context);
        eventList = db.query(mLocation);
        if (eventList.size() > 0) {
            return eventList;
        }

        // DBからとれなかったらネットワーク通信
        NetworkResult result = NetworkUtil.get(mLocation.getUrl(), context);
        if (result == null || result.getResultCode() != 200) {
            mError = Error.NETWORK_ERROR;
            return eventList;
        }

        mError = Error.NO_DATA;
        try {
            JSONObject json = new JSONObject(result.getStringBody());
            String baseUrl = json.getString(EventDataKey.BASE_URL.getKey());
            JSONArray eventArray = json.getJSONArray(EventDataKey.ITEMS.getKey());
            for (int i = 0; i < eventArray.length(); i++) {
                EventData data = new EventData(mLocation, baseUrl, eventArray.getJSONObject(i));
                if (data != null) {
                    eventList.add(data);
                }
            }

            // DBに入れる
            db.insert(eventList);
        } catch (JSONException e) {
            mError = Error.FORMAT_ERROR;
            e.printStackTrace();
        }

        return eventList;
    }

    public Error getError() {
        return mError;
    }

    enum Error {
        NO_DATA,
        NETWORK_ERROR,
        FORMAT_ERROR,
    }
}
