package com.kanazawaevent.view.adapter;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kanazawaevent.BR;
import com.kanazawaevent.R;
import com.kanazawaevent.model.event.EventData;
import com.kanazawaevent.model.event.ImageLoadFlow;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nagai on 2015/10/06.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder> {
    // スレッド3つ
    private static ExecutorService sExecutorService = Executors.newFixedThreadPool(3);;
    private static Handler sHandler = new Handler();

    private ArrayList<EventData> mDataset;

    /**
     * ViewHolder
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        // Bindデータ
        public ViewDataBinding mBinding;
        public AdView mBanner;
        public AdView mInterstitial;
        public ImageView mImage;

        public ItemViewHolder(View v) {
            super(v);
            mBinding = DataBindingUtil.bind(v);
            mBanner = (AdView) v.findViewById(R.id.banner);
            mInterstitial = (AdView) v.findViewWithTag(R.id.interstitial);
            mImage = (ImageView) v.findViewById(R.id.image);
        }
    }

    /**
     * コンストラクタ
     */
    public RecyclerViewAdapter() {
        mDataset = new ArrayList<>();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recyler, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        StaggeredGridLayoutManager.LayoutParams layoutParams =
                (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
        BindAdvertisement bindAdvertisement = new BindAdvertisement();
        EventData data = mDataset.get(position);
        if (data != null) {
            // イベント情報の場合
            // タイトルが長かったらFullSpanにする
            layoutParams.setFullSpan(data.getTitle().length() > 30);
            holder.itemView.setLayoutParams(layoutParams);

            BindData bindData = new BindData(data, holder.mBinding.getRoot().getContext());
            holder.mBinding.setVariable(BR.data, bindData);
        } else {
            // 広告
            layoutParams.setFullSpan(true);
            // タグだけImageViewにセット
            holder.mImage.setTag(position);

            // 広告ロード
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            if (position == getItemCount()) {
                bindAdvertisement.setInterstitialVisibility(true);
                holder.mInterstitial.loadAd(adRequest);
            } else {
                bindAdvertisement.setBannerVisibility(true);
                holder.mBanner.loadAd(adRequest);
            }
        }

        holder.mBinding.setVariable(BR.advt, bindAdvertisement);
        holder.mBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void startExecutor() {
        if (sExecutorService == null || sExecutorService.isShutdown()) {
            sExecutorService = Executors.newFixedThreadPool(3);
        }
    }

    public void stopExecutor() {
        sHandler.removeCallbacksAndMessages(null);
        if (sExecutorService != null && !sExecutorService.isShutdown()) {
            sExecutorService.shutdownNow();
        }
    }

    public void setData(ArrayList<EventData> dataset) {
        if (dataset != null) {
            mDataset.clear();
            mDataset = dataset;

            for (int i = 1; 10 * i <= mDataset.size(); i++) {
                // 途中にBannerを入れる
                mDataset.add(10 * i - 1, null);
            }
            // 最後にInterstitialを入れる
            //mDataset.add(null);
            notifyDataSetChanged();
        }
    }

    public void addData(ArrayList<EventData> dataset) {
        if (dataset != null) {
            int dataNum = getItemCount();
            if (mDataset.addAll(dataset)) {
                notifyDataSetChanged();
            }
        }
    }

    public void clear() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(final ImageView view, final String url) {
        final java.lang.Object tag = url;
        view.setTag(tag);
        view.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(url)) {
            view.setVisibility(View.GONE);
            return;
        }

        sExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                if (!tag.equals(view.getTag())) {
                    // 既に画面外なら何もしない
                    return;
                }

                ImageLoadFlow flow = new ImageLoadFlow(url);
                ArrayList<Bitmap> bmpList = flow.load(view.getContext().getApplicationContext());
                ImageAdapter adapter = new ImageAdapter(view, tag, bmpList);
                adapter.execute();
            }
        });
    }

    private static class ImageAdapter {
        private static final int DELAYED = 5000;

        private ImageView mView;
        private java.lang.Object mTag;
        private int mIndex;
        private ArrayList<Bitmap> mBmpList;
        private Runnable mUpdateRunnable;

        private ImageAdapter(ImageView view, java.lang.Object tag, ArrayList<Bitmap> bmpList) {
            mView = view;
            mTag = tag;
            mIndex = 0;
            mBmpList = bmpList;
            mUpdateRunnable = null;
        }

        private void execute() {
            if (mBmpList.size() > 0) {
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mTag.equals(mView.getTag())) {
                            mView.setImageBitmap(mBmpList.get(0));
                        }
                    }
                });

                if (mBmpList.size() > 1) {
                    // 3秒置きに画像差し替え
                    mUpdateRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (mTag.equals(mView.getTag())) {
                                mView.setImageBitmap(mBmpList.get(mIndex));
                                // 横に200移動
//                                TranslateAnimation animation = new TranslateAnimation(
//                                        0, -200, 0, 0);
//                                animation.setDuration(200);
//                                mView.setAnimation(animation);

                                mIndex++;
                                if (mBmpList.size() <= mIndex) {
                                    mIndex = 0;
                                }
                                sHandler.postDelayed(mUpdateRunnable, DELAYED);
                            } else {
                                sHandler.removeCallbacks(mUpdateRunnable);
                            }
                        }
                    };

                    sHandler.postDelayed(mUpdateRunnable, DELAYED);
                }
            } else {
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mTag.equals(mView.getTag())) {
                            mView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    }
}
