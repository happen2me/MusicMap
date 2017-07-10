package com.example.android.musicmap;

import android.Manifest;
import android.app.ActionBar;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
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

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Map;

public class MusicMapActivity extends BaseActivity implements OnMapReadyCallback {

    private String TAG = "MusicMapActivity";
    private GoogleMap mMap;
    private List<LocMusicBind> allEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActionBar actionBar = getActionBar();
        Log.d(TAG, "onCreate: ACTIONBAR == null? " + (actionBar==null));
        setTitle("MusicMap");
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
        mMap.setPadding(50, 0, 0, 0);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        Gson gson = new Gson();
        allEntries = DataSupport.findAll(LocMusicBind.class);

        for (LocMusicBind entry : allEntries) {
            // Add a marker in Sydney and move the camera
            Song song = gson.fromJson((String)entry.getSong(), Song.class);
            LatLng sydney = new LatLng(entry.getLatitude(), entry.getLongitude());
//            if(song.getCover() != null)
//                mMap.addMarker(new MarkerOptions().position(sydney).title(song.getTitle()).snippet(song.getArtist()).icon(BitmapDescriptorFactory.fromPath(song.getCover())));
//            else
//                mMap.addMarker(new MarkerOptions().position(sydney).title(song.getTitle()).snippet(song.getArtist()).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_note_shuffle_player)));
            mMap.addMarker(new MarkerOptions().position(sydney).title(song.getTitle()).snippet(song.getArtist()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            Log.d(TAG, "onMapReady: successfully add " +  song.getTitle() + "(" + entry.getLatitude() +  "," + entry.getLongitude() + ")to the map" );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null)
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
                    }
                });

            }
        }

    }
}
