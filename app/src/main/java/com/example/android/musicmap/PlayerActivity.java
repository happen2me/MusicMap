package com.example.android.musicmap;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    private String TAG = "PlayerActivity";
    private MediaBrowserCompat mMediaBrowser;
    private ArrayList<Song> mSongs;
    private int mSongPosition;
    private ImageView mPlayPauseButton;
    private ImageView mNextButton;
    private ImageView mPrevButton;
    private ImageView mAlbumCoverImage;
    private SeekBar mSeekBar;
    private PlaybackService mMusicService;
    private boolean mMusicBound;
    private Intent playIntent;
    //Customize MediaBrowserCompat.ConnectionCallback
    private final MediaBrowserCompat.ConnectionCallback mConnectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    super.onConnected();

                    // Get the token for the MediaSession
                    MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();

                    // Use the token to Create a MediaControllerCompat
                    MediaControllerCompat mediaController =
                            null;
                    try {
                        mediaController = new MediaControllerCompat(PlayerActivity.this, // Context
                                token);
                        //mediaController.registerCallback(controllerCallback);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    // Save the controller
                    MediaControllerCompat.setMediaController(PlayerActivity.this, mediaController);

                    // Finish building the UI
                    buildTransportControls();

                }

                @Override
                public void onConnectionSuspended() {
                    // The Service has crashed. Disable transport controls until it automatically reconnects
                }

                @Override
                public void onConnectionFailed() {
                    // The Service has refused our connection
                }
            };
    MediaControllerCompat.Callback controllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    mAlbumCoverImage.setImageURI(Uri.parse(metadata.getString(MediaMetadataCompat.METADATA_KEY_ART_URI)));
                    //TODO: change seekbar
                    mSeekBar.setMax(Integer.valueOf(metadata.getString(MediaMetadataCompat.METADATA_KEY_DURATION)));
                }

                /**
                 * Override to handle changes in playback state.
                 *
                 * @param state The new playback state of the session
                 */
                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                    //TODO: bind seekbar
                    mSeekBar.setProgress((int)(state.getPosition()));
                    if(state.getState() == PlaybackStateCompat.STATE_PLAYING){
                        mPlayPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
                    }
                    else/* if(state.getState() == PlaybackStateCompat.STATE_PAUSED)*/{
                        mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    }
                }
            };

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlaybackService.MusicBinder binder = (PlaybackService.MusicBinder) service;
            mMusicService = binder.getService();
            if(mSongs == null){
                mSongs = ReadMusic.getSongList();
            }
            mMusicService.setSongs(mSongs);
            mMusicService.setSongPosition(mSongPosition);
            Log.d(TAG, "onServiceConnected: INITIALISE mMusicService");
            mMusicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicBound = false;
        }
    };

    /**Connect to the MediaBrowserService**/
    @Override
    protected void onCreate(Bundle savedInstanceState) { //constructs a MediaBrowserCompat
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
        mSongs = intent.getParcelableArrayListExtra("play_list");
        mSongPosition = intent.getIntExtra("play_start_position", 0); //0 as default value
        // Create MediaBrowserServiceCompat
        mMediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, PlaybackService.class),
                mConnectionCallbacks, //the MediaBrowserCompat.ConnectionCallback that I've defined.
                null); // optional Bundle
    }

    @Override
    protected void onStart() {//connects to the MediaBrowserService
        super.onStart();
        Log.d(TAG, "onStart executed");
        mMediaBrowser.connect();
        Bundle bundle = new Bundle();
        if(playIntent == null){
            playIntent = new Intent(this,PlaybackService.class);
        }
        bindService(playIntent, musicConnection, BIND_AUTO_CREATE);
        startService(playIntent);
        //MediaControllerCompat.getMediaController(PlayerActivity.this).getTransportControls().play();
//        bundle.putParcelableArrayList("songs", (ArrayList<Song>) mSongs);
//        bundle.putInt("song_position", mSongPosition);
//        MediaControllerCompat.getMediaController(PlayerActivity.this).sendCommand("init play list",bundle, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // (see "stay in sync with the MediaSession")
        if (MediaControllerCompat.getMediaController(PlayerActivity.this) != null) {
            MediaControllerCompat.getMediaController(PlayerActivity.this).unregisterCallback(controllerCallback);
        }
        mMediaBrowser.disconnect();
    }

    void buildTransportControls()
    {
        Log.d(TAG, "buildTransportControls executed");

        // Grab the view for the play/pause button
        mPlayPauseButton = (ImageView) findViewById(R.id.play_play_pause);
        mNextButton = (ImageView) findViewById(R.id.play_next);
        mPrevButton = (ImageView) findViewById(R.id.play_prev);
        mAlbumCoverImage = (ImageView) findViewById(R.id.play_page_album_cover);
        mSeekBar = (SeekBar) findViewById(R.id.play_page_seek_bar);



        // Attach a listener to the button
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since this is a play/pause button, you'll need to test the current state
                // and choose the action accordingly

                int pbState = MediaControllerCompat.getMediaController(PlayerActivity.this).getPlaybackState().getState();
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    MediaControllerCompat.getMediaController(PlayerActivity.this).getTransportControls().pause();
                } else {
                    MediaControllerCompat.getMediaController(PlayerActivity.this).getTransportControls().play();
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat.getMediaController(PlayerActivity.this).getTransportControls().skipToPrevious();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat.getMediaController(PlayerActivity.this).getTransportControls().skipToNext();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MediaControllerCompat.getMediaController(PlayerActivity.this).getTransportControls().seekTo(seekBar.getProgress());
            }
        });

        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(PlayerActivity.this);

        // Display the initial state
        MediaMetadataCompat metadata = mediaController.getMetadata();
        PlaybackStateCompat pbState = mediaController.getPlaybackState();
        mAlbumCoverImage.setImageURI(Uri.parse(metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)));
        if(pbState.getState() == PlaybackStateCompat.STATE_PLAYING){
            Log.d(TAG, "buildTransportControls: pbState.getState()=PLAYING");
            mPlayPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
        }else /*if(pbState.getState() == PlaybackStateCompat.STATE_PAUSED)*/{
            Log.d(TAG, "buildTransportControls: pbState.getState()=PAUSED");
            mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }

        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback);
    }

//    private void updateProgress() {
//        if (mLastPlaybackState == null) {
//            return;
//        }
//        long currentPosition = mLastPlaybackState.getPosition();
//        if (mLastPlaybackState.getState() != PlaybackState.STATE_PAUSED) {
//            // Calculate the elapsed time between the last position update and now and unless
//            // paused, we can assume (delta * speed) + current position is approximately the
//            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
//            // on MediaController.
//            long timeDelta = SystemClock.elapsedRealtime() -
//                    mLastPlaybackState.getLastPositionUpdateTime();
//            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
//        }
//        mSeekbar.setProgress((int) currentPosition);
//    }

}
