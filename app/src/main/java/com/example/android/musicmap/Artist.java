package com.example.android.musicmap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 申源春 on 2017/7/4.
 */

public class Artist implements Parcelable{
    private String name;
    private int albumCount;
    private int songCount;

    public Artist(String name, int albumCount, int songCount) {
        this.name = name;
        this.albumCount = albumCount;
        this.songCount = songCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAlbumCount() {
        return albumCount;
    }

    public void setAlbumCount(int albumCount) {
        this.albumCount = albumCount;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(albumCount);
        dest.writeInt(songCount);
    }

    public static final Parcelable.Creator<Artist> CREATOR
            = new Parcelable.Creator<Artist>() {
        /**
         * Create a new instance of the Parcelable class, instantiating it
         * from the given Parcel whose data had previously been written by
         * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
         *
         * @param source The Parcel to read the object's data from.
         * @return Returns a new instance of the Parcelable class.
         */
        @Override
        public Artist createFromParcel(Parcel source) {
            Artist artist = new Artist(source.readString(),source.readInt(),source.readInt());
            return artist;
        }

        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
