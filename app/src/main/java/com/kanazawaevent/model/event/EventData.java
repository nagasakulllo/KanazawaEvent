package com.kanazawaevent.model.event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nagai on 2016/01/10.
 */
public class EventData {
    private EventLocation mLocation;
    private String mBaseUrl;

    private String mGroup;
    private String mDateFrom;
    private String mDateTo;
    private String mDates;
    private String mTitle;
    private String mLink;
    private String mDescription;
    private ArrayList<String> mImages;

    /**
     * コンストラクタ
     *
     * @param location 場所
     * @param baseUrl  ベースURL
     * @param item     アイテム
     * @throws IllegalArgumentException
     */
    public EventData(EventLocation location,
                     String baseUrl, JSONObject item) throws IllegalArgumentException {
        try {
            mLocation = location;
            mBaseUrl = baseUrl;

            if (item.has(EventDataKey.GROUP.getKey())) {
                mGroup = item.getString(EventDataKey.GROUP.getKey());
            }
            mDateFrom = item.getString(EventDataKey.DATE_FROM.getKey());
            mDateTo = item.getString(EventDataKey.DATE_TO.getKey());
            mDates = item.getString(EventDataKey.DATES.getKey());
            mTitle = item.getString(EventDataKey.TITLE.getKey());
            mLink = item.getString(EventDataKey.LINK.getKey());
            mDescription = item.getString(EventDataKey.DESCRIPTION.getKey());

            mImages = new ArrayList<>();
            JSONArray imageArray = item.getJSONArray(EventDataKey.IMAGES.getKey());
            for (int i = 0; i < imageArray.length(); i++) {
                mImages.add(imageArray.getString(i));
            }

        } catch (JSONException e) {
            throw new IllegalArgumentException("json is invalid");
        }
    }

    public EventLocation getLocation() {
        return mLocation;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public String getGroup() {
        return mGroup;
    }

    public String getDateFrom() {
        return mDateFrom;
    }

    public String getDateTo() {
        return mDateTo;
    }

    public String getDates() {
        return mDates;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLink() {
        return mLink;
    }

    public String getDescription() {
        return mDescription;
    }

    public ArrayList<String> getImages() {
        return mImages;
    }
}
