package com.kanazawaevent.view.activity.viewpager;

import android.support.v4.view.ViewPager;

/**
 * Created by nagai on 2016/02/07.
 */
public class PageChangeListener implements ViewPager.OnPageChangeListener {
    private ViewPager mPager;
    private SectionsPagerAdapter mAdapter;
    private int mPrePosition;

    public PageChangeListener(ViewPager pager, SectionsPagerAdapter adapter) {
        mPager = pager;
        mAdapter = adapter;
        mPrePosition = -1;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPrePosition != position) {
            PlaceholderFragment fragment = (PlaceholderFragment) mAdapter.instantiateItem(mPager, position);
            fragment.showTimeLine(false);
            mPrePosition = position;
        }
    }

    @Override
    public void onPageSelected(int position) {
        // こっちでやると最初のページときに呼ばれない
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
