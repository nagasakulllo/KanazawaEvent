package com.kanazawaevent.model.event;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by nagai on 2016/02/07.
 */
class EventDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "event.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_EVENT = "event";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_BASE_URL = "base_url";
    // groupは予約語なので"_"をつける
    private static final String COLUMN_GROUP = "_group";
    private static final String COLUMN_DATE_FROM = "date_from";
    private static final String COLUMN_DATE_TO = "date_to";
    private static final String COLUMN_DATES = "dates";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGES = "images";

    private static final String TABLE_IMAGE = "image";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_BMP = "bmp";

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    EventDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_EVENT + " ("
                + COLUMN_LOCATION + " TEXT NOT NULL,"
                + COLUMN_BASE_URL + " TEXT NOT NULL,"
                + COLUMN_GROUP + " TEXT ,"
                + COLUMN_DATE_FROM + " TEXT ,"
                + COLUMN_DATE_TO + " TEXT ,"
                + COLUMN_DATES + " TEXT ,"
                + COLUMN_TITLE + " TEXT NOT NULL,"
                + COLUMN_LINK + " TEXT ,"
                + COLUMN_DESCRIPTION + " TEXT ,"
                + COLUMN_IMAGES + " TEXT "
                + ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_IMAGE + " ("
                + COLUMN_LOCATION + " TEXT NOT NULL,"
                + COLUMN_URL + " TEXT NOT NULL,"
                + COLUMN_BMP + " BLOB NOT NULL"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * イベントデータのinsert
     *
     * @param eventList イベントデータリスト
     * @return boolean
     */
    synchronized boolean insert(ArrayList<EventData> eventList) {
        int insertNum = 0;

        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();

            for (EventData event : eventList) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_LOCATION, event.getLocation().name());
                values.put(COLUMN_BASE_URL, event.getBaseUrl());
                values.put(COLUMN_GROUP, event.getGroup());
                values.put(COLUMN_DATE_FROM, event.getDateFrom());
                values.put(COLUMN_DATE_TO, event.getDateTo());
                values.put(COLUMN_DATES, event.getDates());
                values.put(COLUMN_TITLE, event.getTitle());
                values.put(COLUMN_LINK, event.getLink());
                values.put(COLUMN_DESCRIPTION, event.getDescription());

                // Imagesは","区切りで格納
                StringBuilder builder = new StringBuilder();
                for (String image : event.getImages()) {
                    builder.append(image);
                    builder.append(",");
                }
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                values.put(COLUMN_IMAGES, builder.toString());

                if (db.insert(TABLE_EVENT, null, values) != -1) {
                    insertNum++;
                }
            }
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return eventList.size() == insertNum;
    }

    /**
     * BMPのinsert
     *
     * @param location 場所
     * @param url      URL
     * @param bmp      BMP
     * @return boolean
     */
    boolean insert(EventLocation location, String url, Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)) {
            byte[] bytes = byteArrayOutputStream.toByteArray();
            if (bytes != null) {
                return insert(location, url, bytes);
            }
        }

        return false;
    }

    /**
     * イベントデータのquery
     *
     * @param location 場所
     * @return イベントデータリスト
     */
    synchronized ArrayList<EventData> query(EventLocation location) {
        ArrayList<EventData> eventList = new ArrayList<>();

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            cursor = db.query(TABLE_EVENT, null,
                    COLUMN_LOCATION + " = ?",
                    new String[]{location.name()},
                    null, null, null, null);
            while (cursor.moveToNext()) {
                String baseUrl = cursor.getString(cursor.getColumnIndex(COLUMN_BASE_URL));
                try {
                    JSONObject json = new JSONObject();
                    json.put(EventDataKey.GROUP.getKey(),
                            cursor.getString(cursor.getColumnIndex(COLUMN_GROUP)));
                    json.put(EventDataKey.DATE_FROM.getKey(),
                            cursor.getString(cursor.getColumnIndex(COLUMN_DATE_FROM)));
                    json.put(EventDataKey.DATE_TO.getKey(),
                            cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TO)));
                    json.put(EventDataKey.DATES.getKey(),
                            cursor.getString(cursor.getColumnIndex(COLUMN_DATES)));
                    json.put(EventDataKey.TITLE.getKey(),
                            cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                    json.put(EventDataKey.LINK.getKey(),
                            cursor.getString(cursor.getColumnIndex(COLUMN_LINK)));
                    json.put(EventDataKey.DESCRIPTION.getKey(),
                            cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                    JSONArray imageArray = new JSONArray();
                    String imageStr = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGES));
                    if (!TextUtils.isEmpty(imageStr)) {
                        String[] images = imageStr.split(",");
                        for (String image : images) {
                            imageArray.put(image);
                        }
                    }
                    json.put(EventDataKey.IMAGES.getKey(), imageArray);

                    EventData data = new EventData(location, baseUrl, json);
                    eventList.add(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return eventList;
    }

    /**
     * BMPのquery
     *
     * @param location 場所
     * @param url      URL
     * @return BMP
     */
    Bitmap query(EventLocation location, String url) {
        byte[] bytes = queryImage(location, url);
        if (bytes != null) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }

        return null;
    }

    /**
     * 特定ロケーションデータのdelete
     *
     * @param location 場所
     */
    synchronized void deleteLocationData(EventLocation location) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            String[] str = new String[]{location.name()};
            db.delete(TABLE_EVENT, COLUMN_LOCATION + " = ?", str);
            db.delete(TABLE_IMAGE, COLUMN_LOCATION + " = ?", str);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private synchronized boolean insert(EventLocation location, String url, byte[] bytes) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_LOCATION, location.name());
            values.put(COLUMN_URL, url);
            values.put(COLUMN_BMP, bytes);
            return db.insert(TABLE_IMAGE, null, values) != -1;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private synchronized byte[] queryImage(EventLocation location, String url) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            cursor = db.query(TABLE_IMAGE, null,
                    COLUMN_LOCATION + " = ? and " + COLUMN_URL + " = ?",
                    new String[]{location.name(), url},
                    null, null, null, null);
            if (cursor.moveToNext()) {
                return cursor.getBlob(cursor.getColumnIndex(COLUMN_BMP));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return null;
    }
}
