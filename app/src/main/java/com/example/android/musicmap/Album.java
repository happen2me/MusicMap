package com.example.android.musicmap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 申源春 on 2017/7/4.
 */

public class Album implements Parcelable{
    private String name;
    private String artist;
    private int albumCoverImageId;

    public Album(String name, String artist, int albumCoverImageId){
        this.name = name;
        this.artist = artist;
        this.albumCoverImageId = albumCoverImageId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getAlbumCoverImageId() {
        return albumCoverImageId;
    }

    public void setAlbumCoverImageId(int albumCoverImageId) {
        this.albumCoverImageId = albumCoverImageId;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(artist);
        dest.writeInt(albumCoverImageId);
    }

    public static final Parcelable.Creator<Album> CREATOR
            = new Parcelable.Creator<Album>() {
        /**
         * Create a new instance of the Parcelable class, instantiating it
         * from the given Parcel whose data had previously been written by
         * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
         *
         * @param source The Parcel to read the object's data from.
         * @return Returns a new instance of the Parcelable class.
         */
        @Override
        public Album createFromParcel(Parcel source) {
            Album album = new Album(source.readString(),source.readString(),source.readInt());
            return album;
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
