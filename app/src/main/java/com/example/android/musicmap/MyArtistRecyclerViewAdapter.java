package com.example.android.musicmap;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.musicmap.ArtistFragment.OnListFragmentInteractionListener;
import com.example.android.musicmap.Artist;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Artist} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyArtistRecyclerViewAdapter extends RecyclerView.Adapter<MyArtistRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private final List<Artist> mArtistList;
    private final OnListFragmentInteractionListener mListener;
    private final String TAG = "ArtistRecAdapter";

    public MyArtistRecyclerViewAdapter(Context context, List<Artist> artistList, OnListFragmentInteractionListener listener) {
        mContext = context;
        mArtistList = artistList;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_artist, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mArtistName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ArtistName Click Listener called");
                //TODO: finish artist listener
                Intent intent = new Intent(mContext, DetailsActivity.class);
                int position = holder.getAdapterPosition();
                Artist artist = mArtistList.get(position);
                ArrayList<Song> songs = ReadMusic.querySongsOfArtist(artist.getName());
                intent.putExtra("artist_self", artist);
                intent.putExtra("songs", songs);
                //Toast.makeText(mContext, "DETECTED songs " + songs.size() + " of this artist", Toast.LENGTH_SHORT).show();
                mContext.startActivity(intent);
            }
        });
        holder.mArtistCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ArtistCount Click Listener called");
                //TODO: finish artist listener
                Intent intent = new Intent(mContext, DetailsActivity.class);
                int position = holder.getAdapterPosition();
                Artist artist = mArtistList.get(position);
                ArrayList<Song> songs = ReadMusic.querySongsOfArtist(artist.getName());
                intent.putExtra("artist_self", artist);
                intent.putExtra("songs", songs);
                Log.d(TAG, "DETECTED songs " + songs.size() + " of this artist");
                //Toast.makeText(mContext, "DETECTED songs " + songs.size() + " of this artist", Toast.LENGTH_SHORT).show();
                mContext.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Artist artist = mArtistList.get(position);
        holder.mItem = mArtistList.get(position);
        holder.mArtistName.setText(artist.getName());
        holder.mArtistCount.setText(artist.getAlbumCount() + " " + mContext.getString(R.string.album) + ", " + artist.getSongCount() + " " + mContext.getString(R.string.song));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArtistList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mArtistName;
        public final TextView mArtistCount;
        public Artist mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mArtistName = (TextView) view.findViewById(R.id.artist_name);
            mArtistCount = (TextView) view.findViewById(R.id.artist_count);
        }

        @Override
        public String toString() {
            return super.toString() + " '" +  "MyArtistRecyclerViewAdapter'";
        }
    }
}
