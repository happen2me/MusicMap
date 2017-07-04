package com.example.android.musicmap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.musicmap.ArtistFragment.OnListFragmentInteractionListener;
import com.example.android.musicmap.Artist;

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

    public MyArtistRecyclerViewAdapter(Context context, List<Artist> artistList, OnListFragmentInteractionListener listener) {
        mContext = context;
        mArtistList = artistList;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_artist, parent, false);
        return new ViewHolder(view);
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
