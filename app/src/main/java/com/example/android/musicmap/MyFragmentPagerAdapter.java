package com.example.android.musicmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 申源春 on 2017/7/4.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private String TAG = "MyFragmentPagerAdapter";
    private Context mContext;
    private List<Fragment> mFragments;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        initFragments();
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Log.d(TAG, "getItem: 0");
                return AlbumFragment.newInstance(2, ReadMusic.getAlbums());
            case 1:
                Log.d(TAG, "getItem: 1");
                return ArtistFragment.newInstance(1, ReadMusic.getArtists());
            case 2:
                Log.d(TAG, "getItem: 2");
                return SongFragment.newInstance(1, ReadMusic.getSongList());
            default:
                Log.d(TAG, "Failed in getItem(int position)");
                return mFragments.get(position);
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    /**
     * This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.album);
            case 1:
                return mContext.getString(R.string.artist);
            case 2:
                return mContext.getString(R.string.song);
            default:
                Log.d(TAG, "getPageTitle: BIGGER than 2");
                return "FAILED";
        }
    }

    private void initFragments(){
        mFragments = new ArrayList<>();
        mFragments.add(new AlbumFragment());
        mFragments.add(new ArtistFragment());
        mFragments.add(new SongFragment());
    }
}
