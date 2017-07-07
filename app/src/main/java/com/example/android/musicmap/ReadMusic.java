package com.example.android.musicmap;

/**
 * Created by 申源春 on 2017/7/5.
 */
import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.android.musicmap.MyApplication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 申源春 on 2017/7/5.
 */

public class ReadMusic {

    private static String TAG = "ReadMusicInfo";

    private static Map<Long, String> getCoverPathMap(){
        HashMap<Long, String> map = new HashMap<Long, String>();
        Cursor c= MyApplication.getContext().getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                null,
                null,
                null);

        if(c != null){
            for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
                map.put(c.getLong(0), c.getString(1));
            }
        }
        c.close();
        return map;
    }

    public static ArrayList<Song> getSongList() {
        Log.d(TAG, "try to getSongList() ");
        ArrayList<Song> songs = new ArrayList<>();
        ContentResolver resolver = MyApplication.getContext().getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        Map<Long, String> map = getCoverPathMap();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Song song = new Song();
            song.setId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            song.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            song.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            song.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            song.setUrl(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))); //路径
            song.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            song.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
//            Cursor albumArtCursor = MyApplication.getContext().getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                    new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
//                    MediaStore.Audio.Albums._ID+ "=?",
//                    new String[] {String.valueOf(albumId)},
//                    null);
//            if (albumArtCursor.moveToFirst()) {
//                //Log.d(TAG, "ALBUM_ART=" + cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
//                song.setCover(cursor.getString(1));
//            }
//            albumArtCursor.close();

            song.setCover(map.get(albumId));
            songs.add(song);
        }
        cursor.close();
        Log.d(TAG, "DETECTED Song " + songs.size());
        return songs;
    }

    public static ArrayList<Album> getAlbums() {
        Log.d(TAG, "try to getAlbums()");
        ArrayList<Album> albums = new ArrayList<>();

        ContentResolver resolver = MyApplication.getContext().getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null,
                null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Album album = new Album();
            album.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
            album.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
            album.setAlbumCover(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
            albums.add(album);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d(TAG, "DETECTED Albums " + albums.size());
        return albums;
    }

    public static ArrayList<Artist> getArtists(){
        Log.d(TAG, "try to getArtists()");
        ArrayList<Artist> artists = new ArrayList<>();
        ContentResolver resolver = MyApplication.getContext().getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null,
                null, null, MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            Artist artist = new Artist();
            artist.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
            artist.setAlbumCount(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));
            artist.setSongCount(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)));
            artists.add(artist);
        }
        cursor.close();
        Log.d(TAG, "DETECTED Artist " + artists.size());
        return artists;
    }

    public static ArrayList<Song> querySongsInAlbum(String albumName){
        Log.d(TAG, "Query Songs In an Album");
        ArrayList<Song> songs = new ArrayList<>();
        ContentResolver resolver = MyApplication.getContext().getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                android.provider.MediaStore.Audio.Media.ALBUM + "=?",
                new String[] {albumName},
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        Map<Long, String> map = getCoverPathMap();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Song song = new Song();
            song.setId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            song.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            song.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            song.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            song.setUrl(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))); //路径
            song.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            song.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            song.setCover(map.get(albumId));
            songs.add(song);
        }
        cursor.close();
        return songs;
    }

    public static ArrayList<Song> querySongsOfArtist(String artistName){
        Log.d(TAG, "Query Songs Of an Artist");
        ArrayList<Song> songs = new ArrayList<Song>();
        ContentResolver resolver = MyApplication.getContext().getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.ARTIST + "=?",
                new String[] {artistName},
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        Map<Long, String> map = getCoverPathMap();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Song song = new Song();
            song.setId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            song.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            song.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            song.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            song.setUrl(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))); //路径
            song.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            song.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            song.setCover(map.get(albumId));
            songs.add(song);
        }
        cursor.close();
        return  songs;
    }
}

