package com.example.android.musicmap;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.*;

import com.example.android.musicmap.AlbumFragment.OnListFragmentInteractionListener;
import com.example.android.musicmap.Album;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Album} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * Done: Replace the implementation with code for your data type.
 */
public class MyAlbumRecyclerViewAdapter extends RecyclerView.Adapter<MyAlbumRecyclerViewAdapter.ViewHolder> {

    private final List<Album> mAlbumList;
    private final OnListFragmentInteractionListener mListener;


    public MyAlbumRecyclerViewAdapter(List<Album> albums, OnListFragmentInteractionListener listener) {
        mAlbumList = albums;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mAlbumList.get(position);
        Album album = mAlbumList.get(position);
        holder.mAlbumCoverImage.setImageDrawable(Drawable.createFromPath(album.getAlbumCover()));
        holder.mAlbumNameText.setText(album.getName());
        holder.mAlbumArtistText.setText(album.getArtist());

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
        return mAlbumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mAlbumCoverImage;
        public final TextView mAlbumNameText;
        public final TextView mAlbumArtistText;
        public Album mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAlbumCoverImage = (ImageView) view.findViewById(R.id.album_cover_image);
            mAlbumNameText = (TextView) view.findViewById(R.id.album_name);
            mAlbumArtistText = (TextView) view.findViewById(R.id.album_artist);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "MyAlbumRecyclerViewAdapter'";
        }
    }
}
