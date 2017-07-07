package com.example.android.musicmap;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.musicmap.SongFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Song} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySongRecyclerViewAdapter extends RecyclerView.Adapter<MySongRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private final List<Song> mSongList;
    private final OnListFragmentInteractionListener mListener;

    public MySongRecyclerViewAdapter(Context context, List<Song> songList, OnListFragmentInteractionListener listener) {
        mContext = context;
        mSongList = songList;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_song, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: finish song listener
                //TODO: 随便找位置写的，catch java.io.FileNotFoundException, 替换成默认专辑封面
                Intent intent = new Intent(mContext, PlaybackService.class);
                intent.putExtra("play_list",(ArrayList)mSongList);
                intent.putExtra("play_start_position", holder.getAdapterPosition());
                mContext.startService(intent);
            }
        });
        return holder;
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
