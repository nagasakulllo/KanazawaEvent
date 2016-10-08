package com.kanazawaevent.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kanazawaevent.BR;
import com.kanazawaevent.model.event.EventData;
import com.kanazawaevent.model.event.EventLocation;
import com.kanazawaevent.view.util.ColorUtil;

/**
 * Created by nagai on 2016/02/06.
 */
public class BindData extends BaseObservable {
    private String mGroup;
    private String mImageUrl;
    private String mTitle;
    private String mDates;
    private String mDescription;
    private String mUrl;

    private int mGroupVisibiility;
    private int mGroupColor;

    private FirebaseAnalytics mFirebaseAnalytics;
    /**
     * コンストラクタ
     *
     * @param eventData 　イベントデータ
     */
    public BindData(EventData eventData, Context context) {
        mGroup = eventData.getGroup();
        if (eventData.getImages().size() > 0) {
            // ImageViewのloadImage()でlocationを判断するためのprefixをつける。良いやり方ないかな。。
            String prefix = eventData.getLocation().name() + ",";
            StringBuilder builder = new StringBuilder(prefix);
            for (String image : eventData.getImages()) {
                // ","区切りで複数URLくっつける
                builder.append(eventData.getBaseUrl());
                builder.append(image);
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            mImageUrl = builder.toString();
        }
        mTitle = eventData.getTitle();
        mDates = eventData.getDates();
        mDescription = eventData.getDescription();

        // オープンデータの仕様が変わってALLのときはLinkの値ををそのまま使う
        mUrl = eventData.getLocation() == EventLocation.ALL
                ? eventData.getLink()
                : eventData.getBaseUrl() + eventData.getLink();

        mGroupVisibiility = !TextUtils.isEmpty(mGroup) ? View.VISIBLE : View.GONE;
        if (mGroupVisibiility == View.VISIBLE) {
            for (EventLocation location : EventLocation.values()) {
                if (context.getString(location.getStrId()).equals(mGroup)) {
                    mGroupColor = ColorUtil.getText(context, location.getColor());
                    break;
                }
            }
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Bindable
    public String getGroup() {
        return mGroup;
    }

    @Bindable
    public String getImageUrl() {
        return mImageUrl;
    }

    @Bindable
    public String getTitle() {
        return mTitle;
    }

    @Bindable
    public String getDates() {
        return mDates;
    }

    @Bindable
    public String getDescription() {
        return mDescription;
    }

    @Bindable
    public String getUrl() {
        return mUrl;
    }

    public void setGroup(String group) {
        mGroup = group;
        notifyPropertyChanged(BR.group);
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
        notifyPropertyChanged(BR.imageUrl);
    }

    public void setTitle(String title) {
        mTitle = title;
        notifyPropertyChanged(BR.title);
    }

    public void setDates(String dates) {
        mDates = dates;
        notifyPropertyChanged(BR.dates);
    }

    public void setDescription(String description) {
        mDescription = description;
        notifyPropertyChanged(BR.description);
    }

    public void setUrl(String url) {
        mUrl = url;
        notifyPropertyChanged(BR.url);
    }

    @Bindable
    public int getGroupVisibility() {
        return mGroupVisibiility;
    }

    public void setGroupVisibility(int visibility) {
        mGroupVisibiility = visibility;
    }

    @Bindable
    public int getGroupColor() {
        return mGroupColor;
    }

    public void setGroupColor(int groupColor) {
        mGroupColor = groupColor;
    }

    // クリック時
    public void onItemClicked(View view) {
        Uri uri = Uri.parse(mUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        Activity activity = (Activity) view.getContext();
        activity.startActivity(intent);

        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.LOCATION, mGroup);
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, mTitle);
        params.putString(FirebaseAnalytics.Param.VALUE, mUrl);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
    }
}
