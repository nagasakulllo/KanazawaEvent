package com.kanazawaevent.model.event;

import android.content.Context;

import com.kanazawaevent.view.adapter.RecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Created by nagai on 2016/02/07.
 */
public class TimeLineManager {
    private RecyclerViewAdapter mAdapter;
    private EventLocation mLocation;
    private Context mContext;

    /**
     * コンストラクタ
     *
     * @param adapter  adapter
     * @param location location
     * @param context  context
     */
    public TimeLineManager(RecyclerViewAdapter adapter,
                           EventLocation location, Context context) {
        if (adapter == null || location == null || context == null) {
            throw new IllegalArgumentException("argument is empty");
        }

        mAdapter = adapter;
        mLocation = location;
        mContext = context;
    }

    /**
     * タイムラインの表示
     *
     * @return boolean
     */
    public boolean show() {
        // タイムライン取得
        EventLoader loader = new EventLoader(mLocation);
        ArrayList<EventData> eventList = loader.load(mContext);
        if (eventList.size() > 0) {
            mAdapter.setData(eventList);
            return true;
        }

        // データなしの場合はtrueにする
        return loader.getError() == EventLoader.Error.NO_DATA;
    }

    /**
     * クリア
     */
    public void clear() {
        mAdapter.clear();

        // DBクリア
        EventDB db = new EventDB(mContext);
        db.deleteLocationData(mLocation);
    }

    public void startExecutor() {
        mAdapter.startExecutor();
    }

    public void stopExecutor() {
        mAdapter.stopExecutor();
    }
}
