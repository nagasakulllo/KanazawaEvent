package com.kanazawaevent.view.activity.viewpager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kanazawaevent.model.event.EventLocation;

/**
 * Created by nagai on 2016/02/07.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        EventLocation newLocation = EventLocation.ordinalOf(position);
        return PlaceholderFragment.newInstance(newLocation);
    }

    @Override
    public int getCount() {
        return EventLocation.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        EventLocation location = EventLocation.ordinalOf(position);
        return mContext.getString(location.getStrId());
    }
}
