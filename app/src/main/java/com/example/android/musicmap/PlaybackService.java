package com.example.android.musicmap;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.drm.DrmStore;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlaybackService extends MediaBrowserServiceCompat
        implements AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnCompletionListener{
    private String TAG = "PlaybackService";
    //TODO: check these two data member
    private Context mContext = MyApplication.getContext();

    private MediaPlayer mMediaPlayer;
    private MediaSessionCompat mMediaSessionCompat;
    private PlaybackStateCompat.Builder mStateBuilder;
    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private AudioManager.OnAudioFocusChangeListener afChangeListener = this;
    private ArrayList<Song> mSongs;
    private int mSongPosition;
    private  IBinder mBinder;
    private BroadcastReceiver myNoisyAudioStreamReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if( mMediaPlayer != null && mMediaPlayer.isPlaying() ) {
                mMediaPlayer.pause();
            }
        }
    };
    MediaSessionCompat.Callback callback;

    public PlaybackService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate executed");
        super.onCreate();

        initMediaPlayer();
        Log.d(TAG, "onCreate: initMediaPlayer");

        initMediaSession();
        Log.d(TAG, "onCreate: initMediaSession");

        mBinder = new MusicBinder();

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand executed ");
        // pass the intent received along with mMediaSessionCompat to the MediaButtonReceiver
        MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy executed");
        super.onDestroy();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);
        unregisterReceiver(myNoisyAudioStreamReceiver);
        mMediaSessionCompat.release();
        //NotificationManagerCompat.from(this).cancel(1);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "onBind executed");
        return mBinder;
    }

    /**
     * Called to get the root information for browsing by a particular client.
     * <p>
     * The implementation should verify that the client package has permission
     * to access browse media information before returning the root id; it
     * should return null if the client is not allowed to access this
     * information.
     * </p>
     *
     * @param clientPackageName The package name of the application which is
     *                          requesting access to browse media.
     * @param clientUid         The uid of the application which is requesting access to
     *                          browse media.
     * @param rootHints         An optional bundle of service-specific arguments to send
     *                          to the media browse service when connecting and retrieving the
     *                          root id for browsing, or null if none. The contents of this
     *                          bundle may affect the information returned when browsing.
     * @return The {@link BrowserRoot} for accessing this app's content or null.
     * @see BrowserRoot#EXTRA_RECENT
     * @see BrowserRoot#EXTRA_OFFLINE
     * @see BrowserRoot#EXTRA_SUGGESTED
     */
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        //controls access to the service
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.

        if(TextUtils.equals(clientPackageName, getPackageName())){
            // Returns a root ID, so clients can use onLoadChildren() to retrieve the content hierarchy
            return new BrowserRoot(getString(R.string.app_name), null);
        }
//        if (allowBrowsing(clientPackageName, clientUid)) {
//            return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
//        }
        else {
            // Clients can connect, but since the BrowserRoot is an empty string
            // onLoadChildren will return nothing. This disables the ability to browse for content.
            return new BrowserRoot("", null);
        }
    }

    /**
     * Called to get information about the children of a media item.
     * <p>
     * Implementations must call {@link Result#sendResult result.sendResult}
     * with the list of children. If loading the children will be an expensive
     * operation that should be performed on another thread,
     * {@link Result#detach result.detach} may be called before returning from
     * this function, and then {@link Result#sendResult result.sendResult}
     * called when the loading is complete.
     * </p><p>
     * In case the media item does not have any children, call {@link Result#sendResult}
     * with an empty list. When the given {@code parentId} is invalid, implementations must
     * call {@link Result#sendResult result.sendResult} with {@code null}, which will invoke
     * {@link MediaBrowserCompat.SubscriptionCallback#onError}.
     * </p>
     *
     * @param parentId The id of the parent media item whose children are to be
     *                 queried.
     * @param result   The Result to send the list of children to.
     */
    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        //provides the ability for a client to build and display a menu of the MediaBrowserService's content hierarchy
        //  Browsing not allowed
        if (TextUtils.isEmpty(parentId)) {
            result.sendResult(null);
            return;
        }
        // Assume for example that the music catalog is already loaded/cached.
        //TODO: finish this logic
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        // Check if this is the root menu:
        if (getString(R.string.app_name).equals(parentId)) {

            // build the MediaItem objects for the top level,
            // and put them in the mediaItems list
        } else {

            // examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list
        }
        result.sendResult(mediaItems);
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
        switch( focusChange ) {
            case AudioManager.AUDIOFOCUS_LOSS: {
                if( mMediaPlayer.isPlaying() ) {
                    mMediaPlayer.stop();
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                mMediaPlayer.pause();
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                if( mMediaPlayer != null ) {
                    mMediaPlayer.setVolume(0.3f, 0.3f);
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN: {
                if( mMediaPlayer != null ) {
                    if( !mMediaPlayer.isPlaying() ) {
                        mMediaPlayer.start();
                    }
                    mMediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            }
        }
    }

    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if( mMediaPlayer != null ) {
            mMediaPlayer.release();
        }
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setVolume(1.0f, 1.0f);
    }

    private void initMediaSession(){
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        // Create a MediaSessionCompat
        mMediaSessionCompat = new MediaSessionCompat(mContext, TAG, mediaButtonReceiver, null);

        // MySessionCallback() has methods that handle callbacks from a media controller
        mMediaSessionCompat.setCallback(new
                MediaSessionCompat.Callback() {
                    @Override
                    public void onPlay() {
                        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                        // Request audio focus for playback, this registers the afChangeListener
                        int result = am.requestAudioFocus(afChangeListener,
                                // Use the music stream.
                                AudioManager.STREAM_MUSIC,
                                // Request permanent focus.
                                AudioManager.AUDIOFOCUS_GAIN);

                        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                            // Start the service
                            //startService(new Intent(getApplicationContext(), PlaybackService.class));
                            // Set the session active  (and update metadata and state)
                            mMediaSessionCompat.setActive(true);
                            // start the player (custom call)
                            onPlayFromUri(null, null);
                            mMediaPlayer.start();
                            // Register BECOME_NOISY BroadcastReceiver
                            registerReceiver(myNoisyAudioStreamReceiver, intentFilter);

                            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);

                            // Put the service in the foreground, post notification
                            //startForeground(myPlayerNotification);
                            NotificationCompat.Builder builder = MediaStyleHelper.from(PlaybackService.this, mMediaSessionCompat);
                            if(builder != null){
                                //TODO: check
                                //NotificationManagerCompat.from(PlaybackService.this).notify(1, builder.build());
                                //builder.addAction(R.drawable.ic_pause_black_24dp, getString(R.string.pause), MediaButtonReceiver.buildMediaButtonPendingIntent(service, PlaybackStateCompat.ACTION_PLAY_PAUSE));
                                startForeground(1, builder.build());
                            }
                        }
                    }

                    @Override
                    public void onStop() {
                        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                        // Abandon audio focus
                        am.abandonAudioFocus(afChangeListener);
                        unregisterReceiver(myNoisyAudioStreamReceiver);
                        // Stop the service
                        stopSelf();
                        // Set the session inactive  (and update metadata and state)
                        mMediaSessionCompat.setActive(false);
                        // stop the player (custom call)
                        mMediaPlayer.stop();
                        // Take the service out of the foreground
                        stopForeground(false);
                    }

                    @Override
                    public void onPause() {
                        super.onPause();
                        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                        // Update metadata and state
                        // pause the player (custom call)
                        if(mMediaPlayer.isPlaying()){
                            mMediaPlayer.pause();
                            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
                            //TODO: show pause notification
                        }

                        // unregister BECOME_NOISY BroadcastReceiver
                        unregisterReceiver(myNoisyAudioStreamReceiver);
                        // Take the service out of the foreground, retain the notification
                        stopForeground(false);
                    }



                    /**
                     * Override to handle requests to play a specific media item represented by a URI.
                     *
                     * @param uri
                     * @param extras
                     */
                    @Override
                    public void onPlayFromUri(Uri uri, Bundle extras) {
                        Log.d(TAG, "onPlayFromUri: ");
                        super.onPlayFromUri(uri, extras);
                        Song song = mSongs.get(mSongPosition);
                        buildAndSetMediaMetadata(song);
                        try {
                            mMediaPlayer.reset();
                            mMediaPlayer.setDataSource(song.getUrl());
                            mMediaPlayer.prepareAsync();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            Log.e(TAG, "playSong: Error setting data resource", e);
                        }

                    }

                    /**
                     * Called when a controller has sent a custom command to this session.
                     * The owner of the session may handle custom commands but is not
                     * required to.
                     *
                     * @param command The command name.
                     * @param extras  Optional parameters for the command, may be null.
                     * @param cb      A result receiver to which a result may be sent by the command, may be null.
                     */
                    @Override
                    public void onCommand(String command, Bundle extras, ResultReceiver cb) {
                        super.onCommand(command, extras, cb);
                        mSongs = extras.getParcelableArrayList("songs");
                        mSongPosition = extras.getInt("song_position", 0);
                        updatePlayQueue(mSongs);
                    }

                    /**
                     * Override to handle requests to seek to a specific position in ms.
                     *
                     * @param pos New position to move to, in milliseconds.
                     */
                    @Override
                    public void onSeekTo(long pos) {
                        super.onSeekTo(pos);
                        //TODO: check seekto()
                        mMediaPlayer.seekTo((int)pos);
                    }

                    /**
                     * Override to handle requests to skip to the next media item.
                     */
                    @Override
                    public void onSkipToNext() {
                        super.onSkipToNext();
                        if(mSongPosition < mSongs.size()-1)
                            mSongPosition++;
                        onPlayFromUri(null, null);
                    }

                    /**
                     * Override to handle requests to skip to the previous media item.
                     */
                    @Override
                    public void onSkipToPrevious() {
                        super.onSkipToPrevious();
                        if(mSongPosition > 0)
                            mSongPosition--;
                        onPlayFromUri(null, null);
                    }

                    /**
                     * Override to handle the setting of the shuffle mode.
                     * <p>
                     * You should call {@link #"setShuffleModeEnabled"} before the end of this method in order to
                     * notify the change to the {@link MediaControllerCompat}, or
                     * {@link MediaControllerCompat#isShuffleModeEnabled} could return an invalid value.
                     *
                     * @param enabled true when the shuffle mode is enabled, false otherwise.
                     */
                    @Override
                    public void onSetShuffleModeEnabled(boolean enabled) {
                        super.onSetShuffleModeEnabled(enabled);
                    }
                });

        // Enable callbacks from MediaButtons and TransportControls
        mMediaSessionCompat.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //mMediaSessionCompat.setCallback(this);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSessionCompat.setPlaybackState(mStateBuilder.build());


        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mMediaSessionCompat.setMediaButtonReceiver(pendingIntent);

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mMediaSessionCompat.getSessionToken());
    }



    private void setMediaPlaybackState(int state) {
        //PlaybackStateCompat.Builder playbackstateBuilder = new PlaybackStateCompat.Builder();
        if( state == PlaybackStateCompat.STATE_PLAYING ) {
            mStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE);
        } else {
            mStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY);
        }
        mStateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mMediaSessionCompat.setPlaybackState(mStateBuilder.build());
    }

    private void buildAndSetMediaMetadata(Song song) {
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
//Notification icon in card
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_note_white_24dp));
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, Uri.fromFile(new File(song.getCover())).toString());
//lock screen icon for pre lollipop
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ART_URI, Uri.fromFile(new File(song.getCover())).toString());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.getTitle());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DURATION, String.valueOf(song.getDuration()));
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbum());
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 1);
        mMediaSessionCompat.setMetadata(metadataBuilder.build());
    }

    @NonNull
    private MediaMetadataCompat createMetadataFromSong(Song song){
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
//Notification icon in card
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_note_white_24dp));
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, Uri.fromFile(new File(song.getCover())).toString());
//lock screen icon for pre lollipop
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ART_URI, Uri.fromFile(new File(song.getCover())).toString());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.getTitle());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DURATION, String.valueOf(song.getDuration()));
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbum());
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 1);
        return metadataBuilder.build();
    }

    public void updatePlayQueue(ArrayList<Song> songs){
        List<MediaSessionCompat.QueueItem> queue = new ArrayList<>();
        for(int i = 0; i < songs.size(); i++){
            queue.add(new MediaSessionCompat.QueueItem(createMetadataFromSong(songs.get(i)).getDescription(), UUID.randomUUID().getMostSignificantBits()));
        }
        mMediaSessionCompat.setQueue(queue);
    }

    public class MusicBinder extends Binder {
        public PlaybackService getService(){
            return PlaybackService.this;
        }
    }

    public void setSongs(ArrayList<Song> songs) {
        mSongs = songs;
    }
    public void setSongPosition(int position){
        mSongPosition = position;
    }
}
