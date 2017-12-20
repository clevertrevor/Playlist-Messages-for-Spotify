package com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.spartandeveloper.playlistmessagesforspotify.R;
import com.blogspot.spartandeveloper.playlistmessagesforspotify.ui.main.PlaylistAdapter.PlaylistViewHolder;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistViewHolder> {

    private List<PlaylistSimple> playlists;
    private OnPlaylistItemClicked listener;

    interface OnPlaylistItemClicked {
        void onPlaylistItemClicked(String uri);
    }

    @Inject
    public PlaylistAdapter() {
        playlists = new ArrayList<>();
    }

    public void setListener(OnPlaylistItemClicked listener) {
        this.listener = listener;
    }

    public void setPlaylists(List<PlaylistSimple> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlaylistViewHolder holder, int position) {
        final PlaylistSimple item = playlists.get(position);

        holder.playlistNameTv.setText(item.name);
        holder.authorTv.setText(item.owner.display_name);
        String trackStr = item.tracks.total + "  " + holder.itemView.getContext().getString(R.string.songs);
        holder.trackTotalTv.setText(trackStr);

        if (item.images.size() >= 1) {
            Glide.with(holder.itemView.getContext())
                    .load(item.images.get(0).url)
                    .into(holder.playlistArtworkIv);
        }

        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPlaylistItemClicked(item.uri);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_playlist_artwork) ImageView playlistArtworkIv;
        @BindView(R.id.tv_playlist_name) TextView playlistNameTv;
        @BindView(R.id.tv_total_tracks) TextView trackTotalTv;
        @BindView(R.id.tv_playlist_author) TextView authorTv;

        PlaylistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}