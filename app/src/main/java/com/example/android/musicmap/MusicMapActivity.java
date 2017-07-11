package com.example.android.musicmap;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterManager;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MusicMapActivity extends BaseActivity implements OnMapReadyCallback {

    private String TAG = "MusicMapActivity";
    private GoogleMap mMap;
    private List<LocMusicBind> allEntries;
    private ClusterManager<MapItem> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        Log.d(TAG, "onCreate: ACTIONBAR == null? " + (actionBar==null));
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("MusicMap");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(0, 145, 0, 0);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        // Initialize the manager with the context and the map.
        mClusterManager = new ClusterManager<MapItem>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        Gson gson = new Gson();
        allEntries = DataSupport.findAll(LocMusicBind.class);

        for (LocMusicBind entry : allEntries) {
            // Add a marker in Sydney and move the camera
            Song song = gson.fromJson((String)entry.getSong(), Song.class);
            LatLng sydney = new LatLng(entry.getLatitude(), entry.getLongitude());
            if(BitmapFactory.decodeFile(song.getCover()) != null)
                mMap.addMarker(new MarkerOptions().position(sydney).title(song.getTitle()).snippet(song.getArtist()).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(song.getCover(),100,100))));
            else
                mMap.addMarker(new MarkerOptions().position(sydney).title(song.getTitle()).snippet(song.getArtist()).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.blue_note_shuffle_player,100,100))));
            Log.d(TAG, "onMapReady: successfully add " +  song.getTitle() + "(" + entry.getLatitude() +  "," + entry.getLongitude() + ")to the map" );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null)
                            mMap.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 14));
                    }
                });

            }
        }

    }

    private Bitmap resizeMapIcons(String iconPath, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeFile(iconPath);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
    private Bitmap resizeMapIcons(int iconRes,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), iconRes);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.show_all_locations) {
            Log.d(TAG, "onOptionsItemSelected: SHOW ALL LOC");
            Intent intent = new Intent(MusicMapActivity.this, ScrollingActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK) {
                    double lat = data.getDoubleExtra("latitude", 0);
                    double lgn = data.getDoubleExtra("longitude", 0);
                    mMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(new LatLng(lat, lgn), 25));
                    Log.d(TAG, "onActivityResult: get location " + lat + "," + lgn);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
