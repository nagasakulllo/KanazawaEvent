package com.kanazawaevent.model.event;

import com.kanazawaevent.R;
import com.kanazawaevent.view.util.ColorUtil.Color;

/**
 * イベント場所
 * Created by nagai on 2016/02/04.
 */
public enum EventLocation {
    /**
     * 全施設
     */
    ALL("http://www.kanazawa-arts.or.jp/event-all.json",
            R.string.location_all,
            Color.RED),
    /**
     * 金沢能楽美術館
     */
    NOH_MUSEUM(
            "http://www.kanazawa-noh-museum.gr.jp/event.json",
            R.string.location_noh_museum,
            Color.PURPLE),
    /**
     * 金沢湯涌創作の森
     */
    SOSAKU_MORI(
            "http://www.sousaku-mori.gr.jp/event.json",
            R.string.location_sosacku_mori,
            Color.BLUE),
    /**
     * 金沢卯辰山工芸工房
     */
    UTATSU_KOGEI(
            "http://www.utatsu-kogei.gr.jp/event.json",
            R.string.location_utatsu_kogei,
            Color.CYAN),
    /**
     * 金沢市民芸術村
     */
    ART_VILLAGE(
            "http://www.artvillage.gr.jp/event.json",
            R.string.location_art_village,
            Color.GREEN),
    /**
     * 金沢市アートホール
     */
    ART_HOLE(
            "http://www.art-h.gr.jp/event.json",
            R.string.location_art_hole,
            Color.LIME),
    /**
     * 金沢市文化ホール
     */
    BUNKA_HOLE(
            "http://www.bunka-h.gr.jp/event.json",
            R.string.location_bunka_hole,
            Color.YELLOW),
    /**
     * 金沢歌劇座
     */
    KAGEKIZA(
            "http://www.kagekiza.gr.jp/event.json",
            R.string.location_kagekiza,
            Color.ORANGE),
    /**
     * 金沢芸術創造財団
     */
    KANAZAWA_ARTS(
            "http://www.kanazawa-arts.or.jp/event.json",
            R.string.location_kanazawa_arts,
            Color.BROWN),;

    private String mUrl;
    private int mStrId;
    private Color mColor;

    /**
     * コンストラクタ
     *
     * @param url   URL
     * @param strId 文字列ID
     * @param color カラーID
     */
    EventLocation(String url, int strId, Color color) {
        mUrl = url;
        mStrId = strId;
        mColor = color;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getStrId() {
        return mStrId;
    }

    public Color getColor() {
        return mColor;
    }

    public static EventLocation ordinalOf(int ordinal) {
        for (EventLocation location : EventLocation.values()) {
            if (ordinal == location.ordinal()) {
                return location;
            }
        }

        return EventLocation.ALL;
    }

    public static EventLocation nameOf(String name) {
        for (EventLocation location : EventLocation.values()) {
            if (name.equals(location.name())) {
                return location;
            }
        }

        return EventLocation.ALL;
    }
}
