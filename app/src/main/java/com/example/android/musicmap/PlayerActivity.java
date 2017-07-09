package com.example.android.musicmap;
/*
 * created by 申源春
 *  This activity is designed to display music info and control play
 *  It should be called when click notification and click music piece
 */
import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
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

public class PlayerActivity extends AppCompatActivity{
    private String TAG = "PlayerActivity";
    private static final String SONGS = "songs";
    private static final String PLAY_POS = "play_position";

    private ImageView mPlayPauseButton;
    private ImageView mNextButton;
    private ImageView mPrevButton;
    private ImageView mAlbumCoverImage;
    private SeekBar mSeekBar;

    private int posToPlay;

    private PlaybackService mMusicService;
    private boolean mMusicBound = false;
    private Intent playIntent;
    ArrayList<Song> mSongArrayList;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        unbindService(musicConnection);
        super.onStop();
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlaybackService.MusicBinder binder = (PlaybackService.MusicBinder) service;
            mMusicService = binder.getService();
            mSongArrayList = mMusicService.getSongs();
            posToPlay = mMusicService.getSongPos();
            Log.d(TAG, "onServiceConnected: bounded and get playlist");
            mMusicBound = true;
            //when connected init UI
            updateUI();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicBound = false;
        }
    };

    @Override
    protected void onStart() { //start the Service instance when the Activity instance starts
        super.onStart();
        Log.d(TAG, "onStart");
        if(playIntent == null){
            playIntent = new Intent(this, PlaybackService.class);
        }
        bindService(playIntent, musicConnection, BIND_AUTO_CREATE); //bind to get music info and control
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: STOP SERVICE");
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mPlayPauseButton = (ImageView) findViewById(R.id.play_play_pause);
        mNextButton = (ImageView) findViewById(R.id.play_next);
        mPrevButton = (ImageView) findViewById(R.id.play_prev);
        mAlbumCoverImage = (ImageView) findViewById(R.id.play_page_album_cover);
        mSeekBar = (SeekBar) findViewById(R.id.play_page_seek_bar);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicService.playNext();
                updateUI();
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicService.playPrev();
                updateUI();
            }
        });

        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMusicService.isPlaying()){
                    mMusicService.pausePlayer();
                }
                else {
                    mMusicService.startPlay();
                }
                updatePlayPauseButton();
            }
        });

        //TODO: sync seek bar
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMusicService.seek(seekBar.getProgress());
            }
        });
    }

    private void updateUI(){ //called when change song
        setTitle(mMusicService.getPlayingSong().getTitle());
        if(BitmapFactory.decodeFile(mMusicService.getPlayingSong().getCover()) == null){
            mAlbumCoverImage.setImageResource(R.drawable.default_cd);
        }else {
            mAlbumCoverImage.setImageDrawable(Drawable.createFromPath(mMusicService.getPlayingSong().getCover()));
        }
        mSeekBar.setMax(mMusicService.getDur());
        updatePlayPauseButton();
    }

    private void updatePlayPauseButton(){
        if(mMusicService.isPlaying()){
            mPlayPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
        }
        else {
            mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
    }
}