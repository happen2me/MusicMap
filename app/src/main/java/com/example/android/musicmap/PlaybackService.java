package com.example.android.musicmap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;

public class PlaybackService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener{

    private final String TAG = "PlaybackService";
    private static final int NOTIFY_ID = 1;
    private MediaPlayer mPlayer;
    private ArrayList<Song> mSongs;
    private int mSongPos;
    private final IBinder musicBind = new MusicBinder();

    private boolean shuffle = false;
    private Random rand;

    public PlaybackService() {
    }

    public void setSongs(ArrayList<Song> songs) {
        mSongs = songs;
    }

    public void setShuffle(){
        if(shuffle)
            shuffle = false;
        else
            shuffle = true;
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mSongPos = 0;
        rand = new Random();
        AudioManager audioManager = (AudioManager) MyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        initMusicPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return musicBind;
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Called when all clients have disconnected from a particular interface
     * published by the service.  The default implementation does nothing and
     * returns false.
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return true if you would like to have the service's
     * {@link #onRebind} method later called when new clients bind to it.
     */
    @Override
    public boolean onUnbind(Intent intent) { //release resources when the Service instance is unbound
        mPlayer.stop();
        mPlayer.release();
        return false;
    }

    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mPlayer.getCurrentPosition() > 0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayer.start();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                //TODO: Modify icon
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(mSongs.get(mSongPos).getTitle())
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(mSongs.get(mSongPos).getTitle());
        Notification notification = builder.build();
        startForeground(NOTIFY_ID, notification);
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    public void initMusicPlayer(){ //set player properties
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); //
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    /**
     * Called on the listener to notify it the audio focus for this listener has been changed.
     * The focusChange value indicates whether the focus was gained,
     * whether the focus was lost, and whether that loss is transient, or whether the new focus
     * holder will hold it for an unknown amount of time.
     * When losing focus, listeners can use the focus change information to decide what
     * behavior to adopt when losing focus. A music player could for instance elect to lower
     * the volume of its music stream (duck) for transient focus losses, and pause otherwise.
     *
     * @param focusChange the type of focus change, one of {@link AudioManager#AUDIOFOCUS_GAIN},
     *                    {@link AudioManager#AUDIOFOCUS_LOSS}, {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT}
     *                    and {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK}.
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
        //TODO: complete react
        switch (focusChange){
            case AudioManager.AUDIOFOCUS_LOSS:
                mPlayer.pause();
                break;
            default:
        }
    }

    public class MusicBinder extends Binder{
        public PlaybackService getService(){
            return PlaybackService.this;
        }
    }

    public void playSong(){
        mPlayer.reset();
        Song song = mSongs.get(mSongPos);
        try{
            mPlayer.setDataSource(song.getUrl());
        }
        catch (Exception e){
            Log.e(TAG, "playSong: Error setting data resource", e);
        }
        mPlayer.prepareAsync(); //to prepare
    }

    public void setSongPos(int position){
        mSongPos = position;
    }

    public int getCurrentPos(){
        return mPlayer.getCurrentPosition();
    }

    public int getDur(){
        return mPlayer.getDuration();
    }

    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }

    public void pausePlayer(){
        mPlayer.pause();
    }

    public void seek(int position){
        mPlayer.seekTo(position);
    }

    public void startPlay(){
        mPlayer.start();
    }

    public void playPrev(){
        if(mSongPos > 0){
            mSongPos--;
        }
        playSong();
    }

    public void playNext(){
        //TODO: enhance this method
        if(shuffle){
            int newSong = mSongPos;
            while(newSong==mSongPos){
                newSong=rand.nextInt(mSongs.size());
            }
            mSongPos=newSong;
        }
        else if(mSongPos < mSongs.size()-1){
            mSongPos++;
        }
        playSong();
    }

    public Song getPlayingSong(){
        return mSongs.get(mSongPos);
    }
}

