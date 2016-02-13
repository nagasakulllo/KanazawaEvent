//package com.smartkanazawa.view.adapter;
//
//import android.os.Handler;
//
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//
//
///**
// * Created by nagai on 2016/02/09.
// */
//public class AdLoaderTask {
//    private static final long TIME_PERIOD = 60;
//
//    private AdView mBanner;
//    private static ScheduledExecutorService sExecutor;
//    private ScheduledFuture<?> mScheduledFuture;
//    private Handler mHandler;
//
//    public AdLoaderTask(AdView banner) {
//        mBanner = banner;
//        if (sExecutor.isShutdown()) {
//            sExecutor = Executors.newSingleThreadScheduledExecutor();
//        }
//        mHandler = new Handler();
//    }
//
//    public void start() {
//        if (mScheduledFuture != null && !mScheduledFuture.isCancelled()) {
//            return;
//        }
//
//        mScheduledFuture = sExecutor.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mBanner != null) {
//                            AdRequest adRequest = new AdRequest.Builder()
//                                    .addTestDevice("6CEC6C9B2BB9B81F235267CDF8AFF05C")
//                                    .build();
//                            mBanner.loadAd(adRequest);
//                        }
//                    }
//                });
//            }
//        }, 0, TIME_PERIOD, TimeUnit.SECONDS);
//    }
//
//    public void cancel() {
//        if (mScheduledFuture == null || mScheduledFuture.isCancelled()) {
//            return;
//        }
//
//        mScheduledFuture.cancel(true);
//    }
//}
