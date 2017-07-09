package com.example.android.musicmap;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.SeekBar;
import com.example.android.musicmap.Song;
import com.example.android.musicmap.MusicController;

public class PlayerActivity extends AppCompatActivity implements  MediaPlayerControl{
    private String TAG = "PlayerActivity";

    private ImageView mPlayPauseButton;
    private ImageView mNextButton;
    private ImageView mPrevButton;
    private ImageView mAlbumCoverImage;
    private SeekBar mSeekBar;

    private int posToPlay;
    //added
    private PlaybackService mMusicService;
    private boolean mMusicBound = false;
    private Intent playIntent;
    ArrayList<Song> mSongArrayList;
    private MusicController controller;

    private boolean paused = false;
    private boolean playbackPaused = false;

    @Override
    protected void onPause() {
        super.onPause();
        mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(paused){
            mPlayPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
            setController();
            paused = false;
        }
    }

    @Override
    protected void onStop() {
        controller.hide();
        mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        super.onStop();
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlaybackService.MusicBinder binder = (PlaybackService.MusicBinder) service;
            mMusicService = binder.getService();
            if(mSongArrayList == null){
                mSongArrayList = ReadMusic.getSongList();
            }
            mMusicService.setSongs(mSongArrayList);
            mMusicService.setSongPos(posToPlay);
            Log.d(TAG, "onServiceConnected: INITIALISE mMusicService if it's null");
            mMusicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicBound = false;
        }
    };

    @Override
    protected void onStart() { //start the Service instance when the Activity instance starts
        super.onStart();
        Log.d(TAG, "onStart: try to start music service");
        if(playIntent == null){
            playIntent = new Intent(this, PlaybackService.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, BIND_AUTO_CREATE); //musicConnection pass musicList
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: STOP SERVICE");
        stopService(playIntent);
        mMusicService = null;
        super.onDestroy();
    }


    //mediaControl
    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public void seekTo(int pos) {
        //if(mMusicService != null && mMusicBound)
        mMusicService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(mMusicService != null && mMusicBound && mMusicService.isPlaying())
            return true;
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public void start() {
        //if(mMusicService != null && mMusicBound)
        mMusicService.startPlay();
        mAlbumCoverImage.setImageDrawable(Drawable.createFromPath(mMusicService.getPlayingSong().getUrl()));
        mSeekBar.setMax(mMusicService.getDur());
        //TODO: check
        if(playbackPaused){
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    @Override
    public void pause() {
        //if(mMusicService != null && mMusicBound)
        mMusicService.pausePlayer();
        playbackPaused = true;
        mMusicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(mMusicService != null && mMusicBound && mMusicService.isPlaying())
            return mMusicService.getDur();
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(mMusicService != null && mMusicBound && mMusicService.isPlaying()){
            return mMusicService.getCurrentPos();
        }
        return mMusicService.getCurrentPos();
    }

    /**
     * Get the audio session id for the player used by this VideoView. This can be used to
     * apply audio effects to the audio track of a video.
     *
     * @return The audio session, or 0 if there was an error.
     */
    @Override
    public int getAudioSessionId() {
        return 0;
    }

    //controllerHelper
    private void setController(){
        controller = new MusicController(this);
        //styleMediaController(controller);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        controller.setMediaPlayer(this);
        //TODO: change anchored layout
        controller.setAnchorView(findViewById(R.id.view_pager));

        Log.d(TAG, "setController: ANCHORVIEW SETTED");
        controller.setEnabled(true);
    }

    private void playNext(){
        mMusicService.playNext();
        mAlbumCoverImage.setImageDrawable(Drawable.createFromPath(mMusicService.getPlayingSong().getUrl()));
        if(playbackPaused){
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    private void playPrev(){
        mMusicService.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    //added end


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mPlayPauseButton = (ImageView) findViewById(R.id.play_play_pause);
        mNextButton = (ImageView) findViewById(R.id.play_next);
        mPrevButton = (ImageView) findViewById(R.id.play_prev);
        mAlbumCoverImage = (ImageView) findViewById(R.id.play_page_album_cover);
        mSeekBar = (SeekBar) findViewById(R.id.play_page_seek_bar);
        Intent intent = getIntent();
        mSongArrayList = intent.getParcelableArrayListExtra("songs");
        posToPlay = intent.getIntExtra("song_position", 0);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        //TODO: finish play pause event
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                seekTo(seekBar.getProgress());
            }
        });

        if(ContextCompat.checkSelfPermission(PlayerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        //add
        setVolumeControlStream(AudioManager.STREAM_MUSIC); //直接控制指定的音频流
        setController();
        //addend

    }

    private void setSongsToDefault(){
        mSongArrayList = ReadMusic.getSongList();
        posToPlay = 0;
    }

    private void  abaddonedPlayNext(){
        mMusicService.setSongPos(posToPlay++);
        mMusicService.playSong();
        controller.show();
    }

    private void abaddondedStop(){
        Log.d(TAG, "onClick stop button: STOP SERVICE");
        stopService(playIntent);
        mMusicService = null;
    }
}
