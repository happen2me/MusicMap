package com.example.android.musicmap;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.Random;

import android.media.AudioManager;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;

public class PlaybackService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener{

    private final String TAG = "PlaybackService";
    private static final String SONGS = "songs";
    private static final String PLAY_POS = "play_position";
    private static final int NOTIFY_ID = 1;
    private MediaPlayer mPlayer;
    private ArrayList<Song> mSongs;
    private int mSongPos;
    private final IBinder musicBind = new MusicBinder();
    private boolean shuffle = false;
    private Random rand;
    Notification.Builder mBuilder;

    public PlaybackService() {
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mBuilder = new Notification.Builder(this);
        mPlayer = new MediaPlayer();
        rand = new Random();
        AudioManager audioManager = (AudioManager) MyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        initMusicPlayer();
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * {@link Context#startService}, providing the arguments it supplied and a
     * unique integer token representing the start request.  Do not call this method directly.
     * <p>
     * <p>For backwards compatibility, the default implementation calls
     * {@link #onStart} and returns either {@link #START_STICKY}
     * or {@link #START_STICKY_COMPATIBILITY}.
     * <p>
     * <p>If you need your application to run on platform versions prior to API
     * level 5, you can use the following model to handle the older {@link #onStart}
     * callback in that case.  The <code>handleCommand</code> method is implemented by
     * you as appropriate:
     * <p>
     * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
     * start_compatibility}
     * <p>
     * <p class="caution">Note that the system calls this on your
     * service's main thread.  A service's main thread is the same
     * thread where UI operations take place for Activities running in the
     * same process.  You should always avoid stalling the main
     * thread's event loop.  When doing long-running operations,
     * network calls, or heavy disk I/O, you should kick off a new
     * thread, or use {@link AsyncTask}.</p>
     *
     * @param intent  The Intent supplied to {@link Context#startService},
     *                as given.  This may be null if the service is being restarted after
     *                its process has gone away, and it had previously returned anything
     *                except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags   Additional data about this start request.  Currently either
     *                0, {@link #START_FLAG_REDELIVERY}, or {@link #START_FLAG_RETRY}.
     * @param startId A unique integer representing this specific request to
     *                start.  Use with {@link #stopSelfResult(int)}.
     * @return The return value indicates what semantics the system should
     * use for the service's current started state.  It may be one of the
     * constants associated with the {@link #START_CONTINUATION_MASK} bits.
     * @see #stopSelfResult(int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSongs = intent.getParcelableArrayListExtra(SONGS);
        mSongPos = intent.getIntExtra(PLAY_POS, 0);
        //TODO: play in new thread
        playSong();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        return musicBind;
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
    public boolean onUnbind(Intent intent) {
        return false;
    }

    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion");
        if(mPlayer.getCurrentPosition() > 0){
            mp.reset();
            playNext();
        }
        else {
            //TODO: finish last song event
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: MediaPlayer error occurred");
        return false;
    }

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {

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
        mPlayer.stop();
        mPlayer.release();
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

    private void playSong(){
        mPlayer.reset();
        Song song = mSongs.get(mSongPos);
        try{
            mPlayer.setDataSource(song.getUrl());
        }
        catch (Exception e){
            Log.e(TAG, "playSong: Error setting data resource", e);
        }
        mPlayer.prepareAsync(); //to prepare

        showForegroundNotification();
        mPlayer.start();
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
        stopForeground(true);
    }

    public void seek(int position){
        mPlayer.seekTo(position);
    }

    public void startPlay(){
        mPlayer.start();
        showForegroundNotification();
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

    public ArrayList<Song> getSongs(){
        return mSongs;
    }

    public int getSongPos(){
        return mSongPos;
    }

    public Song getPlayingSong(){
        return mSongs.get(mSongPos);
    }

    //TODO: Complete shuffle logic
    public void setShuffle(){
        if(shuffle)
            shuffle = false;
        else
            shuffle = true;
    }

    public boolean isShuffle(){
        return shuffle;
    }

    private void showForegroundNotification(){
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putParcelableArrayListExtra(SONGS, mSongs);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int width = getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
        int height = getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height);

        mBuilder.setContentIntent(pendingIntent)
                //Modify icon
                .setSmallIcon(R.drawable.ic_music_note_white_24dp)
                .setTicker(mSongs.get(mSongPos).getTitle())
                .setOngoing(true)
                .setContentTitle(getPlayingSong().getTitle())
                .setContentText(getPlayingSong().getArtist());
        try {
            mBuilder.setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(getPlayingSong().getCover()), width, height, true));
        }
        catch (NullPointerException e){
            mBuilder.setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_cd), width, height, true));
        }

        Notification notification = mBuilder.build();
        startForeground(NOTIFY_ID, notification);
    }
}

