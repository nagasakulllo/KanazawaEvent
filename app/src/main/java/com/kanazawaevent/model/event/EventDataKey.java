package com.kanazawaevent.model.event;

/**
 * Created by nagai on 2016/01/10.
 */
enum EventDataKey {
    BASE_URL("base_url"),
    ITEMS("items"),
    GROUP("group"),
    DATE_FROM("date_from"),
    DATE_TO("date_to"),
    DATES("dates"),
    TITLE("title"),
    LINK("link"),
    DESCRIPTION("description"),
    IMAGES("images"),;

    private String mKey;

    EventDataKey(String key) {
        mKey = key;
    }

    String getKey() {
        return mKey;
    }
}
