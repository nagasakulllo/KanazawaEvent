package com.kanazawaevent.view.adapter;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.kanazawaevent.BR;

/**
 * Created by nagai on 2016/02/06.
 */
public class BindAdvertisement extends BaseObservable {
    private boolean mLayoutVisibility;
    private boolean mBannerVisibility;
    private boolean mInterstitialVisibility;

    /**
     * コンストラクタ
     */
    public BindAdvertisement() {
        mLayoutVisibility = false;
        mBannerVisibility = false;
        mInterstitialVisibility = false;
    }

    @Bindable
    public boolean isLayoutVisibility() {
        return mBannerVisibility || mInterstitialVisibility;
    }

    public void setLayoutVisibility(boolean layoutVisibility) {
        mLayoutVisibility = layoutVisibility;
        notifyPropertyChanged(BR.layoutVisibility);
    }

    @Bindable
    public boolean isBannerVisibility() {
        return mBannerVisibility;
    }

    public void setBannerVisibility(boolean bannerVisibility) {
        mBannerVisibility = bannerVisibility;
        notifyPropertyChanged(BR.bannerVisibility);
    }

    @Bindable
    public boolean isInterstitialVisibility() {
        return mInterstitialVisibility;
    }

    public void setInterstitialVisibility(boolean interstitialVisibility) {
        mInterstitialVisibility = interstitialVisibility;
        notifyPropertyChanged(BR.interstitialVisibility);
    }
}
