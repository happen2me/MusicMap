package com.example.android.musicmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AlbumFragment.OnListFragmentInteractionListener,
        ArtistFragment.OnListFragmentInteractionListener,
        SongFragment.OnListFragmentInteractionListener{
    private static String TAG = "MainActivity";
    private static final String SONGS = "songs";
    private static final String PLAY_POS = "play_position";
    private static final int CODE_READ_EXTERNAL_STORAGE = 1;
    private static final int CODE_ACCESS_FINE_LOCATION = 2;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolBar;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODE_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    //Init UI
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            Intent serviceIntent = new Intent(getApplicationContext(), PlaybackService.class);
                            ArrayList<Song> songs = ReadMusic.getSongList();
                            serviceIntent.putParcelableArrayListExtra(SONGS,songs);
                            serviceIntent.putExtra(PLAY_POS, new Random().nextInt(songs.size()));
                            startService(serviceIntent);
                            Intent playerIntent = new Intent(getApplicationContext(), PlayerActivity.class);
                            startActivity(playerIntent);
                        }
                    });
                    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    mDrawerLayout.setDrawerListener(toggle);
                    toggle.syncState();
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.setNavigationItemSelectedListener(this);
                    initTabLayout();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.read_media_denied), Toast.LENGTH_SHORT).show();
                    ActivityCollector.finishAll();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case CODE_ACCESS_FINE_LOCATION:{

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODE_READ_EXTERNAL_STORAGE);
        }
        Log.d(TAG, "onCreate: permission ask executed");
        super.onCreate(savedInstanceState);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    //TODO: show dialog to explain why
                }
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE_ACCESS_FINE_LOCATION);
        }


        setVolumeControlStream(AudioManager.STREAM_MUSIC); //直接控制指定的音频流
        setContentView(R.layout.activity_main);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        /*
        Ask for Permission
         */
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Intent serviceIntent = new Intent(getApplicationContext(), PlaybackService.class);
                    ArrayList<Song> songs = ReadMusic.getSongList();
                    serviceIntent.putParcelableArrayListExtra(SONGS,songs);
                    serviceIntent.putExtra(PLAY_POS, new Random().nextInt(songs.size()));
                    startService(serviceIntent);
                    Intent playerIntent = new Intent(getApplicationContext(), PlayerActivity.class);
                    startActivity(playerIntent);
                }
            });
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerLayout.setDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            //初始化tabLayout
            initTabLayout();
        }

        LitePal.getDatabase();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home){
            mDrawerLayout.openDrawer(Gravity.START);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_library) {
            // Handle the camera action
        } else if (id == R.id.nav_playing) {

        } else if (id == R.id.nav_music_map) {
            startActivity(new Intent(this, MusicMapActivity.class));
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initTabLayout(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), this);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    public void onListFragmentInteraction(Album item) {
    }

    @Override
    public void onListFragmentInteraction(Artist item) {
    }

    @Override
    public void onListFragmentInteraction(Song item) {
    }


}
