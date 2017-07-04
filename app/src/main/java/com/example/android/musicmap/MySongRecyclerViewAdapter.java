package com.example.android.musicmap;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.musicmap.SongFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Song} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySongRecyclerViewAdapter extends RecyclerView.Adapter<MySongRecyclerViewAdapter.ViewHolder> {

    private final List<Song> mSongList;
    private final OnListFragmentInteractionListener mListener;

    public MySongRecyclerViewAdapter(List<Song> songList, OnListFragmentInteractionListener listener) {
        mSongList = songList;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mSongList.get(position);
        Song song = mSongList.get(position);
        holder.mCoverImage.setImageDrawable(Drawable.createFromPath(song.getCover()));
        holder.mNameText.setText(song.getTitle());
        holder.mArtistText.setText(song.getArtist());

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
        return mSongList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mCoverImage;
        public final TextView mNameText;
        public final TextView mArtistText;
        public Song mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCoverImage = (ImageView) view.findViewById(R.id.song_cover);
            mNameText = (TextView) view.findViewById(R.id.song_name);
            mArtistText = (TextView) view.findViewById(R.id.song_artist);
        }

        @Override
        public String toString() {
            return super.toString() + " '" +  "MySongRecyclerViewAdapter'";
        }
    }
}
