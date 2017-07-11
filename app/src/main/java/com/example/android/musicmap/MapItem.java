package com.example.android.musicmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by 申源春 on 2017/7/11.
 */

public class MapItem implements ClusterItem {
    private LatLng mPosition;
    public MapItem(double lat, double lng){
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
