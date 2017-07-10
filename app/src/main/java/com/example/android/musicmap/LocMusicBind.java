package com.example.android.musicmap;

import org.litepal.crud.DataSupport;

/**
 * Created by 申源春 on 2017/7/10.
 */

public class LocMusicBind extends DataSupport{
    private double latitude;
    private double longitude;
    private String songGson;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitudeGson(double longitude) {
        this.longitude = longitude;
    }

    public String getSong() {
        return songGson;
    }

    public void setSongGson(String song) {
        this.songGson = song;
    }
}
