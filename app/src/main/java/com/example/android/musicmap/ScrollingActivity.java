package com.example.android.musicmap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ScrollingActivity extends AppCompatActivity {
    private static final String TAG = "ScrollingActivity";
    private List<LocMusicBind> allEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.all_locations));

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.all_loc_list);
        Log.d(TAG, "onCreate: recyclerView==null? " + (recyclerView==null));
        LinearLayoutManager manage = new LinearLayoutManager(this);
        manage.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manage);
        allEntries = DataSupport.findAll(LocMusicBind.class);
        Log.d(TAG, "onCreate: allEntries has " + allEntries.size());
        recyclerView.setAdapter(new LocationsAdapter(this, allEntries));
    }
}
