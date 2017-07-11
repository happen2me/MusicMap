package com.example.android.musicmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by 申源春 on 2017/7/11.
 */

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {
    private static final String TAG = "LocationsAdapter";
    private List<LocMusicBind> mLocMusicBinds;
    private Activity mActivity;
    private static final int RESULT_CODE = 1;
    Gson gson = new Gson();

    public LocationsAdapter(Activity activity, List<LocMusicBind> locations){
        mLocMusicBinds = locations;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.locationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                final LocMusicBind location = mLocMusicBinds.get(position);
                Intent intent = new Intent();
                intent.putExtra("latitude", location.getLatitude());
                intent.putExtra("longitude", location.getLongitude());
                mActivity.setResult(Activity.RESULT_OK, intent);
                Log.d(TAG, "onClick: finish");
                mActivity.finish();
            }
        });
        holder.latlgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                final LocMusicBind location = mLocMusicBinds.get(position);
                Intent intent = new Intent();
                intent.putExtra("latitude", location.getLatitude());
                intent.putExtra("longitude", location.getLongitude());
                mActivity.setResult(RESULT_CODE, intent);
                Log.d(TAG, "onClick: finish");
                mActivity.finish();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocMusicBind location = mLocMusicBinds.get(position);
        holder.latlgn.setText(location.getLatitude() + "," + location.getLongitude());
        holder.title.setText(gson.fromJson(location.getSong(), Song.class).getTitle());
    }

    @Override
    public int getItemCount() {
        return mLocMusicBinds.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View locationView;
        TextView latlgn;
        TextView title;
        public ViewHolder(View view){
            super(view);
            locationView = view;
            latlgn = (TextView)view.findViewById(R.id.location_lat_lgn);
            title = (TextView)view.findViewById(R.id.location_title);
        }
    }
}
