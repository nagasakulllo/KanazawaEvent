package com.kanazawaevent.view.activity.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kanazawaevent.R;
import com.kanazawaevent.model.event.EventLocation;
import com.kanazawaevent.model.event.TimeLineManager;
import com.kanazawaevent.view.adapter.RecyclerViewAdapter;
import com.kanazawaevent.view.util.ColorUtil;

/**
 * Created by nagai on 2016/02/07.
 */
public class PlaceholderFragment extends Fragment {
    private static final String KEY_LOCATION = "location";
    // UIアイテム
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private StaggeredGridLayoutManager mLayoutManager;
    private EventLocation mLocation;
    private TimeLineManager mManager;

    public PlaceholderFragment() {
    }

    /**
     * インスタンス生成
     *
     * @param location 場所
     * @return PlaceholderFragment
     */
    public static PlaceholderFragment newInstance(EventLocation location) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Bundle bundle = getArguments();
        mLocation = (EventLocation) bundle.getSerializable(KEY_LOCATION);

        // Viewの設定
        initializeView(rootView);
        setListener();

        // タイムラインの管理
        mManager = new TimeLineManager(
                (RecyclerViewAdapter) mRecyclerView.getAdapter(), mLocation, getContext());

        return rootView;
    }

    public void onActivityResume() {
        if (mManager != null) {
            mManager.startExecutor();
        }
    }

    public void onActivityPause() {
        if (mManager != null) {
            mManager.stopExecutor();
        }
    }

    /**
     * タイムラインの表示
     */
    public void showTimeLine(boolean isDataClear) {
        setRefleshLayoutEnabled(true);

        if (isDataClear) {
            mManager.clear();
        }

        if (!mManager.show()) {
            Toast toast = Toast.makeText(getContext(), R.string.error_network, Toast.LENGTH_SHORT);
            toast.show();
        }

        setRefleshLayoutEnabled(false);
    }

    /**
     * Viewの初期化
     *
     * @param view rootView
     */
    private void initializeView(View view) {
        // 背景色をアイテムごとに変える
        view.setBackgroundColor(ColorUtil.getBack(getContext(), mLocation.getColor()));

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipelayout);

        mRecyclerView.setHasFixedSize(true);
        // RecyclerViewを2列にする
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * リスナのセット
     */
    private void setListener() {
        // スワイプリフレッシュのリスナ
        mSwipeRefreshLayout.setColorSchemeResources(
                ColorUtil.Color.RED.getBack(), ColorUtil.Color.GREEN.getBack(),
                ColorUtil.Color.BLUE.getBack(), ColorUtil.Color.ORANGE.getBack());
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showTimeLine(true);
            }
        });
    }

    private void setRefleshLayoutEnabled(boolean isEnaled) {
        if (mSwipeRefreshLayout.isRefreshing() == !isEnaled) {
            mSwipeRefreshLayout.setRefreshing(isEnaled);
        }
    }
}
