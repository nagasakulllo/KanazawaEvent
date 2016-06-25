package com.kanazawaevent.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kanazawaevent.R;
import com.kanazawaevent.view.activity.viewpager.PageChangeListener;
import com.kanazawaevent.view.activity.viewpager.PlaceholderFragment;
import com.kanazawaevent.view.activity.viewpager.SectionsPagerAdapter;
import com.kanazawaevent.view.layout.SlidingTabLayout;

public class MainActivity extends BaseActivity {
    private static final String URL_KANAZAWA_ARTS = "http://www.kanazawa-arts.or.jp";
    private static final String URL_KANAZAWA_OFFICIAL = "http://www4.city.kanazawa.lg.jp";
    private static final String URL_OSS = "file:///android_asset/oss/licence.html";
    private static final String URL_OPENDATA = "file:///android_asset/opendata/licence.html";

    // UIアイテム
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    int getNavViewId() {
        return R.id.nav_view;
    }

    @Override
    int getToolBarId() {
        return R.menu.menu_main;
    }

    @Override
    protected void prepareCreate() {
        mToolBarEnabled = true;
        mFabEnabled = false;
        mNavigationViewEnabled = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewPager
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // スライディングタブ
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        PageChangeListener listener = new PageChangeListener(mViewPager, mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(listener);
        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                if (position < -1) {
                    page.setAlpha(0);
                } else if (position <= 0) {
                    page.setAlpha(position + 1);
                } else if (position <= 1) {
                    page.setAlpha(1 - position);
                } else {
                    page.setAlpha(0);
                }
            }
        });

        // ツールバーのメニュークリック
        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.refresh) {
                    PlaceholderFragment fragment = getCurrentFragment();
                    if (fragment != null) {
                        fragment.showTimeLine(true);
                        return true;
                    }
                }

                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        PlaceholderFragment fragment = getCurrentFragment();
        if (fragment != null) {
            fragment.onActivityResume();
        }
    }

    @Override
    public void onPause() {
        PlaceholderFragment fragment = getCurrentFragment();
        if (fragment != null) {
            fragment.onActivityPause();
        }

        super.onPause();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String analyticsName = null;
        switch (item.getItemId()) {
            case R.id.kanazawa_arts:
                postIntent(URL_KANAZAWA_ARTS);
                analyticsName = "kanazawa_arts";
                break;
            case R.id.kanazawa_official:
                postIntent(URL_KANAZAWA_OFFICIAL);
                analyticsName = "kanazawa_official";
                break;
            case R.id.open_data:
                showLicenceDialog(R.string.open_data_licence, URL_OPENDATA);
                analyticsName = "open_data_license";
                break;
            case R.id.oss:
                showLicenceDialog(R.string.oss_licence, URL_OSS);
                analyticsName = "oss_license";
                break;
            default:
                break;
        }

        if (analyticsName != null) {
            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.ITEM_NAME, analyticsName);
            mFirebaseAnalytics.logEvent("select_navigation_content", params);
        }

        return super.onNavigationItemSelected(item);
    }

    private void postIntent(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void showLicenceDialog(int titleId, String url) {
        WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.dialog_licence, null);
        view.loadUrl(url);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(titleId)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private PlaceholderFragment getCurrentFragment() {
        return (PlaceholderFragment) mSectionsPagerAdapter
                .instantiateItem(mViewPager, mViewPager.getCurrentItem());
    }
}
