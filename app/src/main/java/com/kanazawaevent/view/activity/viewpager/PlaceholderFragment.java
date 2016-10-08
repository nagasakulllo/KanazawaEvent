package com.kanazawaevent.view.activity.viewpager;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kanazawaevent.BR;
import com.kanazawaevent.R;
import com.kanazawaevent.databinding.FragmentMainBinding;
import com.kanazawaevent.model.event.EventLocation;
import com.kanazawaevent.model.event.TimeLineManager;
import com.kanazawaevent.model.event.TimeLineManager.ShowTimeLineListener;
import com.kanazawaevent.view.adapter.BindAdvertisement;
import com.kanazawaevent.view.adapter.RecyclerViewAdapter;
import com.kanazawaevent.view.util.ColorUtil;

import java.util.Random;

/**
 * Created by nagai on 2016/02/07.
 */
public class PlaceholderFragment extends Fragment {
    private static final String KEY_LOCATION = "location";
    // UIアイテム
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AdView mBanner;
    private AdView mInterstitial;
    FragmentMainBinding mBinding;

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        View rootView = mBinding.getRoot();
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

    @Override
    public void onPause() {
        mManager.onFragmentPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mManager.onFragmentDestroy();
        mRecyclerView.setAdapter(null);
        mRecyclerView = null;

        super.onDestroyView();
    }

    public void onActivityResume() {
        if (mManager != null) {
            mManager.onActivityResume();
        }
    }

    public void onActivityPause() {
        if (mManager != null) {
            mManager.onActivityPause();
        }
    }

    /**
     * タイムラインの表示
     */
    public void showTimeLine(boolean isDataClear) {
        if (mManager == null) {
            return;
        }

        setRefleshLayoutEnabled(true);

        if (isDataClear) {
            mManager.clear();
        }

        ShowTimeLineListener listener = new ShowTimeLineListener() {
            @Override
            public void onShow(boolean result) {
                if (!result) {
                    Toast toast = Toast.makeText(getContext(), R.string.error_network, Toast.LENGTH_SHORT);
                    toast.show();
                }

                setRefleshLayoutEnabled(false);
            }
        };

        mManager.show(listener);
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

        mBanner = (AdView) view.findViewById(R.id.banner);
        mInterstitial = (AdView) view.findViewById(R.id.interstitial);

        // 広告の処理
        BindAdvertisement bindAdvertisement = new BindAdvertisement();
        // 広告ロード
        AdRequest adRequest = new AdRequest.Builder().build();
        Random random = new Random();
//        if (random.nextBoolean()) {
//            bindAdvertisement.setInterstitialVisibility(true);
//            mInterstitial.loadAd(adRequest);
//        } else {
            bindAdvertisement.setBannerVisibility(true);
            mBanner.loadAd(adRequest);
//        }
        mBinding.setVariable(BR.advt, bindAdvertisement);
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
        if (mSwipeRefreshLayout == null) {
            return;
        }

        if (mSwipeRefreshLayout.isRefreshing() == !isEnaled) {
            mSwipeRefreshLayout.setRefreshing(isEnaled);
        }
    }
}
