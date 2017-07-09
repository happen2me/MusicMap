package com.example.android.musicmap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class DetailsActivity extends AppCompatActivity implements SongFragment.OnListFragmentInteractionListener {
    private static final String SONGS = "songs";
    private static final String PLAY_POS = "play_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent serviceIntent = new Intent(getApplicationContext(), PlaybackService.class);
                ArrayList<Song> songs = getIntent().getParcelableArrayListExtra("songs");
                serviceIntent.putParcelableArrayListExtra(SONGS,songs);
                serviceIntent.putExtra(PLAY_POS, new Random().nextInt(songs.size()));
                startService(serviceIntent);
                Intent playerIntent = new Intent(getApplicationContext(), PlayerActivity.class);
                startActivity(playerIntent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getParcelableExtra("album_self") != null){
            Album album = getIntent().getParcelableExtra("album_self");
            float widthDp = getResources().getDisplayMetrics().widthPixels;
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
            lp.height = (int)widthDp;
            appBarLayout.setBackground(Drawable.createFromPath(album.getAlbumCover()));
            setTitle(album.getName());
        }

        if(getIntent().getParcelableExtra("artist_self") != null){
            Artist artist = getIntent().getParcelableExtra("artist_self");
            setTitle(artist.getName());
        }

        ArrayList<Song> songs = getIntent().getParcelableArrayListExtra("songs");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.details_songs, SongFragment.newInstance(1, songs));
        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(Song item) {
    }
}
