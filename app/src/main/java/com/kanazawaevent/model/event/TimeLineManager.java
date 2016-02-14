package com.kanazawaevent.model.event;

import android.content.Context;
import android.os.Handler;

import com.kanazawaevent.view.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     */
    public void show(final ShowTimeLineListener listener) {
        final Handler handler = new Handler();
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // タイムライン取得
                final EventLoader loader = new EventLoader(mLocation);
                final ArrayList<EventData> eventList = loader.load(mContext);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (eventList.size() > 0) {
                            if (mAdapter != null) {
                                mAdapter.setData(eventList);
                            }
                            listener.onShow(true);
                        } else {
                            listener.onShow(loader.getError() == EventLoader.Error.NO_DATA);
                        }
                    }
                });
            }
        });

        executor.shutdown();
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

    public void onActivityResume() {
        if (mAdapter != null) {
            mAdapter.onActivityResume();
        }
    }

    public void onActivityPause() {
        if (mAdapter != null) {
            mAdapter.onActivityPause();
        }
    }

    public void onFragmentPause() {
        if (mAdapter != null) {
            mAdapter.onFragmentPause();
        }
    }

    public void onFragmentDestroy() {
        mAdapter = null;
    }

    public interface ShowTimeLineListener {
        void onShow(boolean result);
    }
}
